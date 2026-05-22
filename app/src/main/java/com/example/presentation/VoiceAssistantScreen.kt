package com.example.presentation

import android.Manifest
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VoiceAssistantScreen(
    viewModel: VoiceAssistantViewModel = viewModel()
) {
    val isListening by viewModel.isListening.collectAsStateWithLifecycle()
    val recognizedText by viewModel.recognizedText.collectAsStateWithLifecycle()
    val statusText by viewModel.statusText.collectAsStateWithLifecycle()

    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE
        )
    )

    LaunchedEffect(Unit) {
        if (!permissionState.allPermissionsGranted) {
            permissionState.launchMultiplePermissionRequest()
        }
    }

    val infiniteTransition = rememberInfiniteTransition()
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF7FF))
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {}) { 
                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color(0xFF49454F)) 
                }
                Spacer(Modifier.width(4.dp))
                Text("Aura Assistant", fontSize = 20.sp, color = Color(0xFF1D1B20))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFE8DEF8), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.CloudOff, contentDescription = null, tint = Color(0xFF21005D), modifier = Modifier.size(12.dp))
                        Text(
                            "OFFLINE", 
                            fontSize = 10.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = Color(0xFF21005D), 
                            letterSpacing = (-0.5).sp
                        )
                    }
                }
                Spacer(Modifier.width(4.dp))
                IconButton(onClick = {}) { 
                    Icon(Icons.Default.MoreVert, contentDescription = "More options", tint = Color(0xFF49454F)) 
                }
            }
        }

        // Main Content Area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Status Indicator
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier
                        .background(Color(0xFFD0BCFF).copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = if (isListening) Color(0xFF6750A4).copy(alpha = pulseAlpha) else Color(0xFF6750A4),
                                shape = CircleShape
                            )
                    )
                    Text(
                        text = if (isListening) "Listening Mode Active" else if (!permissionState.allPermissionsGranted) "Permissions Required" else "Ready",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF21005D)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "VOICE PROCESSOR V2.4",
                    fontSize = 12.sp,
                    color = Color(0xFF49454F),
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp
                )
            }

            // Recognized Command Display
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "I heard...",
                    fontSize = 18.sp,
                    color = Color(0xFF6750A4).copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = if (recognizedText.isNotEmpty()) "\"${recognizedText}\"" else "\"Select mic to start\"",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Light,
                    color = Color(0xFF1D1B20),
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 40.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                Spacer(Modifier.height(48.dp))
                
                // Visual Feedback Ring
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(256.dp)) {
                    Box(modifier = Modifier.size(256.dp).border(1.dp, Color(0xFFD0BCFF).copy(alpha = 0.1f), CircleShape))
                    Box(modifier = Modifier.size(208.dp).border(2.dp, Color(0xFFD0BCFF).copy(alpha = 0.2f), CircleShape))
                    Box(modifier = Modifier.size(160.dp).border(3.dp, Color(0xFFD0BCFF).copy(alpha = 0.4f), CircleShape))
                    Box(
                        modifier = Modifier
                            .size(128.dp)
                            .background(Brush.linearGradient(listOf(Color(0xFFEADDFF), Color(0xFFD0BCFF))), CircleShape)
                            .shadow(2.dp, CircleShape, clip = false),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(16.dp)
                                .height(48.dp)
                                .background(Color(0xFF6750A4).copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .blur(4.dp)
                        )
                    }
                }
            }

            // Success Notification Card / Action Update
            if (statusText.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFFE8DEF8))
                        .border(1.dp, Color(0xFFD0BCFF), RoundedCornerShape(24.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF6750A4), RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                    }
                    Column {
                        Text("Action Update", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF21005D))
                        Text(statusText, fontSize = 12.sp, color = Color(0xFF49454F))
                    }
                }
            } else {
                Spacer(Modifier.height(80.dp))
            }
            
            Spacer(Modifier.height(16.dp))
        }

        // Bottom Control Bar
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp)
                    .shadow(
                        elevation = 24.dp, 
                        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp), 
                        spotColor = Color.Black.copy(alpha = 0.05f)
                    )
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .background(Color(0xFFF7F2FA))
                    .padding(bottom = 40.dp, top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {}) { 
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color(0xFF49454F)) 
                    }
                    IconButton(onClick = {}) { 
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color(0xFF49454F)) 
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "TAP TO SPEAK",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6750A4),
                    letterSpacing = 2.sp
                )
            }

            Button(
                onClick = {
                    if (permissionState.allPermissionsGranted) {
                        if (isListening) viewModel.stopListening() else viewModel.startListening()
                    } else {
                        permissionState.launchMultiplePermissionRequest()
                    }
                },
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                modifier = Modifier
                    .size(96.dp)
                    .shadow(16.dp, RoundedCornerShape(28.dp), spotColor = Color(0xFF6750A4))
                    .testTag("mic_button"),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFFD0BCFF), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = if (isListening) "Stop Listening" else "Start Listening",
                        modifier = Modifier.size(40.dp),
                        tint = Color(0xFF21005D)
                    )
                }
            }
        }
    }
}
