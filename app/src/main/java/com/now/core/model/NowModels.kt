package com.now.core.model

enum class Plan(val label: String) {
    Coffee("Coffee"),
    Walk("Walk"),
    Lunch("Lunch"),
    Dinner("Dinner"),
    Activity("Activity")
}

enum class Intent(val label: String) {
    Friendly("Friendly"),
    Date("Date"),
    Romantic("Romantic"),
    OpenMinded("Open-minded")
}

enum class TimeWindow(val label: String) {
    Now("Now"),
    Lunch("Lunch"),
    Afternoon("Afternoon"),
    Evening("Evening")
}

enum class MapPointState {
    Unseen,
    Viewed,
    Interested,
    HiddenToday,
    Blocked
}

enum class MatchStatus {
    Active,
    Met,
    Cancelled,
    Expired
}

enum class MeetingStatus(val label: String) {
    None("None"),
    OnMyWay("On my way"),
    Arrived("Arrived"),
    Delayed("Delayed")
}

enum class MeetingProposalStatus {
    Pending,
    Accepted,
    Rejected
}

enum class MessageSender {
    Me,
    Them
}

data class UserProfile(
    val id: String,
    val name: String,
    val age: Int,
    val distance: String,
    val plan: Plan,
    val intent: Intent,
    val occupation: String,
    val languages: List<String>,
    val interests: List<String>,
    val sharedInterests: List<String>,
    val prompt: String
)

data class TodayIntent(
    val plan: Plan,
    val intent: Intent,
    val timeWindow: TimeWindow
)

data class MapPoint(
    val id: String,
    val profile: UserProfile,
    val approximateLatitude: Double,
    val approximateLongitude: Double,
    val state: MapPointState,
    val isMutualMock: Boolean
)

data class Match(
    val id: String,
    val profile: UserProfile,
    val status: MatchStatus,
    val myFirstLoopSent: Boolean,
    val theirFirstLoopReceived: Boolean,
    val meetingStatus: MeetingStatus
)

data class Message(
    val id: String,
    val sender: MessageSender,
    val text: String
)

data class MeetingProposal(
    val id: String,
    val matchId: String,
    val placeName: String,
    val latitude: Double,
    val longitude: Double,
    val time: String,
    val status: MeetingProposalStatus
)

data class HistoryItem(
    val id: String,
    val name: String,
    val detail: String,
    val status: String
)
