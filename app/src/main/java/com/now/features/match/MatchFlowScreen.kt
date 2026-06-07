package com.now.features.match

import androidx.compose.runtime.Composable
import com.now.app.AppState
import com.now.core.model.MeetingProposalStatus
import com.now.features.chat.ChatScreen
import com.now.features.loops.FirstLoopScreen
import com.now.features.meetingmode.MeetingModeScreen
import com.now.features.meetingproposal.MeetingProposalScreen

@Composable
fun MatchFlowScreen(appState: AppState) {
    when {
        appState.meetingProposal?.status == MeetingProposalStatus.Accepted -> MeetingModeScreen(appState)
        appState.meetingProposal != null -> MeetingProposalScreen(appState)
        appState.chatUnlocked -> ChatScreen(appState)
        else -> FirstLoopScreen(appState)
    }
}
