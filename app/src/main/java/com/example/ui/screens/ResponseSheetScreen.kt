package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.*
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
import com.example.viewmodel.Screen

@Composable
fun ResponseSheetScreen(viewModel: OmrViewModel) {
    val attempt = viewModel.lastAttempt.collectAsState().value ?: return
    val correctKeys = viewModel.correctAnswers.collectAsState().value
    val studentAnswers = viewModel.userAnswers.collectAsState().value
    
    var activeFilter by remember { mutableStateOf("ALL") } // "ALL", "WRONG", "UNATTEMPTED", "CORRECT"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OMRifyTheme.DarkGradientBrush)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Navigation back header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(Screen.Dashboard) },
                modifier = Modifier.testTag("results_back_btn")
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Dashboard", tint = Color.White)
            }
            Text(
                text = "${attempt.examType} Performance Analytics",
                color = OMRifyTheme.TextMain,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                modifier = Modifier.padding(start = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TOP PERFORMANCE SUMMARY BLOCK
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(OMRifyTheme.CardBackground)
                .border(1.dp, OMRifyTheme.BorderColor, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                     text = "EVALUATION SCORECARD",
                     color = OMRifyTheme.AccentCyan,
                     fontWeight = FontWeight.Bold,
                     fontSize = 11.sp,
                     letterSpacing = 0.5.sp
                )
                
                Spacer(modifier = Modifier.height(10.dp))

                // Score metrics display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "${String.format("%.1f", attempt.finalMarks)} / ${attempt.maxMarks}",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Estimated Marks",
                            color = OMRifyTheme.TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${String.format("%.1f", attempt.accuracyPercentage)}%",
                            color = if (attempt.accuracyPercentage >= 70f) OMRifyTheme.BubbleGreen else OMRifyTheme.BubbleYellow,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Accuracy Rate",
                            color = OMRifyTheme.TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Item grid indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ScoreStatPill(icon = Icons.Rounded.CheckCircle, value = "${attempt.correctCount}", label = "Correct", color = OMRifyTheme.BubbleGreen)
                    ScoreStatPill(icon = Icons.Rounded.Cancel, value = "${attempt.wrongCount}", label = "Wrong", color = OMRifyTheme.BubbleRed)
                    ScoreStatPill(icon = Icons.Rounded.Help, value = "${attempt.unattemptedCount}", label = "Blank", color = OMRifyTheme.TextMuted)
                    ScoreStatPill(
                        icon = Icons.Rounded.Timer,
                        value = "${attempt.timeTakenSeconds / 60}m ${attempt.timeTakenSeconds % 60}s",
                        label = "Elapsed",
                        color = OMRifyTheme.AccentCyan
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // FILTER SEGMENT ROW
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RowPillFilter(label = "Show All", isActive = activeFilter == "ALL", count = attempt.totalQuestions, onClick = { activeFilter = "ALL" })
            RowPillFilter(label = "Incorrect", isActive = activeFilter == "WRONG", count = attempt.wrongCount, color = OMRifyTheme.BubbleRed, onClick = { activeFilter = "WRONG" })
            RowPillFilter(label = "Unattempted", isActive = activeFilter == "UNATTEMPTED", count = attempt.unattemptedCount, color = OMRifyTheme.TextMuted, onClick = { activeFilter = "UNATTEMPTED" })
            RowPillFilter(label = "Correct", isActive = activeFilter == "CORRECT", count = attempt.correctCount, color = OMRifyTheme.BubbleGreen, onClick = { activeFilter = "CORRECT" })
        }

        Spacer(modifier = Modifier.height(12.dp))

        // EVALUATED DIGITIZED ROW RESPONSES LIST
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val qList = (1..attempt.totalQuestions).toList().filter { qNum ->
                val userOpt = studentAnswers[qNum]
                val correctOpt = correctKeys[qNum]
                
                when (activeFilter) {
                    "WRONG" -> userOpt != null && userOpt != correctOpt
                    "UNATTEMPTED" -> userOpt == null
                    "CORRECT" -> userOpt != null && userOpt == correctOpt
                    else -> true
                }
            }

            items(qList) { qNum ->
                val uOpt = studentAnswers[qNum]
                val cOpt = correctKeys[qNum] ?: "A"
                val isCorrect = uOpt == cOpt

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when {
                                uOpt == null -> OMRifyTheme.CardBackground
                                isCorrect -> OMRifyTheme.BubbleGreen.copy(alpha = 0.15f)
                                else -> OMRifyTheme.BubbleRed.copy(alpha = 0.15f)
                            }
                        )
                        .border(
                            1.dp,
                            when {
                                uOpt == null -> OMRifyTheme.BorderColor
                                isCorrect -> OMRifyTheme.BubbleGreen.copy(alpha = 0.4f)
                                else -> OMRifyTheme.BubbleRed.copy(alpha = 0.4f)
                            },
                            RoundedCornerShape(12.dp)
                        )
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Label block
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(OMRifyTheme.SlateGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$qNum",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Your Choice: ",
                                color = OMRifyTheme.TextMuted,
                                fontSize = 13.sp
                            )
                            Text(
                                text = uOpt ?: "[ Blank Row ]",
                                color = when {
                                    uOpt == null -> Color.White
                                    isCorrect -> OMRifyTheme.BubbleGreen
                                    else -> OMRifyTheme.BubbleRed
                                },
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Correct Key: ",
                                color = OMRifyTheme.TextMuted,
                                fontSize = 13.sp
                            )
                            Text(
                                text = cOpt,
                                color = OMRifyTheme.BubbleGreen,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Success Indicator check/cross tags
                    Icon(
                        imageVector = when {
                            uOpt == null -> Icons.Rounded.Close
                            isCorrect -> Icons.Rounded.CheckCircle
                            else -> Icons.Rounded.HighlightOff
                        },
                        contentDescription = null,
                        tint = when {
                            uOpt == null -> OMRifyTheme.TextMuted
                            isCorrect -> OMRifyTheme.BubbleGreen
                            else -> OMRifyTheme.BubbleRed
                        },
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // CTA FOOTER ACTIONS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedGlowButton(
                text = "Close Mock Summary",
                onClick = { viewModel.navigateTo(Screen.Dashboard) },
                modifier = Modifier.weight(1f)
            )

            GlowButton(
                text = "Restart Mock Run",
                onClick = {
                    viewModel.startNewTest(viewModel.isBlankOmrMode.value)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ScoreStatPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(OMRifyTheme.SlateGray.copy(alpha = 0.4f))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, color = OMRifyTheme.TextMuted, fontSize = 9.sp)
    }
}

@Composable
fun RowPillFilter(
    label: String,
    isActive: Boolean,
    count: Int,
    color: Color = OMRifyTheme.AccentCyan,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isActive) color else OMRifyTheme.CardBackground)
            .border(1.dp, if (isActive) Color.Transparent else OMRifyTheme.BorderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                color = if (isActive) Color.White else OMRifyTheme.TextMuted,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isActive) Color.White.copy(alpha = 0.2f) else OMRifyTheme.SlateGray)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "$count",
                    color = if (isActive) Color.White else OMRifyTheme.TextMuted,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
        }
    }
}
