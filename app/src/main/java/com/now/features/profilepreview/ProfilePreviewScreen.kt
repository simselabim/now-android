package com.now.features.profilepreview

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
fun ProfilePreviewScreen(appState: AppState) {
    val point = appState.selectedPoint ?: return
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        OutlinedButton(onClick = { appState.closeProfilePreview() }) {
            Text("Back to map")
        }
        Column(
            modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(
                    modifier = Modifier.size(84.dp).clip(CircleShape).background(NowColors.coral),
                    contentAlignment = Alignment.Center
                ) {
                    Text(point.profile.name.first().toString(), color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("${point.profile.name}, ${point.profile.age}", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text("${point.profile.distance} away", color = NowColors.inkSoft)
                    Text("${point.profile.plan.label} · ${point.profile.intent.label}", color = NowColors.teal, fontWeight = FontWeight.SemiBold)
                }
            }
            Text(point.profile.prompt)
            Text("You share ${point.profile.sharedInterests.size} interests", fontWeight = FontWeight.Bold)
            Text(point.profile.sharedInterests.joinToString(" · "), color = NowColors.inkSoft)
            Text("Contact details stay off profiles. Meet through NOW first.", color = NowColors.inkSoft, fontSize = 13.sp)
        }
        Button(onClick = { appState.markInterested(point) }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            Text("Interested today")
        }
        OutlinedButton(onClick = { appState.notNow(point) }, modifier = Modifier.fillMaxWidth().height(48.dp)) {
            Text("Not Now")
        }
        OutlinedButton(onClick = { appState.block(point) }, modifier = Modifier.fillMaxWidth().height(48.dp)) {
            Text("Block")
        }
    }
}
