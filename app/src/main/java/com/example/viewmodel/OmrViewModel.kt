package com.example.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject

sealed interface Screen {
    object Onboarding : Screen
    object OnboardingDemo : Screen // Landing page & OCR Demo
    object Auth : Screen
    object Dashboard : Screen
    object ExamSelection : Screen
    object OcrUploadScan : Screen
    object OcrPreviewEdit : Screen
    object TestConfig : Screen
    object DigitalOmr : Screen
    object ResponseSheet : Screen
    object Analytics : Screen
    object History : Screen
    object Profile : Screen
}

enum class ActiveTab {
    HOME, TESTS, ANALYTICS, PROFILE
}

class OmrViewModel(private val repository: ExamRepository) : ViewModel() {

    // --- Navigation ---
    val currentScreen = MutableStateFlow<Screen>(Screen.Onboarding)
    val activeTab = MutableStateFlow(ActiveTab.HOME)
    val backStack = mutableListOf<Screen>()

    fun navigateTo(screen: Screen) {
        backStack.add(currentScreen.value)
        currentScreen.value = screen
    }

    fun navigateBack() {
        if (backStack.isNotEmpty()) {
            currentScreen.value = backStack.removeAt(backStack.size - 1)
        } else {
            currentScreen.value = Screen.Dashboard
        }
    }

    // --- Authentication ---
    val isLoggedIn = MutableStateFlow(false)
    val authEmail = MutableStateFlow("")
    val authPassword = MutableStateFlow("")
    val authName = MutableStateFlow("Ommpra")
    val userPrepExam = MutableStateFlow("NEET")
    val streakCount = MutableStateFlow(5) // Starter streak

    fun handleLogin() {
        isLoggedIn.value = true
        currentScreen.value = Screen.Dashboard
    }

    fun handleSignup(name: String, email: String, exam: String) {
        authName.value = name
        authEmail.value = email
        userPrepExam.value = exam
        isLoggedIn.value = true
        currentScreen.value = Screen.Dashboard
    }

    fun handleLogout() {
        isLoggedIn.value = false
        currentScreen.value = Screen.Onboarding
        backStack.clear()
    }

    // --- Selected / Configure Exam Data ---
    val selectedExamConfig = MutableStateFlow(ExamConfig.ALL_EXAMS.first())
    val questionCount = MutableStateFlow(50)
    val timeLimitMinutes = MutableStateFlow(45)
    val selectedMode = MutableStateFlow("Practice") // "Practice" or "Real Exam"
    
    val chapterName = MutableStateFlow("")
    val subjectName = MutableStateFlow("")
    val notesText = MutableStateFlow("")

    // --- OCR & Answer Keys ---
    val correctAnswers = MutableStateFlow<Map<Int, String>>(emptyMap())
    val isOcrRunning = MutableStateFlow(false)
    val isBlankOmrMode = MutableStateFlow(false)
    val selectedSampleDocName = MutableStateFlow<String?>(null)

    // --- Live Test State ---
    val userAnswers = MutableStateFlow<Map<Int, String>>(emptyMap())
    val markedForReview = MutableStateFlow<Set<Int>>(emptySet())
    val currentQuestionIndex = MutableStateFlow(1)
    val elapsedTimeSeconds = MutableStateFlow(0)
    val isTimerRunning = MutableStateFlow(false)
    val isTestSubmitted = MutableStateFlow(false)

    // Active Attempt ID to load on results screen
    val activeAttemptId = MutableStateFlow<Long?>(null)
    val lastAttempt = MutableStateFlow<ExamAttempt?>(null)

    // --- Historical Data Source (Room) ---
    val allAttempts: StateFlow<List<ExamAttempt>> = repository.allAttempts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var timerJob: Job? = null

    // Initialize an exam attempt
    fun startNewTest(blankMode: Boolean) {
        isBlankOmrMode.value = blankMode
        userAnswers.value = emptyMap()
        markedForReview.value = emptySet()
        currentQuestionIndex.value = 1
        elapsedTimeSeconds.value = 0
        isTestSavedAndSubmitted = false
        
        if (blankMode) {
            // Fill correct answers with blank / placeholders or default A for key comparison
            val placeholder = mutableMapOf<Int, String>()
            for (i in 1..questionCount.value) {
                placeholder[i] = "A" // Simulated comparison
            }
            correctAnswers.value = placeholder
        }

        isTimerRunning.value = true
        startTimerEngine()
        navigateTo(Screen.DigitalOmr)
    }

    private fun startTimerEngine() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isTimerRunning.value) {
                delay(1000)
                elapsedTimeSeconds.value++
                
                // Real Exam Mode timeout check
                if (selectedMode.value == "Real Exam") {
                    val maxSecs = timeLimitMinutes.value * 60
                    if (elapsedTimeSeconds.value >= maxSecs) {
                        submitActiveTest()
                    }
                }
            }
        }
    }

    fun togglePauseTimer() {
        isTimerRunning.value = !isTimerRunning.value
        if (isTimerRunning.value) {
            startTimerEngine()
        } else {
            timerJob?.cancel()
        }
    }

    fun selectSampleDocument(sample: OcrEngine.SampleDoc) {
        selectedSampleDocName.value = sample.name
        selectedExamConfig.value = ExamConfig.getById(sample.examType)
        questionCount.value = sample.defaultQuestionCount
        
        // Start simulated OCR extraction
        viewModelScope.launch {
            isOcrRunning.value = true
            delay(2000) // Beautiful cinematic simulation delay
            correctAnswers.value = sample.correctAnswers
            isOcrRunning.value = false
            navigateTo(Screen.OcrPreviewEdit)
        }
    }

    // Trigger local smart scan or Gemini OCR directly
    fun runCustomImageOcr(bitmap: Bitmap?) {
        viewModelScope.launch {
            isOcrRunning.value = true
            
            var apiResult: Map<Int, String>? = null
            if (bitmap != null) {
                apiResult = GeminiOcrService.performOcr(bitmap)
            }
            
            if (apiResult != null && apiResult.isNotEmpty()) {
                correctAnswers.value = apiResult
                questionCount.value = apiResult.keys.maxOrNull() ?: questionCount.value
            } else {
                // Generates extremely realistic layout simulating denoising & tilt correction
                delay(2500)
                val simResult = OcrEngine.generateSimulatedParsedKeys(
                    selectedExamConfig.value.id,
                    questionCount.value
                )
                correctAnswers.value = simResult
            }
            
            isOcrRunning.value = false
            navigateTo(Screen.OcrPreviewEdit)
        }
    }

    fun updateCorrectAnswer(qNum: Int, option: String) {
        val currentKeys = correctAnswers.value.toMutableMap()
        currentKeys[qNum] = option
        correctAnswers.value = currentKeys
    }

    // Update student's OMR selection
    fun setStudentAnswer(qNum: Int, option: String) {
        if (selectedMode.value == "Real Exam" && isTestSubmitted.value) return
        
        val answers = userAnswers.value.toMutableMap()
        if (answers[qNum] == option) {
            answers.remove(qNum) // Tap same answer again to clear! (Tactile interaction)
        } else {
            answers[qNum] = option
        }
        userAnswers.value = answers
    }

    fun toggleMarkForReview(qNum: Int) {
        val marked = markedForReview.value.toMutableSet()
        if (marked.contains(qNum)) {
            marked.remove(qNum)
        } else {
            marked.add(qNum)
        }
        markedForReview.value = marked
    }

    fun clearResponse(qNum: Int) {
        val answers = userAnswers.value.toMutableMap()
        answers.remove(qNum)
        userAnswers.value = answers
        
        val marked = markedForReview.value.toMutableSet()
        marked.remove(qNum)
        markedForReview.value = marked
    }

    private var isTestSavedAndSubmitted = false

    // Evaluates answers and stores in Room
    fun submitActiveTest() {
        if (isTestSavedAndSubmitted) return
        isTestSavedAndSubmitted = true
        isTimerRunning.value = false
        timerJob?.cancel()

        val config = selectedExamConfig.value
        val answers = userAnswers.value
        val keys = correctAnswers.value

        var correct = 0
        var wrong = 0
        var unattempted = 0

        // Traverse correct answer range
        for (qNum in 1..questionCount.value) {
            val userOpt = answers[qNum]
            val correctOpt = keys[qNum]

            if (userOpt == null) {
                unattempted++
            } else if (userOpt == correctOpt) {
                correct++
            } else {
                wrong++
            }
        }

        // Final score calculators
        val finalScore = (correct * config.positiveMarks) + (wrong * config.negativeMarks)
        val maxScore = (questionCount.value * config.positiveMarks).toInt()
        val accuracy = if (correct + wrong > 0) {
            (correct.toFloat() / (correct + wrong)) * 100f
        } else {
            0.0f
        }

        // Construct evaluation JSONs
        val answersJson = JSONObject(answers.mapKeys { it.key.toString() }).toString()
        val correctJson = JSONObject(keys.mapKeys { it.key.toString() }).toString()

        val newAttempt = ExamAttempt(
            examType = config.id,
            totalQuestions = questionCount.value,
            correctCount = correct,
            wrongCount = wrong,
            unattemptedCount = unattempted,
            finalMarks = finalScore,
            maxMarks = maxScore,
            accuracyPercentage = accuracy,
            timeLimitSeconds = timeLimitMinutes.value * 60,
            timeTakenSeconds = elapsedTimeSeconds.value,
            userAnswersJson = answersJson,
            correctAnswersJson = correctJson,
            chapterName = if (chapterName.value.isEmpty()) "General Practice" else chapterName.value,
            subjectName = if (subjectName.value.isEmpty()) config.subjects.firstOrNull() ?: "General" else subjectName.value,
            notes = notesText.value,
            mode = selectedMode.value
        )

        viewModelScope.launch {
            val insertedId = repository.insertAttempt(newAttempt)
            activeAttemptId.value = insertedId
            lastAttempt.value = newAttempt.copy(id = insertedId)
            
            // Add to user streaks if accuracy is constructive (>50%)
            if (accuracy > 50f) {
                streakCount.value++
            }
            
            navigateTo(Screen.ResponseSheet)
        }
    }

    fun showAttemptDetails(attempt: ExamAttempt) {
        lastAttempt.value = attempt
        activeAttemptId.value = attempt.id
        // Load configurations linked.
        val config = ExamConfig.getById(attempt.examType)
        selectedExamConfig.value = config
        questionCount.value = attempt.totalQuestions
        
        // Parse JSON maps saved
        val parsedAnswers = mutableMapOf<Int, String>()
        val parsedCorrect = mutableMapOf<Int, String>()
        
        try {
            val uObj = JSONObject(attempt.userAnswersJson)
            val uKeys = uObj.keys()
            while (uKeys.hasNext()) {
                val key = uKeys.next()
                parsedAnswers[key.toInt()] = uObj.getString(key)
            }
            
            val cObj = JSONObject(attempt.correctAnswersJson)
            val cKeys = cObj.keys()
            while (cKeys.hasNext()) {
                val key = cKeys.next()
                parsedCorrect[key.toInt()] = cObj.getString(key)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        userAnswers.value = parsedAnswers
        correctAnswers.value = parsedCorrect
        navigateTo(Screen.ResponseSheet)
    }

    fun deleteAttemptById(id: Long) {
        viewModelScope.launch {
            repository.deleteAttempt(id)
        }
    }

    fun clearAllUserHistory() {
        viewModelScope.launch {
            repository.clearAll()
            streakCount.value = 0
        }
    }
}
