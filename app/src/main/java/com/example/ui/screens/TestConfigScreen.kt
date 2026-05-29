package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.OmrViewModel

@Composable
fun TestConfigScreen(viewModel: OmrViewModel) {
    val selectedExam = viewModel.selectedExamConfig.collectAsState().value
    val isBlankMode = viewModel.isBlankOmrMode.collectAsState().value
    
    // ViewModel states
    val currentMode = viewModel.selectedMode.collectAsState().value
    val timeLimitMin = viewModel.timeLimitMinutes.collectAsState().value
    val chapter = viewModel.chapterName.collectAsState().value
    val subject = viewModel.subjectName.collectAsState().value
    val notes = viewModel.notesText.collectAsState().value

    // Subject dropdown selection option list
    val subjectsList = selectedExam.subjects

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
        // Back header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateBack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = "${selectedExam.id} Practice Settings",
                color = OMRifyTheme.TextMain,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Configure Mock Parameters",
            color = OMRifyTheme.TextMain,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Fine-tune chapters, subject filters, and timings to begin accurate evaluation.",
            color = OMRifyTheme.TextMuted,
            fontSize = 13.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // CHAPTER DETAILS FORM
        PremiumCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "PRACTICE TOPIC DETAILS",
                color = OMRifyTheme.AccentCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Chapter Name field
            OutlinedTextField(
                value = chapter,
                onValueChange = { viewModel.chapterName.value = it },
                label = { Text("Chapter Name (e.g., Electrostatics)") },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.LightGray,
                    focusedContainerColor = OMRifyTheme.MatteBlack,
                    unfocusedContainerColor = OMRifyTheme.MatteBlack
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("config_chapter_input"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Subject Select scroll-row
            Text(
                text = "Select Segment Subject:",
                color = OMRifyTheme.TextMain,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                subjectsList.forEach { sub ->
                    val isSelected = subject == sub
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) OMRifyTheme.CyanGlowBrush else Brush.linearGradient(listOf(OMRifyTheme.SlateGray, OMRifyTheme.SlateGray)))
                            .clickable { viewModel.subjectName.value = sub }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = sub,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // CHOOSE ATTEMPT TIMING MODES
        PremiumCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "MOCK SIMULATOR MODES",
                color = OMRifyTheme.AccentCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Practice Mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (currentMode == "Practice") OMRifyTheme.SlateGray else OMRifyTheme.MatteBlack)
                        .border(
                            1.dp,
                            if (currentMode == "Practice") OMRifyTheme.AccentCyan else OMRifyTheme.BorderColor,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { viewModel.selectedMode.value = "Practice" }
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "Practice Mode",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Timer persists past limit. Ideal for self-correction & analysis.",
                            color = OMRifyTheme.TextMuted,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }

                // Real Exam Mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (currentMode == "Real Exam") OMRifyTheme.SlateGray else OMRifyTheme.MatteBlack)
                        .border(
                            1.dp,
                            if (currentMode == "Real Exam") OMRifyTheme.AccentCyan else OMRifyTheme.BorderColor,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { viewModel.selectedMode.value = "Real Exam" }
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "Real Exam Mode",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Auto-submits at timeout. Unanswered remain locked.",
                            color = OMRifyTheme.TextMuted,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // TIMER CONFIGURATION SECTION
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Timer,
                        contentDescription = null,
                        tint = OMRifyTheme.AccentCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Countdown duration:",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Text(
                    text = "$timeLimitMin Minutes",
                    color = OMRifyTheme.AccentCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Slider(
                value = timeLimitMin.toFloat(),
                onValueChange = { viewModel.timeLimitMinutes.value = it.toInt() },
                valueRange = 10f..240f,
                steps = 23,
                colors = SliderDefaults.colors(
                    thumbColor = OMRifyTheme.AccentCyan,
                    activeTrackColor = OMRifyTheme.AccentCyan,
                    inactiveTrackColor = OMRifyTheme.SlateGray
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // NOTES FIELD
        OutlinedTextField(
            value = notes,
            onValueChange = { viewModel.notesText.value = it },
            label = { Text("Practice notes & reminders") },
            leadingIcon = { Icon(Icons.Rounded.EditNote, contentDescription = null) },
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.LightGray,
                focusedContainerColor = OMRifyTheme.CardBackground,
                unfocusedContainerColor = OMRifyTheme.CardBackground
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(30.dp))

        GlowButton(
            text = "Launch Digital OMR Sheet",
            onClick = { viewModel.startNewTest(isBlankMode) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("launch_omr_sheet_btn")
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}
