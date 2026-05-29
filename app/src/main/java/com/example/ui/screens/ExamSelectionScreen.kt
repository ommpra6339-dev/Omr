package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExamConfig
import com.example.viewmodel.OmrViewModel
import com.example.viewmodel.Screen

@Composable
fun ExamSelectionScreen(viewModel: OmrViewModel) {
    val isBlankMode = viewModel.isBlankOmrMode.collectAsState().value
    var selectedExamId by remember { mutableStateOf(ExamConfig.ALL_EXAMS.first().id) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OMRifyTheme.DarkGradientBrush)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Back row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateBack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = if (isBlankMode) "Blank OMR Selection" else "Select Target Exam",
                color = OMRifyTheme.TextMain,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Which competitive exam paper are you practicing?",
            color = OMRifyTheme.TextMuted,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Grid List of Target Exams
        ExamConfig.ALL_EXAMS.forEach { config ->
            val isSelected = selectedExamId == config.id
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) OMRifyTheme.CardBackground else OMRifyTheme.MatteBlack)
                    .border(
                        1.5.dp,
                        if (isSelected) OMRifyTheme.AccentCyan else OMRifyTheme.BorderColor,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { selectedExamId = config.id }
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = config.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )

                        // Visual markers for marking rules
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(OMRifyTheme.SlateGray)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Correct: +${config.positiveMarks}  Wrong: ${config.negativeMarks}",
                                color = OMRifyTheme.AccentCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = config.description,
                        color = OMRifyTheme.TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        GlowButton(
            text = "Proceed to Next Step",
            onClick = {
                val selectedConfig = ExamConfig.getById(selectedExamId)
                viewModel.selectedExamConfig.value = selectedConfig
                viewModel.questionCount.value = selectedConfig.defaultQuestions
                viewModel.timeLimitMinutes.value = selectedConfig.defaultDurationMinutes
                
                if (isBlankMode) {
                    viewModel.navigateTo(Screen.TestConfig)
                } else {
                    viewModel.navigateTo(Screen.OcrUploadScan)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("exam_proceed_btn")
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}
