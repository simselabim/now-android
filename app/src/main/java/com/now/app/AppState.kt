package com.now.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.now.core.model.*
import java.util.UUID

class AppState {
    var isAuthenticated by mutableStateOf(false)
    var isProfileComplete by mutableStateOf(false)
    var isOnline by mutableStateOf(false)
    var todayIntent by mutableStateOf(TodayIntent(Plan.Coffee, Intent.Date, TimeWindow.Evening))
    var mapPoints = mutableStateListOf<MapPoint>().apply { addAll(MockData.mapPoints) }
    var selectedPoint by mutableStateOf<MapPoint?>(null)
    var activeMatch by mutableStateOf<Match?>(null)
    var meetingProposal by mutableStateOf<MeetingProposal?>(null)
    var messages = mutableStateListOf<Message>()
    var history = mutableStateListOf<HistoryItem>().apply { addAll(MockData.history) }
    var showHistory by mutableStateOf(false)

    val visibleMapPoints: List<MapPoint>
        get() = mapPoints.filter { it.state != MapPointState.HiddenToday && it.state != MapPointState.Blocked }

    val chatUnlocked: Boolean
        get() = activeMatch?.myFirstLoopSent == true && activeMatch?.theirFirstLoopReceived == true

    fun login() {
        isAuthenticated = true
    }

    fun completeProfile() {
        isProfileComplete = true
    }

    fun goOnline() {
        showHistory = false
        isOnline = true
    }

    fun goOffline() {
        isOnline = false
    }

    fun viewPoint(point: MapPoint) {
        selectedPoint = point
        if (point.state == MapPointState.Unseen) {
            updatePoint(point.id, MapPointState.Viewed)
        }
    }

    fun closeProfilePreview() {
        selectedPoint = null
    }

    fun markInterested(point: MapPoint) {
        updatePoint(point.id, MapPointState.Interested)
        selectedPoint = null
        if (point.isMutualMock) {
            activeMatch = Match(
                id = UUID.randomUUID().toString(),
                profile = point.profile,
                status = MatchStatus.Active,
                myFirstLoopSent = false,
                theirFirstLoopReceived = false,
                meetingStatus = MeetingStatus.None
            )
            isOnline = false
        }
    }

    fun notNow(point: MapPoint) {
        updatePoint(point.id, MapPointState.HiddenToday)
        selectedPoint = null
    }

    fun block(point: MapPoint) {
        updatePoint(point.id, MapPointState.Blocked)
        selectedPoint = null
    }

    fun sendFirstLoop() {
        activeMatch = activeMatch?.copy(myFirstLoopSent = true, theirFirstLoopReceived = true)
    }

    fun sendMessage(text: String) {
        val trimmed = text.trim()
        if (!chatUnlocked || trimmed.isEmpty()) return
        messages.add(Message(UUID.randomUUID().toString(), MessageSender.Me, trimmed))
    }

    fun createMeetingProposal() {
        val match = activeMatch ?: return
        meetingProposal = MeetingProposal(
            id = UUID.randomUUID().toString(),
            matchId = match.id,
            placeName = "Zest Coffee",
            latitude = -8.6504,
            longitude = 115.1387,
            time = "18:30",
            status = MeetingProposalStatus.Pending
        )
    }

    fun acceptMeetingProposal() {
        meetingProposal = meetingProposal?.copy(status = MeetingProposalStatus.Accepted)
        activeMatch = activeMatch?.copy(meetingStatus = MeetingStatus.OnMyWay)
    }

    fun updateMeetingStatus(status: MeetingStatus) {
        activeMatch = activeMatch?.copy(meetingStatus = status)
    }

    fun weMet() {
        val match = activeMatch ?: return
        history.add(
            0,
            HistoryItem(UUID.randomUUID().toString(), match.profile.name, "${match.profile.plan.label} today", "Awaiting confirmation")
        )
        activeMatch = null
        meetingProposal = null
        messages.clear()
        showHistory = true
        isOnline = false
    }

    fun cancelMatch() {
        activeMatch = null
        meetingProposal = null
        messages.clear()
        showHistory = false
        isOnline = false
    }

    fun closeHistory() {
        showHistory = false
    }

    private fun updatePoint(id: String, state: MapPointState) {
        val index = mapPoints.indexOfFirst { it.id == id }
        if (index == -1) return
        mapPoints[index] = mapPoints[index].copy(state = state)
        if (selectedPoint?.id == id) {
            selectedPoint = selectedPoint?.copy(state = state)
        }
    }
}
