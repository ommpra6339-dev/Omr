package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.ActiveTab
import com.example.viewmodel.OmrViewModel
import com.example.viewmodel.Screen

@Composable
fun ProfileScreen(viewModel: OmrViewModel) {
    val email = viewModel.authEmail.collectAsState().value
    val name = viewModel.authName.collectAsState().value
    val primaryExam = viewModel.userPrepExam.collectAsState().value
    val streak = viewModel.streakCount.collectAsState().value
    val attempts = viewModel.allAttempts.collectAsState().value

    var showClearWarningDialog by remember { mutableStateOf(false) }

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

            // Profile Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(Screen.Dashboard) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Dashboard", tint = Color.White)
                }
                Text(
                    text = "Student Profile Center",
                    color = OMRifyTheme.TextMain,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // AVATAR AND USER INFORMATION
            BlockProfileHeader(name = name, email = email, prep = primaryExam)

            Spacer(modifier = Modifier.height(24.dp))

            // PERFORMANCE INDEX
            Text(
                text = "PRACTICE AND STATS OVERVIEW",
                color = OMRifyTheme.TextMain,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            PremiumCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfilePerformanceRowItem(icon = Icons.Rounded.School, label = "Tests Finished", value = "${attempts.size}")
                    ProfilePerformanceRowItem(icon = Icons.Rounded.Whatshot, label = "Active Streak", value = "$streak Days")
                    ProfilePerformanceRowItem(
                        icon = Icons.Rounded.MilitaryTech,
                        label = "Points XP",
                        value = "${attempts.sumOf { it.correctCount * 10 - it.wrongCount * 2 } + 100}"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // PRESETS & TROUBLESHOOTING ACTIONS
            Text(
                text = "SETTINGS AND MAINTENANCE",
                color = OMRifyTheme.TextMain,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Dynamic Config reset / account controls
            ProfileActionButtonRow(
                title = "Clear Attempt History Database",
                subtitle = "Permanently wipe previous evaluations from Room",
                icon = Icons.Rounded.DeleteSweep,
                color = OMRifyTheme.BubbleRed,
                onClick = { showClearWarningDialog = true }
            )

            Spacer(modifier = Modifier.height(10.dp))

            ProfileActionButtonRow(
                title = "Log Out Scholar Session",
                subtitle = "Clean session instances & exit",
                icon = Icons.Rounded.PowerSettingsNew,
                color = OMRifyTheme.TextMuted,
                onClick = { viewModel.handleLogout() }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // SQLite/Room Database clear warning
    if (showClearWarningDialog) {
        AlertDialog(
            onDismissRequest = { showClearWarningDialog = false },
            containerColor = OMRifyTheme.CardBackground,
            title = {
                Text(
                    text = "Confirm Hard Reset?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to completely erase the database? All previous tests, average logs, accuracy trends, and streak records will be permanently lost. This action is irreversible.",
                    color = OMRifyTheme.TextMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showClearWarningDialog = false
                        viewModel.clearAllUserHistory()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OMRifyTheme.BubbleRed)
                ) {
                    Text("Permanently Erase Everything", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearWarningDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }
}

@Composable
fun BlockProfileHeader(name: String, email: String, prep: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(OMRifyTheme.CardBackground)
            .border(1.dp, OMRifyTheme.BorderColor, RoundedCornerShape(16.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(OMRifyTheme.CyanGlowBrush)
                .padding(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(OMRifyTheme.MatteBlack),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.take(2).uppercase(),
                    color = OMRifyTheme.AccentCyan,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = name,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = if (email.isEmpty()) "student@omrify.in" else email,
            color = OMRifyTheme.TextMuted,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(OMRifyTheme.SlateGray)
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = "Target: $prep Exam",
                color = OMRifyTheme.AccentCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun ProfilePerformanceRowItem(icon: ImageVector, label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(6.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = OMRifyTheme.AccentCyan, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, color = OMRifyTheme.TextMuted, fontSize = 10.sp)
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun ProfileActionButtonRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
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
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = subtitle, color = OMRifyTheme.TextMuted, fontSize = 11.sp, lineHeight = 14.sp)
        }
    }
}
