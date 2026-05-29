package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.OmrViewModel
import kotlinx.coroutines.launch

@Composable
fun DigitalOmrScreen(viewModel: OmrViewModel) {
    val selectedExam = viewModel.selectedExamConfig.collectAsState().value
    val qCount = viewModel.questionCount.collectAsState().value
    val userAns = viewModel.userAnswers.collectAsState().value
    val markedReview = viewModel.markedForReview.collectAsState().value
    val currIdx = viewModel.currentQuestionIndex.collectAsState().value
    
    val timeLimitSecs = viewModel.timeLimitMinutes.collectAsState().value * 60
    val elapsedSecs = viewModel.elapsedTimeSeconds.collectAsState().value
    val mode = viewModel.selectedMode.collectAsState().value

    // Calculated stats
    val attemptedCount = userAns.size
    val remainingCount = qCount - attemptedCount

    // Timer calculation
    val isRealMode = mode == "Real Exam"
    val remainingSeconds = if (isRealMode) {
        maxOf(0, timeLimitSecs - elapsedSecs)
    } else {
        elapsedSecs // Practice Mode counts up
    }

    val isUrgent = isRealMode && remainingSeconds < 300 // Pulse red in under 5 minutes

    val minutesStr = String.format("%02d", remainingSeconds / 60)
    val secondsStr = String.format("%02d", remainingSeconds % 60)

    var showSubmitConfirmation by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Scroll to active index
    LaunchedEffect(currIdx) {
        if (currIdx in 1..qCount) {
            coroutineScope.launch {
                listState.animateScrollToItem(maxOf(0, currIdx - 2))
            }
        }
    }

    Scaffold(
        containerColor = OMRifyTheme.MatteBlack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(OMRifyTheme.DarkGradientBrush)
                .padding(innerPadding)
        ) {
            
            // STICKY TOP HUD PANEL
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .background(OMRifyTheme.CardBackground)
                    .border(
                        1.5.dp,
                        if (isUrgent) OMRifyTheme.BubbleRed else OMRifyTheme.BorderColor,
                        RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${selectedExam.id} DIGITAL OMR LIVE",
                                color = OMRifyTheme.AccentCyan,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = if (viewModel.chapterName.value.isEmpty()) "General Mock Practice" else viewModel.chapterName.value,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        // Circular or solid Glow Timer
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isUrgent) OMRifyTheme.BubbleRed.copy(alpha = 0.2f) else OMRifyTheme.SlateGray)
                                .border(
                                    1.dp,
                                    if (isUrgent) OMRifyTheme.BubbleRed else OMRifyTheme.AccentCyan,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "$minutesStr:$secondsStr",
                                color = if (isUrgent) OMRifyTheme.BubbleRed else OMRifyTheme.AccentCyan,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Metric Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ProgressIndicatorDetail(value = "$attemptedCount", label = "Attempted", color = OMRifyTheme.BubbleGreen)
                        ProgressIndicatorDetail(value = "$remainingCount", label = "Remaining", color = OMRifyTheme.TextMuted)
                        ProgressIndicatorDetail(value = "$qCount", label = "Total Items", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val progressRatio = if (qCount > 0) attemptedCount.toFloat() / qCount else 0f
                    LinearProgressIndicator(
                        progress = progressRatio,
                        color = OMRifyTheme.BubbleGreen,
                        trackColor = OMRifyTheme.SlateGray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // THE REALISTIC OMR BUBBLE LIST
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val questionsRange = (1..qCount).toList()
                items(questionsRange) { qNum ->
                    val selectedOption = userAns[qNum]
                    val isReviewed = markedReview.contains(qNum)
                    val isCurrent = currIdx == qNum

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isCurrent) OMRifyTheme.SlateGray.copy(alpha = 0.5f) else OMRifyTheme.CardBackground)
                            .border(
                                1.dp,
                                when {
                                    isCurrent -> OMRifyTheme.AccentCyan
                                    isReviewed -> OMRifyTheme.BubbleYellow
                                    selectedOption != null -> OMRifyTheme.BubbleGreen.copy(alpha = 0.5f)
                                    else -> OMRifyTheme.BorderColor
                                },
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { viewModel.currentQuestionIndex.value = qNum }
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            
                            // Question Number segment
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isReviewed -> OMRifyTheme.BubbleYellow
                                                selectedOption != null -> OMRifyTheme.BubbleGreen
                                                else -> OMRifyTheme.BorderColor
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$qNum",
                                        color = if (isReviewed || selectedOption != null) Color.White else OMRifyTheme.TextMuted,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                                
                                Spacer(modifier = Modifier.width(10.dp))
                                
                                if (isReviewed) {
                                    Text(
                                        text = "Review",
                                        color = OMRifyTheme.BubbleYellow,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            // A, B, C, D Tactile Bubbles row
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                listOf("A", "B", "C", "D").forEach { opt ->
                                    val isFilled = selectedOption == opt
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isFilled) {
                                                    if (isReviewed) OMRifyTheme.BubbleYellow else OMRifyTheme.BubbleGreen
                                                } else OMRifyTheme.MatteBlack
                                            )
                                            .border(
                                                1.5.dp,
                                                if (isFilled) Color.Transparent else OMRifyTheme.BorderColor,
                                                CircleShape
                                            )
                                            .clickable {
                                                viewModel.setStudentAnswer(qNum, opt)
                                                // Auto advance selection row elegantly
                                                if (qNum < qCount && selectedOption != opt) {
                                                    viewModel.currentQuestionIndex.value = qNum + 1
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = opt,
                                            color = if (isFilled) Color.White else OMRifyTheme.TextMuted,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // FLOATING PANEL CONTROLLER (Mark for Review / Clear / Navigation)
            val hasActiveIndex = currIdx in 1..qCount
            if (hasActiveIndex) {
                val isReviewedCurrent = markedReview.contains(currIdx)
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(OMRifyTheme.CardBackground)
                        .border(1.dp, OMRifyTheme.BorderColor)
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Review button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isReviewedCurrent) OMRifyTheme.BubbleYellow else OMRifyTheme.SlateGray)
                                    .clickable { viewModel.toggleMarkForReview(currIdx) }
                                    .padding(vertical = 10.dp, horizontal = 12.dp)
                            ) {
                                Text(
                                    text = if (isReviewedCurrent) "Reviewed" else "Review row",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }

                            // Clear button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(OMRifyTheme.BorderColor)
                                    .clickable { viewModel.clearResponse(currIdx) }
                                    .padding(vertical = 10.dp, horizontal = 12.dp)
                            ) {
                                Text(
                                    text = "Clear Option",
                                    color = OMRifyTheme.TextMuted,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Jumper icons
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    if (currIdx > 1) {
                                        viewModel.currentQuestionIndex.value = currIdx - 1
                                    }
                                },
                                enabled = currIdx > 1
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Prev row", tint = if (currIdx > 1) Color.White else Color.DarkGray)
                            }

                            Text(
                                text = "Row $currIdx",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )

                            IconButton(
                                onClick = {
                                    if (currIdx < qCount) {
                                        viewModel.currentQuestionIndex.value = currIdx + 1
                                    }
                                },
                                enabled = currIdx < qCount
                            ) {
                                Icon(Icons.Default.ArrowForward, contentDescription = "Next row", tint = if (currIdx < qCount) Color.White else Color.DarkGray)
                            }
                        }
                    }
                }
            }

            // FINAL PROGRESS SUBMIT BUTTON BLOCK
            Box(
                modifier = Modifier
                    .fillModifierWithBottomNavPadding()
                    .background(OMRifyTheme.MatteBlack)
                    .border(1.dp, OMRifyTheme.BorderColor)
                    .padding(16.dp)
            ) {
                GlowButton(
                    text = "SUBMIT OMR FOR EVALUATION",
                    onClick = { showSubmitConfirmation = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_test_sheet_btn")
                )
            }
        }
    }

    // ACCIDENTAL SUBMIT PREVENTION MODAL
    if (showSubmitConfirmation) {
        AlertDialog(
            onDismissRequest = { showSubmitConfirmation = false },
            containerColor = OMRifyTheme.CardBackground,
            icon = { Icon(Icons.Default.Info, contentDescription = null, tint = OMRifyTheme.AccentCyan) },
            title = {
                Text(
                    text = "Submit OMR Sheet?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to finish and submit? You have filled $attemptedCount answers, leaving $remainingCount items blank. This will instantly calculate positive/negative marks according to ${selectedExam.id} patterns.",
                    color = OMRifyTheme.TextMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitConfirmation = false
                        viewModel.submitActiveTest()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OMRifyTheme.BubbleGreen)
                ) {
                    Text("Evaluate Instantly", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitConfirmation = false }) {
                    Text("Go Back & Review", color = OMRifyTheme.TextMuted)
                }
            }
        )
    }
}

@Composable
fun ProgressIndicatorDetail(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = color,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = OMRifyTheme.TextMuted,
            fontSize = 10.sp
        )
    }
}

// Helper modifiers to protect bottom padding in insets
@Composable
fun Modifier.fillModifierWithBottomNavPadding(): Modifier {
    return this
        .fillMaxWidth()
        .windowInsetsPadding(WindowInsets.navigationBars)
}
