package com.now.features.meetingproposal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.now.app.AppState
import com.now.core.design.NowColors

@Composable
fun MeetingProposalScreen(appState: AppState) {
    val proposal = appState.meetingProposal ?: return
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text("Meeting proposal", fontSize = 34.sp, fontWeight = FontWeight.Bold)
        Text("For safety, confirm place and time through NOW.", color = NowColors.inkSoft)
        Column(
            modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(proposal.placeName, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Today · ${proposal.time}", color = NowColors.inkSoft)
            Text("Public place · mock proposal", color = NowColors.inkSoft, fontSize = 13.sp)
        }
        Button(onClick = { appState.acceptMeetingProposal() }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            Text("Accept meeting")
        }
        OutlinedButton(onClick = { appState.cancelMatch() }, modifier = Modifier.fillMaxWidth().height(48.dp)) {
            Text("Cancel match")
        }
    }
}
