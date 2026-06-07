package com.now.features.auth

import androidx.compose.foundation.layout.*
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
fun WelcomeScreen(appState: AppState) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(Modifier.height(24.dp))
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("NOW", fontSize = 56.sp, fontWeight = FontWeight.Black, color = NowColors.ink)
            Text("Meet one real person nearby today.", fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
            Text("No swipe deck. No backlog. One active match, one day, one decision.", color = NowColors.inkSoft)
        }
        Button(onClick = { appState.login() }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            Text("Register / Login")
        }
    }
}
