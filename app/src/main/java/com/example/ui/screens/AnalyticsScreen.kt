package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.CrisisAlert
import androidx.compose.material.icons.rounded.LegendToggle
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExamAttempt
import com.example.viewmodel.ActiveTab
import com.example.viewmodel.OmrViewModel
import com.example.viewmodel.Screen

@Composable
fun AnalyticsScreen(viewModel: OmrViewModel) {
    val attempts = viewModel.allAttempts.collectAsState().value

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                activeTab = viewModel.activeTab.collectAsState().value,
                onTabSelected = { tab ->
                    viewModel.activeTab.value = tab
                    when (tab) {
                        ActiveTab.HOME -> viewModel.currentScreen.value = Screen.Dashboard
                        ActiveTab.TESTS -> viewModel.currentScreen.value = Screen.History
                        ActiveTab.ANALYTICS -> viewModel.currentScreen.value = Screen.Analytics
                        ActiveTab.PROFILE -> viewModel.currentScreen.value = Screen.Profile
                    }
                }
            )
        },
        containerColor = OMRifyTheme.MatteBlack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(OMRifyTheme.DarkGradientBrush)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(Screen.Dashboard) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Dashboard", tint = Color.White)
                }
                Text(
                    text = "Personal Analytics Hub",
                    color = OMRifyTheme.TextMain,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (attempts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(OMRifyTheme.CardBackground)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Rounded.Analytics,
                            contentDescription = null,
                            tint = OMRifyTheme.TextMuted,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Insufficient Data",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Submit a few digital OMR tests to compile accuracy trends and consistency charts.",
                            color = OMRifyTheme.TextMuted,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                    }
                }
            } else {
                // ACCURACY PROFILE TREND (Canvas generated chart!)
                Text(
                    text = "SCORE AND ACCURACY CURVE",
                    color = OMRifyTheme.TextMain,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                PremiumCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Accuracy History (%)",
                        color = OMRifyTheme.AccentCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Draw high accuracy trend line with custom Canvas dots!
                    val accuracyList = attempts.map { it.accuracyPercentage }.take(5).reversed()
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .background(OMRifyTheme.MatteBlack)
                    ) {
                        val width = size.width
                        val height = size.height

                        // Grid lines
                        drawLine(Color.DarkGray, Offset(0f, height*0.25f), Offset(width, height*0.25f), strokeWidth = 1f)
                        drawLine(Color.DarkGray, Offset(0f, height*0.5f), Offset(width, height*0.5f), strokeWidth = 1f)
                        drawLine(Color.DarkGray, Offset(0f, height*0.75f), Offset(width, height*0.75f), strokeWidth = 1f)

                        if (accuracyList.size > 1) {
                            val segmentWidth = width / (accuracyList.size - 1)
                            val path = Path()

                            accuracyList.forEachIndexed { idx, acc ->
                                // Accuracy is 0 to 100, normalize y
                                val yNormal = height - (acc / 100f * height)
                                val xNormal = idx * segmentWidth

                                if (idx == 0) {
                                    path.moveTo(xNormal, yNormal)
                                } else {
                                    path.lineTo(xNormal, yNormal)
                                }

                                // Dot circles representing attempts
                                drawCircle(
                                    color = OMRifyTheme.AccentCyan,
                                    radius = 12f,
                                    center = Offset(xNormal, yNormal)
                                )
                            }

                            drawPath(
                                path = path,
                                color = OMRifyTheme.AccentCyan,
                                style = Stroke(width = 6f)
                            )
                        } else {
                            // Single dot if only one attempt
                            drawCircle(
                                color = OMRifyTheme.AccentCyan,
                                radius = 16f,
                                center = Offset(width / 2, height / 2)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Attempt progression (earliest ➔ latest)",
                        color = OMRifyTheme.TextMuted,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // TOP WEAK TOPICS / CONSISTENCY METRICS
                Text(
                    text = "EFFICIENCY & CONSISTENCY MAP",
                    color = OMRifyTheme.TextMain,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MiniMetricDisplayBox(
                        title = "Velocity Profiler",
                        subtitle = "Average attempt check rate per question",
                        icon = Icons.Rounded.Speed,
                        metricValue = "${String.format("%.1f", attempts.map { it.timeTakenSeconds.toFloat() / it.totalQuestions }.average())}s",
                        modifier = Modifier.weight(1f)
                    )

                    MiniMetricDisplayBox(
                        title = "Weak Target Focus",
                        subtitle = "Most missed subjects segments",
                        icon = Icons.Rounded.CrisisAlert,
                        metricValue = attempts.groupBy { it.subjectName }
                            .maxByOrNull { ent -> ent.value.map { it.wrongCount }.sum() }
                            ?.key ?: "None",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // WRONG-COUNT WEAK CHAPTER BAR CHART
                PremiumCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Subject-Wise Error Breakdown",
                        color = OMRifyTheme.AccentCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Aggregate wrong count grouped by subject
                    val wrongSubjectMap = attempts.groupBy { it.subjectName }
                        .mapValues { ent -> ent.value.sumOf { it.wrongCount } }
                        .toList()
                        .sortedByDescending { it.second }
                        .take(3)

                    wrongSubjectMap.forEach { (subName, wrongsCount) ->
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = subName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = "$wrongsCount Mistakes", color = OMRifyTheme.BubbleRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))

                            // Draw mistake count bar
                            val normalizedRatio = wrongsCount.toFloat() / maxOf(1, wrongSubjectMap.first().second)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(normalizedRatio)
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(OMRifyTheme.BubbleRed)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun MiniMetricDisplayBox(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    metricValue: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(OMRifyTheme.CardBackground)
            .border(1.dp, OMRifyTheme.BorderColor, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column {
            Icon(imageVector = icon, contentDescription = null, tint = OMRifyTheme.AccentCyan, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, color = OMRifyTheme.TextMain, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = subtitle, color = OMRifyTheme.TextMuted, fontSize = 9.sp, lineHeight = 12.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = metricValue, color = OMRifyTheme.AccentCyan, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
        }
    }
}
