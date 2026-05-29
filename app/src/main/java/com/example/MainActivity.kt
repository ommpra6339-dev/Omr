package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.ExamRepository
import com.example.ui.screens.*
import com.example.viewmodel.OmrViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase
    private lateinit var repository: ExamRepository
    private lateinit var viewModel: OmrViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Room Database
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "omrify_database"
        )
        .fallbackToDestructiveMigration()
        .build()

        repository = ExamRepository(database.examAttemptDao())
        viewModel = OmrViewModel(repository)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = OMRifyTheme.MatteBlack
                ) {
                    AppNavigationRouter(viewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigationRouter(viewModel: OmrViewModel) {
    val currentScreenState = viewModel.currentScreen.collectAsState().value

    // Animation transition wrap for a beautiful, responsive presentation
    AnimatedContent(
        targetState = currentScreenState,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = "ScreenTransition"
    ) { screen ->
        when (screen) {
            is Screen.Onboarding -> OnboardingScreen(viewModel)
            is Screen.OnboardingDemo -> OnboardingDemoScreen(viewModel)
            is Screen.Auth -> AuthScreen(viewModel)
            is Screen.Dashboard -> DashboardScreen(viewModel)
            is Screen.ExamSelection -> ExamSelectionScreen(viewModel)
            is Screen.OcrUploadScan -> OcrUploadScanScreen(viewModel)
            is Screen.OcrPreviewEdit -> OcrPreviewEditScreen(viewModel)
            is Screen.TestConfig -> TestConfigScreen(viewModel)
            is Screen.DigitalOmr -> DigitalOmrScreen(viewModel)
            is Screen.ResponseSheet -> ResponseSheetScreen(viewModel)
            is Screen.Analytics -> AnalyticsScreen(viewModel)
            is Screen.History -> HistoryScreen(viewModel)
            is Screen.Profile -> ProfileScreen(viewModel)
        }
    }
}

// Minimal customized theme declaration wrapper
@Composable
fun MyApplicationTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = OMRifyTheme.AccentCyan,
            background = OMRifyTheme.MatteBlack,
            surface = OMRifyTheme.CardBackground
        ),
        content = content
    )
}
