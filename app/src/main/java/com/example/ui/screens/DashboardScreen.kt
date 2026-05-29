package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
fun DashboardScreen(viewModel: OmrViewModel) {
    val attempts = viewModel.allAttempts.collectAsState().value
    val studentName = viewModel.authName.collectAsState().value
    val primaryExam = viewModel.userPrepExam.collectAsState().value
    val activeStreak = viewModel.streakCount.collectAsState().value

    // Calculated counters
    val totalTests = attempts.size
    val avgAccuracy = if (attempts.isNotEmpty()) {
        attempts.map { it.accuracyPercentage }.average().toFloat()
    } else 0f
    
    val previousAttemptScore = if (attempts.isNotEmpty()) {
        attempts.first().finalMarks
    } else 0f

    val maxPrevScore = if (attempts.isNotEmpty()) {
        attempts.first().maxMarks
    } else 100

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

            // Student profile greeting row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Namaste, $studentName \uD83D\uDC4B",
                        color = OMRifyTheme.TextMain,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Preparing for $primaryExam exam",
                        color = OMRifyTheme.TextMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Interactive Streak count indicator
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFFFECE5))
                        .border(1.dp, Color(0xFFFFCCBD), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔥",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$activeStreak Days",
                        color = Color(0xFFC0392B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Scorecard metrics view
            PremiumCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "ACADEMIC SCORECARD",
                    color = OMRifyTheme.AccentCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MiniStatSummary(
                        value = "$totalTests",
                        label = "Total Tests"
                    )
                    MiniStatSummary(
                        value = "${String.format("%.1f", avgAccuracy)}%",
                        label = "Avg Accuracy"
                    )
                    MiniStatSummary(
                        value = "${String.format("%.1f", previousAttemptScore)}/${maxPrevScore}",
                        label = "Last Score"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // PRIMARY ACTIONS
            Text(
                text = "PRACTICE AND EVALUATE",
                color = OMRifyTheme.TextMain,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Primary OCR scan button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(OMRifyTheme.CyanGlowBrush)
                    .clickable { viewModel.navigateTo(Screen.ExamSelection) }
                    .padding(20.dp)
                    .testTag("start_ocr_test_btn")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Scan Book Answer Key",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Take a photo of any PYQ / coaching book answer-key to start a realistic digital mock test in seconds.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            lineHeight = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.QrCodeScanner,
                            contentDescription = "Scan icon",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Secondary dashboard grid actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                DashboardActionCard(
                    title = "Blank OMR Sheet",
                    subtitle = "Evaluate coaching or offline mock papers directly.",
                    icon = Icons.Rounded.Article,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.isBlankOmrMode.value = true
                        viewModel.navigateTo(Screen.ExamSelection)
                    }
                )

                DashboardActionCard(
                    title = "Analytics Dashboard",
                    subtitle = "Discover weak chapters, consistency, & speed curves.",
                    icon = Icons.Rounded.Insights,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.activeTab.value = ActiveTab.ANALYTICS
                        viewModel.navigateTo(Screen.Analytics)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // PREVIOUS ATTEMPTS PREVIEW SUMMARY LIST
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "RECENT ATTEMPTS",
                    color = OMRifyTheme.TextMain,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "See All",
                    color = OMRifyTheme.AccentCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable {
                            viewModel.activeTab.value = ActiveTab.TESTS
                            viewModel.navigateTo(Screen.History)
                        }
                        .padding(6.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (attempts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(OMRifyTheme.CardBackground)
                        .border(1.dp, OMRifyTheme.BorderColor, RoundedCornerShape(12.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "⚡ No Tests Practiced Yet",
                            fontWeight = FontWeight.Bold,
                            color = OMRifyTheme.TextMain,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Scan your first Book key to begin digital evaluation.",
                            fontSize = 12.sp,
                            color = OMRifyTheme.TextMuted
                        )
                    }
                }
            } else {
                // Show latest two attempts
                attempts.take(2).forEach { attempt ->
                    RowAttemptRow(attempt = attempt, onClick = {
                        viewModel.showAttemptDetails(attempt)
                    })
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun MiniStatSummary(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = OMRifyTheme.TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun DashboardActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(OMRifyTheme.CardBackground)
            .border(1.dp, OMRifyTheme.BorderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = OMRifyTheme.AccentCyan,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                color = OMRifyTheme.TextMain,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = OMRifyTheme.TextMuted,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
fun RowAttemptRow(attempt: ExamAttempt, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(OMRifyTheme.CardBackground)
            .border(1.dp, OMRifyTheme.BorderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(OMRifyTheme.SlateGray),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = attempt.examType,
                color = OMRifyTheme.AccentCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = attempt.chapterName,
                color = OMRifyTheme.TextMain,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "${attempt.correctCount} Correct • ${attempt.wrongCount} Wrong",
                color = OMRifyTheme.TextMuted,
                fontSize = 12.sp
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${String.format("%.1f", attempt.finalMarks)} Marks",
                color = OMRifyTheme.AccentCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "${String.format("%.0f", attempt.accuracyPercentage)}% Acc",
                color = OMRifyTheme.BubbleGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}

// PREMIUM BOTTOM NAV BAR
@Composable
fun BottomNavigationBar(activeTab: ActiveTab, onTabSelected: (ActiveTab) -> Unit) {
    NavigationBar(
        containerColor = OMRifyTheme.CardBackground,
        tonalElevation = 8.dp,
        modifier = Modifier
            .background(OMRifyTheme.CardBackground)
            .navigationBarsPadding() // Mandated to protect gesture bar clipping!
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home", fontSize = 11.sp) },
            selected = activeTab == ActiveTab.HOME,
            onClick = { onTabSelected(ActiveTab.HOME) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = OMRifyTheme.MatteBlack,
                selectedTextColor = OMRifyTheme.AccentCyan,
                indicatorColor = OMRifyTheme.AccentCyan,
                unselectedIconColor = OMRifyTheme.TextMuted,
                unselectedTextColor = OMRifyTheme.TextMuted
            ),
            modifier = Modifier.testTag("nav_home_btn")
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.History, contentDescription = "History") },
            label = { Text("Tests", fontSize = 11.sp) },
            selected = activeTab == ActiveTab.TESTS,
            onClick = { onTabSelected(ActiveTab.TESTS) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = OMRifyTheme.MatteBlack,
                selectedTextColor = OMRifyTheme.AccentCyan,
                indicatorColor = OMRifyTheme.AccentCyan,
                unselectedIconColor = OMRifyTheme.TextMuted,
                unselectedTextColor = OMRifyTheme.TextMuted
            ),
            modifier = Modifier.testTag("nav_history_btn")
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Insights, contentDescription = "Analytics") },
            label = { Text("Analytics", fontSize = 11.sp) },
            selected = activeTab == ActiveTab.ANALYTICS,
            onClick = { onTabSelected(ActiveTab.ANALYTICS) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = OMRifyTheme.MatteBlack,
                selectedTextColor = OMRifyTheme.AccentCyan,
                indicatorColor = OMRifyTheme.AccentCyan,
                unselectedIconColor = OMRifyTheme.TextMuted,
                unselectedTextColor = OMRifyTheme.TextMuted
            ),
            modifier = Modifier.testTag("nav_analytics_btn")
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile", fontSize = 11.sp) },
            selected = activeTab == ActiveTab.PROFILE,
            onClick = { onTabSelected(ActiveTab.PROFILE) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = OMRifyTheme.MatteBlack,
                selectedTextColor = OMRifyTheme.AccentCyan,
                indicatorColor = OMRifyTheme.AccentCyan,
                unselectedIconColor = OMRifyTheme.TextMuted,
                unselectedTextColor = OMRifyTheme.TextMuted
            ),
            modifier = Modifier.testTag("nav_profile_btn")
        )
    }
}
