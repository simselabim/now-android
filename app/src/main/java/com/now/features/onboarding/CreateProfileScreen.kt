package com.now.features.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.now.app.AppState
import com.now.core.design.NowColors

@Composable
fun CreateProfileScreen(appState: AppState) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text("Create profile", fontSize = 34.sp, fontWeight = FontWeight.Bold)
        Text("Add enough trust for someone to meet you today. Photos, short bio, interests, and an intro loop.", color = NowColors.inkSoft)
        Column(
            modifier = Modifier.fillMaxWidth().background(androidx.compose.ui.graphics.Color.White, RoundedCornerShape(8.dp)).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("At least one photo")
            Text("Short profile")
            Text("Intro loop")
        }
        Spacer(Modifier.weight(1f))
        Button(onClick = { appState.completeProfile() }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            Text("Use mock profile")
        }
    }
}
