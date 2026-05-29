package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.OmrViewModel
import com.example.viewmodel.Screen

@Composable
fun OcrPreviewEditScreen(viewModel: OmrViewModel) {
    val selectedExam = viewModel.selectedExamConfig.collectAsState().value
    val correctKeys = viewModel.correctAnswers.collectAsState().value
    val qCount = viewModel.questionCount.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OMRifyTheme.DarkGradientBrush)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Back header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateBack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = "Verify OCR Answer Key",
                color = OMRifyTheme.TextMain,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Review extracted answers from your physical page. Tap bubbles below to make manual corrections:",
            color = OMRifyTheme.TextMuted,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Adjust question range counter directly
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(OMRifyTheme.CardBackground)
                .border(1.dp, OMRifyTheme.BorderColor, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Question Range: $qCount",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            if (qCount > 5) {
                                viewModel.questionCount.value = qCount - 5
                                val copy = correctKeys.toMutableMap()
                                copy.remove(qCount)
                                viewModel.correctAnswers.value = copy
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(OMRifyTheme.SlateGray)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = Color.White)
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    IconButton(
                        onClick = {
                            viewModel.questionCount.value = qCount + 5
                            val copy = correctKeys.toMutableMap()
                            if (!copy.containsKey(qCount + 1)) {
                                copy[qCount + 1] = "A"
                            }
                            viewModel.correctAnswers.value = copy
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(OMRifyTheme.SlateGray)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", tint = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grid of Correct Answers
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val validQuestionsRange = (1..qCount).toList()
            items(validQuestionsRange) { qNum ->
                val currentCorrectValue = correctKeys[qNum] ?: "A"
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(OMRifyTheme.CardBackground)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Q. ${String.format("%02d", qNum)}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    // Options A, B, C, D Radio Row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf("A", "B", "C", "D").forEach { opt ->
                            val isSelected = currentCorrectValue == opt
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) OMRifyTheme.AccentCyan else OMRifyTheme.MatteBlack)
                                    .border(
                                        1.dp,
                                        if (isSelected) Color.Transparent else OMRifyTheme.BorderColor,
                                        CircleShape
                                    )
                                    .clickable { viewModel.updateCorrectAnswer(qNum, opt) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = opt,
                                    color = if (isSelected) Color.White else OMRifyTheme.TextMuted,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        GlowButton(
            text = "Proceed to Settings",
            onClick = { viewModel.navigateTo(Screen.TestConfig) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("ocr_preview_proceed_btn")
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}
