package com.now.app

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.now.core.api.ActiveMatchSnapshot
import com.now.core.api.BackendMapPoint
import com.now.core.api.BackendProfile
import com.now.core.api.BootstrapSnapshot
import com.now.core.api.DiscoveryMapSnapshot
import com.now.core.api.NowBackendApi
import com.now.core.model.*
import java.time.LocalDate
import java.time.Period
import java.util.UUID

class AppState(
    private val backendApi: NowBackendApi? = null
) {
    private val mainHandler = Handler(Looper.getMainLooper())

    var isAuthenticated by mutableStateOf(false)
    var isProfileComplete by mutableStateOf(false)
    var isOnline by mutableStateOf(false)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
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
        runLive(
            block = {
                backendApi!!.login(DEMO_EMAIL, DEMO_PASSWORD)
                val bootstrap = backendApi.bootstrap()
                val detail = if (bootstrap.activeMatch != null) backendApi.getActiveMatchDetail() else null
                bootstrap to detail
            },
            onSuccess = { (bootstrap, detail) ->
                isAuthenticated = true
                applyBootstrap(bootstrap, detail)
            },
            fallback = {
                isAuthenticated = true
            }
        )
    }

    fun completeProfile() {
        isProfileComplete = true
    }

    fun goOnline() {
        val api = backendApi
        if (api == null) {
            showHistory = false
            isOnline = true
            return
        }

        runLive(
            block = {
                api.updateTodayIntent(todayIntent.plan.apiValue, todayIntent.intent.apiValue, todayIntent.timeWindow.apiValue)
                api.goOnline(MANHATTAN_LAT, MANHATTAN_LNG, 50)
                api.discoverMap()
            },
            onSuccess = { discovery ->
                showHistory = false
                isOnline = true
                applyDiscovery(discovery)
            }
        )
    }

    fun goOffline() {
        backendApi?.let { api ->
            runLive(block = { api.goOffline() }, onSuccess = { isOnline = false })
            return
        }
        isOnline = false
    }

    fun viewPoint(point: MapPoint) {
        val api = backendApi
        if (api == null) {
            selectedPoint = point
            if (point.state == MapPointState.Unseen) {
                updatePoint(point.id, MapPointState.Viewed)
            }
            return
        }

        runLive(
            block = { api.openMapPoint(point.id) },
            onSuccess = { profile ->
                val updated = point.copy(profile = profile.toUserProfile(point), state = MapPointState.Viewed)
                updatePoint(point.id, MapPointState.Viewed, updated.profile)
                selectedPoint = updated
            },
            fallback = {
                selectedPoint = point
                if (point.state == MapPointState.Unseen) updatePoint(point.id, MapPointState.Viewed)
            }
        )
    }

    fun closeProfilePreview() {
        selectedPoint = null
    }

    fun markInterested(point: MapPoint) {
        val api = backendApi
        if (api == null) {
            markInterestedMock(point)
            return
        }

        runLive(
            block = {
                api.likeProfile(point.profile.id)
                api.getActiveMatchDetail()
            },
            onSuccess = { detail ->
                updatePoint(point.id, MapPointState.Interested)
                selectedPoint = null
                if (detail != null) {
                    applyActiveMatchDetail(detail)
                    isOnline = false
                }
            },
            fallback = { markInterestedMock(point) }
        )
    }

    fun notNow(point: MapPoint) {
        backendApi?.let { api ->
            runLive(
                block = { api.passProfile(point.profile.id) },
                onSuccess = {
                    updatePoint(point.id, MapPointState.HiddenToday)
                    selectedPoint = null
                }
            )
            return
        }
        hidePointForToday(point)
    }

    fun block(point: MapPoint) {
        updatePoint(point.id, MapPointState.Blocked)
        selectedPoint = null
    }

    fun sendFirstLoop() {
        val match = activeMatch ?: return
        val api = backendApi
        if (api == null) {
            activeMatch = match.copy(myFirstLoopSent = true, theirFirstLoopReceived = true)
            return
        }

        runLive(
            block = {
                val bytes = "now-demo-first-loop".toByteArray()
                val uploadIntent = api.createUploadIntent("first_loop", "video/mp4", bytes.size)
                api.uploadBytes(uploadIntent, bytes, "video/mp4")
                api.uploadFirstLoop(match.id, uploadIntent.storageKey, 5000)
                api.getActiveMatchDetail()
            },
            onSuccess = { detail ->
                if (detail != null) {
                    applyActiveMatchDetail(detail)
                    if (!detail.chatUnlocked) {
                        activeMatch = activeMatch?.copy(myFirstLoopSent = true, theirFirstLoopReceived = true)
                    }
                } else {
                    activeMatch = activeMatch?.copy(myFirstLoopSent = true, theirFirstLoopReceived = true)
                }
            },
            fallback = {
                activeMatch = activeMatch?.copy(myFirstLoopSent = true, theirFirstLoopReceived = true)
            }
        )
    }

    fun sendMessage(text: String) {
        val trimmed = text.trim()
        if (!chatUnlocked || trimmed.isEmpty()) return
        val match = activeMatch
        val api = backendApi
        if (api == null || match == null) {
            messages.add(Message(UUID.randomUUID().toString(), MessageSender.Me, trimmed))
            return
        }

        runLive(
            block = { api.sendMessage(match.id, trimmed) },
            onSuccess = { message ->
                messages.add(Message(message.id, MessageSender.Me, message.body))
            },
            fallback = {
                messages.add(Message(UUID.randomUUID().toString(), MessageSender.Me, trimmed))
            }
        )
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
        updatePoint(id, state, selectedPoint?.profile)
    }

    private fun updatePoint(id: String, state: MapPointState, profile: UserProfile?) {
        val index = mapPoints.indexOfFirst { it.id == id }
        if (index == -1) return
        mapPoints[index] = mapPoints[index].copy(state = state, profile = profile ?: mapPoints[index].profile)
        if (selectedPoint?.id == id) {
            selectedPoint = selectedPoint?.copy(state = state, profile = profile ?: selectedPoint!!.profile)
        }
    }

    private fun applyBootstrap(bootstrap: BootstrapSnapshot, detail: ActiveMatchSnapshot?) {
        isProfileComplete = !bootstrap.requirements.profileRequired
        isOnline = bootstrap.onlineSession != null
        if (detail != null) {
            applyActiveMatchDetail(detail)
        } else {
            activeMatch = null
            if (bootstrap.nextStep == "discover") {
                backendApi?.let { api ->
                    runLive(block = { api.discoverMap() }, onSuccess = { applyDiscovery(it) })
                }
            }
        }
    }

    private fun applyDiscovery(discovery: DiscoveryMapSnapshot) {
        mapPoints.clear()
        mapPoints.addAll(discovery.points.map { it.toMapPoint() })
    }

    private fun applyActiveMatchDetail(detail: ActiveMatchSnapshot) {
        val profile = detail.otherProfile.toUserProfile()
        activeMatch = Match(
            id = detail.match.id,
            profile = profile,
            status = MatchStatus.Active,
            myFirstLoopSent = detail.loops.isNotEmpty(),
            theirFirstLoopReceived = detail.chatUnlocked,
            meetingStatus = detail.latestMeetingStatus?.status.toMeetingStatus()
        )
        messages.clear()
        messages.addAll(detail.messages.map { Message(it.id, MessageSender.Them, it.body) })
        meetingProposal = detail.latestMeetingProposal?.let {
            MeetingProposal(
                id = it.id,
                matchId = it.matchId,
                placeName = it.placeName,
                latitude = MANHATTAN_LAT,
                longitude = MANHATTAN_LNG,
                time = it.proposedTime,
                status = it.status.toProposalStatus()
            )
        }
    }

    private fun markInterestedMock(point: MapPoint) {
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

    private fun hidePointForToday(point: MapPoint) {
        updatePoint(point.id, MapPointState.HiddenToday)
        selectedPoint = null
    }

    private fun <T> runLive(block: () -> T, onSuccess: (T) -> Unit, fallback: (() -> Unit)? = null) {
        if (backendApi == null) {
            fallback?.invoke()
            return
        }
        isLoading = true
        errorMessage = null
        Thread {
            try {
                val result = block()
                mainHandler.post {
                    isLoading = false
                    onSuccess(result)
                }
            } catch (error: Exception) {
                mainHandler.post {
                    isLoading = false
                    errorMessage = error.message ?: "Backend is unavailable"
                    fallback?.invoke()
                }
            }
        }.start()
    }

    private companion object {
        const val DEMO_EMAIL = "demo.ava@example.com"
        const val DEMO_PASSWORD = "password123"
        const val MANHATTAN_LAT = 40.7580
        const val MANHATTAN_LNG = -73.9855
    }
}

private val Plan.apiValue: String
    get() = when (this) {
        Plan.Coffee -> "coffee"
        Plan.Walk -> "walk"
        Plan.Lunch -> "lunch"
        Plan.Dinner -> "dinner"
        Plan.Activity -> "activity"
    }

private val Intent.apiValue: String
    get() = when (this) {
        Intent.Friendly -> "friendly"
        Intent.Date -> "date"
        Intent.Romantic -> "romantic"
        Intent.OpenMinded -> "open-minded"
    }

private val TimeWindow.apiValue: String
    get() = when (this) {
        TimeWindow.Now -> "now"
        TimeWindow.Lunch -> "lunch"
        TimeWindow.Afternoon -> "afternoon"
        TimeWindow.Evening -> "evening"
    }

private fun BackendMapPoint.toMapPoint(): MapPoint =
    MapPoint(
        id = pointId,
        profile = UserProfile(
            id = profileId,
            name = displayName,
            age = 29,
            distance = if (distanceMeters < 1000) "${distanceMeters} m" else "${distanceMeters / 1000.0} km",
            plan = plan.toPlan(),
            intent = intent.toIntent(),
            occupation = "",
            languages = emptyList(),
            interests = emptyList(),
            sharedInterests = emptyList(),
            prompt = "${plan.toPlan().label} · ${intent.toIntent().label} · ${timeToday.toTimeWindow().label}"
        ),
        approximateLatitude = latitude,
        approximateLongitude = longitude,
        state = state.toMapPointState(),
        isMutualMock = false
    )

private fun BackendProfile.toUserProfile(point: MapPoint? = null): UserProfile =
    UserProfile(
        id = id,
        name = displayName,
        age = ageFromBirthDate(birthDate),
        distance = point?.profile?.distance ?: "Nearby",
        plan = point?.profile?.plan ?: Plan.Coffee,
        intent = point?.profile?.intent ?: Intent.Date,
        occupation = "",
        languages = emptyList(),
        interests = interests,
        sharedInterests = interests.take(3),
        prompt = bio
    )

private fun String.toPlan(): Plan =
    when (this) {
        "walk" -> Plan.Walk
        "lunch" -> Plan.Lunch
        "dinner" -> Plan.Dinner
        "activity" -> Plan.Activity
        else -> Plan.Coffee
    }

private fun String.toIntent(): Intent =
    when (this) {
        "friendly" -> Intent.Friendly
        "romantic" -> Intent.Romantic
        "open-minded" -> Intent.OpenMinded
        else -> Intent.Date
    }

private fun String.toTimeWindow(): TimeWindow =
    when (this) {
        "now" -> TimeWindow.Now
        "lunch" -> TimeWindow.Lunch
        "afternoon" -> TimeWindow.Afternoon
        else -> TimeWindow.Evening
    }

private fun String.toMapPointState(): MapPointState =
    when (this) {
        "viewed" -> MapPointState.Viewed
        "liked_today" -> MapPointState.Interested
        "cancelled_match_before" -> MapPointState.Viewed
        else -> MapPointState.Unseen
    }

private fun String?.toMeetingStatus(): MeetingStatus =
    when (this) {
        "on_my_way" -> MeetingStatus.OnMyWay
        "arrived" -> MeetingStatus.Arrived
        "delayed" -> MeetingStatus.Delayed
        else -> MeetingStatus.None
    }

private fun String.toProposalStatus(): MeetingProposalStatus =
    when (this) {
        "accepted" -> MeetingProposalStatus.Accepted
        "rejected" -> MeetingProposalStatus.Rejected
        else -> MeetingProposalStatus.Pending
    }

private fun ageFromBirthDate(birthDate: String): Int =
    try {
        Period.between(LocalDate.parse(birthDate), LocalDate.now()).years
    } catch (_: Exception) {
        29
    }
