package com.now.features.meetingmode

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.now.app.AppState
import com.now.core.design.NowColors
import com.now.core.model.MeetingStatus

@Composable
fun MeetingModeScreen(appState: AppState) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("On the way", fontSize = 34.sp, fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier.fillMaxWidth().height(280.dp).background(NowColors.tealPale, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(color = NowColors.teal, radius = 13.dp.toPx(), center = Offset(size.width * 0.27f, size.height * 0.72f))
            }
            Text(
                appState.meetingProposal?.placeName ?: "Meeting point",
                modifier = Modifier.offset(x = 54.dp, y = (-84).dp).background(Color.White, RoundedCornerShape(8.dp)).padding(10.dp),
                fontWeight = FontWeight.Bold
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(MeetingStatus.OnMyWay, MeetingStatus.Arrived, MeetingStatus.Delayed).forEach { status ->
                FilterChip(
                    selected = appState.activeMatch?.meetingStatus == status,
                    onClick = { appState.updateMeetingStatus(status) },
                    label = { Text(status.label) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Button(onClick = { appState.weMet() }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            Text("We met")
        }
        OutlinedButton(onClick = { }, modifier = Modifier.fillMaxWidth().height(48.dp)) {
            Text("Emergency")
        }
    }
}
