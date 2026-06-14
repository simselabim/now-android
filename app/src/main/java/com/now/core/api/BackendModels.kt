package com.now.core.api

data class AuthSession(
    val accessToken: String,
    val user: BackendUser
)

data class BackendUser(
    val id: String,
    val email: String,
    val status: String
)

data class BootstrapSnapshot(
    val user: BackendUser,
    val profile: BackendProfile?,
    val todayIntent: BackendTodayIntent?,
    val onlineSession: BackendOnlineSession?,
    val activeMatch: BackendMatch?,
    val requirements: BootstrapRequirements,
    val discoveryLocked: Boolean,
    val nextStep: String
)

data class BootstrapRequirements(
    val profileRequired: Boolean,
    val intentRequired: Boolean,
    val onlineRequired: Boolean,
    val activeMatchRequired: Boolean
)

data class BackendProfile(
    val id: String,
    val userId: String,
    val displayName: String,
    val birthDate: String,
    val gender: String,
    val bio: String,
    val interests: List<String>,
    val isPublishable: Boolean,
    val photos: List<BackendPhoto>
)

data class BackendPhoto(
    val id: String,
    val storageKey: String,
    val position: Int,
    val isMain: Boolean
)

data class BackendTodayIntent(
    val id: String,
    val plan: String,
    val intent: String,
    val timeToday: String
)

data class BackendOnlineSession(
    val id: String,
    val status: String
)

data class DiscoveryMapSnapshot(
    val radiusMeters: Int,
    val discoveryLocked: Boolean,
    val points: List<BackendMapPoint>
)

data class BackendMapPoint(
    val pointId: String,
    val profileId: String,
    val userId: String,
    val displayName: String,
    val mainPhotoStorageKey: String?,
    val plan: String,
    val intent: String,
    val timeToday: String,
    val latitude: Double,
    val longitude: Double,
    val distanceMeters: Int,
    val state: String
)

data class BackendMatch(
    val id: String,
    val otherUserId: String,
    val status: String
)

data class ActiveMatchSnapshot(
    val match: BackendMatch,
    val otherProfile: BackendProfile,
    val loops: List<BackendLoop>,
    val chatUnlocked: Boolean,
    val messages: List<BackendMessage>,
    val latestMeetingProposal: BackendMeetingProposal?,
    val latestMeetingStatus: BackendMeetingStatus?,
    val flags: ActiveMatchFlags
)

data class ActiveMatchFlags(
    val canSendMessage: Boolean,
    val canCreateProposal: Boolean,
    val canConfirmWeMet: Boolean
)

data class BackendLoop(
    val id: String,
    val matchId: String,
    val userId: String,
    val storageKey: String,
    val durationMs: Int
)

data class BackendMessage(
    val id: String,
    val matchId: String,
    val senderUserId: String,
    val body: String
)

data class BackendMeetingProposal(
    val id: String,
    val matchId: String,
    val proposerUserId: String,
    val placeName: String,
    val proposedTime: String,
    val format: String,
    val status: String
)

data class BackendMeetingStatus(
    val id: String,
    val matchId: String,
    val userId: String,
    val status: String
)

data class UploadIntent(
    val storageKey: String,
    val uploadUrl: String,
    val method: String,
    val headers: List<UploadHeader>,
    val expiresAt: String
)

data class UploadHeader(
    val name: String,
    val value: String
)
