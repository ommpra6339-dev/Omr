package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Define standard premium colors inside custom palette tokens
object OMRifyTheme {
    val MatteBlack = Color(0xFF070A12)
    val SlateGray = Color(0xFF1E2640)
    val DeepNavy = Color(0xFF0B0F19)
    val AccentCyan = Color(0xFF06B6D4)
    val AccentPink = Color(0xFFEC4899)
    val ElectricBlue = Color(0xFF3B82F6)
    val CardBackground = Color(0xFF131A2F)
    val BubbleGreen = Color(0xFF10B981)
    val BubbleRed = Color(0xFFEF4444)
    val BubbleYellow = Color(0xFFF59E0B)
    val BorderColor = Color(0xFF222F54)
    val TextMain = Color(0xFFF8FAFC)
    val TextMuted = Color(0xFF94A3B8)
    
    val CyanGlowBrush = Brush.horizontalGradient(
        listOf(Color(0xFF06B6D4), Color(0xFF3B82F6))
    )
    val DarkGradientBrush = Brush.verticalGradient(
        listOf(MatteBlack, DeepNavy)
    )
}

@Composable
fun GlowButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (enabled) OMRifyTheme.CyanGlowBrush else Brush.linearGradient(listOf(OMRifyTheme.SlateGray, OMRifyTheme.SlateGray)))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun OutlinedGlowButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.5.dp, OMRifyTheme.AccentCyan, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = OMRifyTheme.AccentCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    borderColor: Color = OMRifyTheme.BorderColor,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = OMRifyTheme.CardBackground
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            content()
        }
    }
}
