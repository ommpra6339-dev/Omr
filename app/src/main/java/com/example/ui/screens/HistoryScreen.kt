package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.ActiveTab
import com.example.viewmodel.OmrViewModel
import com.example.viewmodel.Screen

@Composable
fun HistoryScreen(viewModel: OmrViewModel) {
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

            // History header segment
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(Screen.Dashboard) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Dashboard", tint = Color.White)
                }
                Text(
                    text = "Attempted Mock History",
                    color = OMRifyTheme.TextMain,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (attempts.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = OMRifyTheme.TextMuted,
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "History is Empty",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Your completed exam response sheets and calculated scorecards will list chronologically right here.",
                        color = OMRifyTheme.TextMuted,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            } else {
                Text(
                    text = "${attempts.size} EXAMS COMPLETED",
                    color = OMRifyTheme.AccentCyan,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                attempts.forEach { mockRun ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            RowAttemptRow(attempt = mockRun, onClick = {
                                viewModel.showAttemptDetails(mockRun)
                            })
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Swipe / Tap deletion action
                        IconButton(onClick = { viewModel.deleteAttemptById(mockRun.id) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Row",
                                tint = OMRifyTheme.BubbleRed.copy(alpha = 0.7f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
