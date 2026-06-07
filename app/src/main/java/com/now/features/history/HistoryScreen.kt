package com.now.features.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun HistoryScreen(appState: AppState) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("History", fontSize = 34.sp, fontWeight = FontWeight.Bold)
        Text("Completed meetings live here. Old chats do not reopen.", color = NowColors.inkSoft)
        OutlinedButton(onClick = { appState.closeHistory() }, modifier = Modifier.fillMaxWidth()) {
            Text("Back to today")
        }
        appState.history.forEach { item ->
            Column(
                modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)).padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(item.name, fontWeight = FontWeight.Bold)
                Text(item.detail, color = NowColors.inkSoft)
                Text(item.status, color = NowColors.teal, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
