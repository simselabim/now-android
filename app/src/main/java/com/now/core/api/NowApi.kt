package com.now.core.api

import com.now.core.model.*

interface NowApi {
    suspend fun getMe(): UserProfile
    suspend fun updateTodayIntent(intent: TodayIntent)
    suspend fun discoverMap(): List<MapPoint>
    suspend fun likeProfile(profileId: String): Match?
    suspend fun passProfile(profileId: String)
    suspend fun getActiveMatch(): Match?
    suspend fun sendMessage(matchId: String, text: String): Message
    suspend fun createMeetingProposal(matchId: String, proposal: MeetingProposal): MeetingProposal
}

class MockNowApi : NowApi {
    override suspend fun getMe(): UserProfile = MockData.mapPoints.first().profile
    override suspend fun updateTodayIntent(intent: TodayIntent) = Unit
    override suspend fun discoverMap(): List<MapPoint> = MockData.mapPoints
    override suspend fun likeProfile(profileId: String): Match? = null
    override suspend fun passProfile(profileId: String) = Unit
    override suspend fun getActiveMatch(): Match? = null
    override suspend fun sendMessage(matchId: String, text: String): Message =
        Message(id = matchId + text.hashCode(), sender = MessageSender.Me, text = text)

    override suspend fun createMeetingProposal(matchId: String, proposal: MeetingProposal): MeetingProposal = proposal
}
