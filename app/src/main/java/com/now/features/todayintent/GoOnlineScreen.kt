package com.now.features.todayintent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.now.app.AppState
import com.now.core.design.NowColors
import com.now.core.model.Intent
import com.now.core.model.Plan
import com.now.core.model.TimeWindow

@Composable
fun GoOnlineScreen(appState: AppState) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("One person at a time.", fontSize = 34.sp, fontWeight = FontWeight.Bold)
        Text("Go online when you are actually open to meet today. If you match, discovery pauses.", color = NowColors.inkSoft)
        appState.errorMessage?.let { Text(it, color = NowColors.inkSoft, fontSize = 12.sp) }
        PlanPicker(appState)
        IntentPicker(appState)
        TimeWindowPicker(appState)
        Text(
            "One active match only. Meet, cancel, or let today end before starting another.",
            color = NowColors.inkSoft,
            modifier = Modifier.fillMaxWidth().background(androidx.compose.ui.graphics.Color.White, RoundedCornerShape(8.dp)).padding(12.dp)
        )
        Spacer(Modifier.weight(1f))
        Button(
            onClick = { appState.goOnline() },
            enabled = !appState.isLoading,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            if (appState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp).align(Alignment.CenterVertically))
            } else {
                Text("Go Online for today")
            }
        }
    }
}

@Composable
private fun PlanPicker(appState: AppState) {
    ChipGroup("PLAN", Plan.entries, appState.todayIntent.plan) {
        appState.todayIntent = appState.todayIntent.copy(plan = it)
    }
}

@Composable
private fun IntentPicker(appState: AppState) {
    ChipGroup("CONNECTION", Intent.entries, appState.todayIntent.intent) {
        appState.todayIntent = appState.todayIntent.copy(intent = it)
    }
}

@Composable
private fun TimeWindowPicker(appState: AppState) {
    ChipGroup("WHEN TODAY", TimeWindow.entries, appState.todayIntent.timeWindow) {
        appState.todayIntent = appState.todayIntent.copy(timeWindow = it)
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun <T> ChipGroup(title: String, values: List<T>, selected: T, onSelect: (T) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().background(androidx.compose.ui.graphics.Color.White, RoundedCornerShape(8.dp)).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(title, fontWeight = FontWeight.Bold, color = NowColors.inkSoft, fontSize = 12.sp)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            values.forEach { value ->
                val label = when (value) {
                    is Plan -> value.label
                    is Intent -> value.label
                    is TimeWindow -> value.label
                    else -> value.toString()
                }
                FilterChip(selected = value == selected, onClick = { onSelect(value) }, label = { Text(label) })
            }
        }
    }
}
