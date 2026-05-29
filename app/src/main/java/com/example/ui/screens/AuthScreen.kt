package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Camera
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.OmrViewModel
import com.example.viewmodel.Screen

@Composable
fun OnboardingScreen(viewModel: OmrViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OMRifyTheme.DarkGradientBrush)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        
        // App Tagline Title
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(OMRifyTheme.CardBackground)
                .border(1.dp, OMRifyTheme.BorderColor, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = OMRifyTheme.AccentCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                     text = "INDIA'S PREMIER EXAM CO-PILOT",
                     color = OMRifyTheme.AccentCyan,
                     fontWeight = FontWeight.SemiBold,
                     fontSize = 11.sp,
                     letterSpacing = 1.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Brand logo
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(OMRifyTheme.CyanGlowBrush)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(OMRifyTheme.MatteBlack),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "OMRify Logo",
                    tint = OMRifyTheme.AccentCyan,
                    modifier = Modifier.size(54.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Hero headline
        Text(
            text = "Turn Any Book Into A\nSmart Digital Test",
            color = OMRifyTheme.TextMain,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 40.sp,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Subheadline
        Text(
            text = "Upload answer keys from your books, attempt realistic digital OMR tests, and get instant exam-style evaluation.",
            color = OMRifyTheme.TextMuted,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
        
        Spacer(modifier = Modifier.height(40.dp))

        // Interlocking metrics preview section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            HeroStatItem(icon = Icons.Rounded.Camera, label = "AI OCR Scan")
            HeroStatItem(icon = Icons.Rounded.Book, label = "7+ Exams")
            HeroStatItem(icon = Icons.Rounded.TrendingUp, label = "Analytics")
        }

        Spacer(modifier = Modifier.height(48.dp))

        GlowButton(
            text = "Start Practicing Now",
            onClick = { viewModel.navigateTo(Screen.OnboardingDemo) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("onboarding_cta_btn")
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedGlowButton(
            text = "Student Login / Signup",
            onClick = { viewModel.navigateTo(Screen.Auth) },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun HeroStatItem(icon: ImageVector, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(90.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(OMRifyTheme.CardBackground)
            .border(1.dp, OMRifyTheme.BorderColor, RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = OMRifyTheme.AccentCyan,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            color = OMRifyTheme.TextMain,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

// LANDING INTERACTIVE DEMO (Shows users exactly how magic OCR checks work!)
@Composable
fun OnboardingDemoScreen(viewModel: OmrViewModel) {
    var step by remember { mutableStateOf(1) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OMRifyTheme.DarkGradientBrush)
            .padding(24.dp)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { viewModel.navigateBack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = "OCR Magic Demo",
                color = OMRifyTheme.TextMain,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Box(Modifier.size(48.dp)) // Spacer to center title
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            if (step == 1) {
                // Step 1: Simulated Camera Capture Book
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(OMRifyTheme.CardBackground)
                        .border(1.dp, OMRifyTheme.BorderColor, RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.DarkGray)
                            .border(2.dp, OMRifyTheme.AccentCyan, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Image mockup
                        Icon(
                            imageVector = Icons.Rounded.Camera,
                            contentDescription = "Capture Key",
                            tint = OMRifyTheme.TextMain,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "[ Simulated NEET PYQ Key Book Page ]",
                            color = OMRifyTheme.TextMuted,
                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp),
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "1. Point camera at Answer Key",
                        fontWeight = FontWeight.Bold,
                        color = OMRifyTheme.TextMain,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Take a quick snapshot of the checklist columns in your reference module or question sheets.",
                        color = OMRifyTheme.TextMuted,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else if (step == 2) {
                // Step 2: Extracting OCR Progress animation
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(OMRifyTheme.CardBackground)
                        .border(1.dp, OMRifyTheme.BorderColor, RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Scanning light
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(OMRifyTheme.MatteBlack)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceEvenly,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("1-A", color = OMRifyTheme.AccentCyan, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Text("2-C", color = OMRifyTheme.AccentCyan, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Text("3-B", color = OMRifyTheme.AccentCyan, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    CircularProgressIndicator(color = OMRifyTheme.AccentCyan)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "2. Smart OCR Pipeline Active",
                        fontWeight = FontWeight.Bold,
                        color = OMRifyTheme.TextMain,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Normalizes perspective tilts, filters background textures, and parses column maps instantly.",
                        color = OMRifyTheme.TextMuted,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Step 3: Editable preview
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(OMRifyTheme.CardBackground)
                        .border(1.dp, OMRifyTheme.BorderColor, RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OcrMockCell(qNum = 1, option = "C")
                        OcrMockCell(qNum = 2, option = "B")
                        OcrMockCell(qNum = 3, option = "A")
                        OcrMockCell(qNum = 4, option = "D")
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = OMRifyTheme.BubbleGreen,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "3. Auto-Evaluated Digital mock Ready",
                        fontWeight = FontWeight.Bold,
                        color = OMRifyTheme.TextMain,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "The parsed digital answer key launches a realistic OMR mock simulator block in mere seconds! No manual checking ever again.",
                        color = OMRifyTheme.TextMuted,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            GlowButton(
                text = if (step == 3) "Start Practicing!" else "Next Step",
                onClick = {
                    if (step < 3) {
                        step++
                    } else {
                        viewModel.navigateTo(Screen.Auth)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedGlowButton(
                text = "Skip To Onboarding Login",
                onClick = { viewModel.navigateTo(Screen.Auth) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun OcrMockCell(qNum: Int, option: String) {
    Column(
        modifier = Modifier
            .border(1.dp, OMRifyTheme.AccentCyan, RoundedCornerShape(8.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Q$qNum", color = OMRifyTheme.TextMuted, fontSize = 11.sp)
        Text(option, color = OMRifyTheme.AccentCyan, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

// REGISTER / AUTH SHEET
@Composable
fun AuthScreen(viewModel: OmrViewModel) {
    var isSignUp by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedExam by remember { mutableStateOf("NEET") }
    
    val examList = listOf("NEET", "JEE", "UPSC", "SSC", "Banking", "CUET", "NDA")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OMRifyTheme.DarkGradientBrush)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { viewModel.navigateBack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = "OMRify Account",
                color = OMRifyTheme.TextMain,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Box(Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = if (isSignUp) "Create Student Account" else "Welcome Back, Scholar",
            color = OMRifyTheme.TextMain,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = if (isSignUp) "Join thousands of Indian competitive exam students" else "Enter credentials to unlock rapid checks",
            color = OMRifyTheme.TextMuted,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Input Blocks
        if (isSignUp) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Student Full Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.LightGray,
                    focusedContainerColor = OMRifyTheme.CardBackground,
                    unfocusedContainerColor = OMRifyTheme.CardBackground
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_name_input"),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.LightGray,
                focusedContainerColor = OMRifyTheme.CardBackground,
                unfocusedContainerColor = OMRifyTheme.CardBackground
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("auth_email_input"),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.LightGray,
                focusedContainerColor = OMRifyTheme.CardBackground,
                unfocusedContainerColor = OMRifyTheme.CardBackground
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (isSignUp) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Target Competitive Exam:",
                color = OMRifyTheme.TextMain,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                examList.forEach { exam ->
                    val isSelected = selectedExam == exam
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) OMRifyTheme.CyanGlowBrush else Brush.linearGradient(listOf(OMRifyTheme.CardBackground, OMRifyTheme.CardBackground)))
                            .border(1.dp, if (isSelected) Color.Transparent else OMRifyTheme.BorderColor, RoundedCornerShape(8.dp))
                            .clickable { selectedExam = exam }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = exam,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        GlowButton(
            text = if (isSignUp) "Register Account" else "Secure Login",
            onClick = {
                if (isSignUp) {
                    viewModel.handleSignup(
                        name = if (name.isEmpty()) "Ommpra" else name,
                        email = if (email.isEmpty()) "ommpra6339@gmail.com" else email,
                        exam = selectedExam
                    )
                } else {
                    viewModel.handleLogin()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("auth_submit_btn")
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (isSignUp) "Already have an account? Login" else "New to OMRify? Sign Up Now",
            color = OMRifyTheme.AccentCyan,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            modifier = Modifier
                .clickable { isSignUp = !isSignUp }
                .padding(10.dp)
        )
    }
}
