package com.now.features.loops

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.now.app.AppState
import com.now.core.design.NowColors

@Composable
fun FirstLoopScreen(appState: AppState) {
    val match = appState.activeMatch ?: return
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text("ONE LIVE MATCH", color = NowColors.teal, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text("Send first loop.", fontSize = 34.sp, fontWeight = FontWeight.Bold)
        Text("Discovery is paused. Both people send a short loop before chat unlocks.", color = NowColors.inkSoft)
        Row(
            modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)).padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(54.dp).clip(CircleShape).background(NowColors.coral),
                contentAlignment = Alignment.Center
            ) {
                Text(match.profile.name.first().toString(), color = Color.White, fontWeight = FontWeight.Bold)
            }
            Column {
                Text("${match.profile.name}, ${match.profile.age}", fontWeight = FontWeight.Bold)
                Text("${match.profile.plan.label} · ${match.profile.intent.label} · ready today", color = NowColors.inkSoft)
            }
        }
        Button(onClick = { appState.sendFirstLoop() }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            Text("Record mock first loop")
        }
        OutlinedButton(onClick = { appState.cancelMatch() }, modifier = Modifier.fillMaxWidth().height(48.dp)) {
            Text("Cancel match")
        }
    }
}
