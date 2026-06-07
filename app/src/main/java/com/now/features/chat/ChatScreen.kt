package com.now.features.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.now.app.AppState
import com.now.core.design.NowColors
import com.now.core.model.MessageSender

@Composable
fun ChatScreen(appState: AppState) {
    var draft by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Temporary chat", fontSize = 34.sp, fontWeight = FontWeight.Bold)
        Text(
            "Chat closes after We Met, cancel, or tonight. Confirm place and time through NOW.",
            color = NowColors.inkSoft,
            modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)).padding(10.dp)
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            appState.messages.forEach { message ->
                Text(
                    message.text,
                    color = if (message.sender == MessageSender.Me) Color.White else NowColors.ink,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (message.sender == MessageSender.Me) NowColors.teal else Color.White, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Write about today's meeting") }
            )
            Button(onClick = {
                appState.sendMessage(draft)
                draft = ""
            }) {
                Text("Send")
            }
        }
        Button(onClick = { appState.createMeetingProposal() }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            Text("Suggest place and time")
        }
    }
}
