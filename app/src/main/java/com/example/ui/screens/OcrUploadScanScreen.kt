package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.DocumentScanner
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OcrEngine
import com.example.viewmodel.OmrViewModel
import com.example.viewmodel.Screen
import kotlinx.coroutines.delay

@Composable
fun OcrUploadScanScreen(viewModel: OmrViewModel) {
    val selectedExam = viewModel.selectedExamConfig.collectAsState().value
    val isOcrRunning = viewModel.isOcrRunning.collectAsState().value
    
    var simStatusText by remember { mutableStateOf("Ready to scan") }
    var simProg by remember { mutableStateOf(0f) }

    // Start simulation progress loops when OCR runs
    LaunchedEffect(isOcrRunning) {
        if (isOcrRunning) {
            simStatusText = "Normalizing grayscale gradients..."
            simProg = 0.25f
            delay(600)
            simStatusText = "Applying Otsu adaptive binarization..."
            simProg = 0.55f
            delay(700)
            simStatusText = "Aligning key edges & correction matrix..."
            simProg = 0.85f
            delay(500)
            simStatusText = "Extracting answer option letters..."
            simProg = 1.0f
        }
    }

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
        // Handover Back
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateBack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = "${selectedExam.id} Answer OCR Capture",
                color = OMRifyTheme.TextMain,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (isOcrRunning) {
            // HOLOGRAPHIC OCR SCAN VIEW
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(OMRifyTheme.CardBackground)
                        .border(2.dp, OMRifyTheme.AccentCyan, RoundedCornerShape(30.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DocumentScanner,
                        contentDescription = "Scanning",
                        tint = OMRifyTheme.AccentCyan,
                        modifier = Modifier.size(72.dp)
                    )
                    
                    // Sliding laser visual effect!
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(OMRifyTheme.AccentCyan)
                            .align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))

                Text(
                    text = "INTELLIGENT OCR PIPELINE",
                    color = OMRifyTheme.AccentCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = simStatusText,
                    color = OMRifyTheme.TextMain,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                LinearProgressIndicator(
                    progress = simProg,
                    color = OMRifyTheme.AccentCyan,
                    trackColor = OMRifyTheme.SlateGray,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        } else {
            // CHOOSE SCAN OR SAMPLE KEYS
            Text(
                text = "Capture Your Printed Answer Key",
                color = OMRifyTheme.TextMain,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Point camera, capture or select a test answer-sheet key. The AI parser extracts rows like '1-A', '2-B' instantly.",
                color = OMRifyTheme.TextMuted,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Simulated HUD Viewfinder box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(OMRifyTheme.CardBackground)
                    .border(1.5.dp, OMRifyTheme.BorderColor, RoundedCornerShape(16.dp))
                    .clickable { viewModel.runCustomImageOcr(null) },
                contentAlignment = Alignment.Center
            ) {
                // Diagonal HUD framing corners
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DocumentScanner,
                        contentDescription = "Launch camera",
                        tint = OMRifyTheme.AccentCyan,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Take Photo of Book Key",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Or tap to load automated OCR simulation",
                        color = OMRifyTheme.TextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // HIGH FIDELITY EXAM SAMPLE PAPERS SELECT
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = null,
                    tint = OMRifyTheme.AccentCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "PRO DEMO: SEAMLESS EXTRACTION",
                    color = OMRifyTheme.AccentCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Select one of OMRify's preloaded exam key models below to verify accurate digital OCR response values instantly:",
                color = OMRifyTheme.TextMuted,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Sample cards filter list matching this selected target exam
            val matches = OcrEngine.SAMPLE_DOCS.filter { it.examType == selectedExam.id }
            
            if (matches.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(OMRifyTheme.CardBackground)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "No sample papers config saved for ${selectedExam.id}. Tap 'Take Photo of Book Key' above to simulate a custom scan key!",
                        color = OMRifyTheme.TextMuted,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                matches.forEach { sampleDoc ->
                    PremiumCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { viewModel.selectSampleDocument(sampleDoc) }
                    ) {
                        Text(
                            text = sampleDoc.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = sampleDoc.description,
                            color = OMRifyTheme.TextMuted,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "⚡ ${sampleDoc.defaultQuestionCount} Questions • Preloaded Answer Key Model",
                            color = OMRifyTheme.AccentCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
