package com.now.core.api

import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class NowBackendApi(
    private val environment: ApiEnvironment,
    private val tokenStore: AuthTokenStore
) {
    suspend fun register(email: String, password: String): AuthSession {
        val response = request(
            method = "POST",
            path = "/auth/register",
            body = JSONObject().put("email", email).put("password", password),
            requiresAuth = false
        )
        return response.toAuthSession().also { tokenStore.accessToken = it.accessToken }
    }

    suspend fun login(email: String, password: String): AuthSession {
        val response = request(
            method = "POST",
            path = "/auth/login",
            body = JSONObject().put("email", email).put("password", password),
            requiresAuth = false
        )
        return response.toAuthSession().also { tokenStore.accessToken = it.accessToken }
    }

    suspend fun bootstrap(): BootstrapSnapshot =
        request(method = "GET", path = "/app/bootstrap").toBootstrapSnapshot()

    suspend fun updateProfile(
        displayName: String,
        birthDate: String,
        gender: String,
        bio: String,
        interests: List<String>
    ): BackendProfile =
        request(
            method = "PUT",
            path = "/profiles/me",
            body = JSONObject()
                .put("display_name", displayName)
                .put("birth_date", birthDate)
                .put("gender", gender)
                .put("bio", bio)
                .put("interests", JSONArray(interests))
        ).toProfile()

    suspend fun createUploadIntent(kind: String, contentType: String, fileSizeBytes: Int): UploadIntent =
        request(
            method = "POST",
            path = "/media/upload-intent",
            body = JSONObject()
                .put("kind", kind)
                .put("content_type", contentType)
                .put("file_size_bytes", fileSizeBytes)
        ).toUploadIntent()

    suspend fun uploadBytes(uploadIntent: UploadIntent, bytes: ByteArray, contentType: String) {
        val connection = URL(uploadIntent.uploadUrl).openConnection() as HttpURLConnection
        try {
            connection.requestMethod = uploadIntent.method
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", contentType)
            uploadIntent.headers.forEach { connection.setRequestProperty(it.name, it.value) }
            connection.outputStream.use { it.write(bytes) }
            val status = connection.responseCode
            if (status !in 200..299) {
                throw ApiError.RequestFailed(status, readError(connection))
            }
        } catch (error: ApiError) {
            throw error
        } catch (error: Exception) {
            throw ApiError.Network(error)
        } finally {
            connection.disconnect()
        }
    }

    suspend fun uploadPhoto(storageKey: String, position: Int, isMain: Boolean): BackendPhoto =
        request(
            method = "POST",
            path = "/profiles/me/photos",
            body = JSONObject()
                .put("storage_key", storageKey)
                .put("position", position)
                .put("is_main", isMain)
        ).getJSONObject("photo").toPhoto()

    suspend fun updateTodayIntent(plan: String, intent: String, timeToday: String): BackendTodayIntent =
        request(
            method = "PUT",
            path = "/today-intent",
            body = JSONObject()
                .put("plan", plan)
                .put("intent", intent)
                .put("time_today", timeToday)
        ).toTodayIntent()

    suspend fun goOnline(latitude: Double, longitude: Double, accuracyMeters: Int?): BackendOnlineSession {
        val body = JSONObject().put("lat", latitude).put("lng", longitude)
        if (accuracyMeters != null) body.put("accuracy_m", accuracyMeters)
        return request(method = "POST", path = "/online", body = body).getJSONObject("session").toOnlineSession()
    }

    suspend fun goOffline() {
        request(method = "DELETE", path = "/online")
    }

    suspend fun discoverMap(radiusMeters: Int = 2000): DiscoveryMapSnapshot =
        request(method = "GET", path = "/discover/map?radius_m=$radiusMeters").toDiscoveryMapSnapshot()

    suspend fun openMapPoint(pointId: String): BackendProfile =
        request(method = "GET", path = "/discover/points/$pointId").getJSONObject("profile").toProfile()

    suspend fun likeProfile(profileId: String): BackendMatch? =
        request(method = "POST", path = "/discover/profiles/$profileId/like").nullableObject("match_item")?.toMatch()

    suspend fun passProfile(profileId: String) {
        request(method = "POST", path = "/discover/profiles/$profileId/pass")
    }

    suspend fun getActiveMatchDetail(): ActiveMatchSnapshot? =
        request(method = "GET", path = "/matches/active/detail")
            .nullableObject("match_item")
            ?.toActiveMatchSnapshot()

    suspend fun uploadFirstLoop(matchId: String, storageKey: String, durationMs: Int): BackendLoop =
        request(
            method = "POST",
            path = "/matches/$matchId/loops",
            body = JSONObject()
                .put("storage_key", storageKey)
                .put("duration_ms", durationMs)
        ).getJSONObject("loop_item").toLoop()

    suspend fun sendMessage(matchId: String, body: String): BackendMessage =
        request(
            method = "POST",
            path = "/matches/$matchId/messages",
            body = JSONObject().put("body", body)
        ).getJSONObject("message").toMessage()

    suspend fun createMeetingProposal(
        matchId: String,
        placeName: String,
        proposedTime: String,
        format: String,
        note: String?
    ): BackendMeetingProposal {
        val body = JSONObject()
            .put("place_name", placeName)
            .put("proposed_time", proposedTime)
            .put("format", format)
        if (note != null) body.put("note", note)
        return request(method = "POST", path = "/matches/$matchId/meeting-proposals", body = body).toMeetingProposal()
    }

    suspend fun acceptMeetingProposal(matchId: String, proposalId: String): BackendMeetingProposal =
        request(
            method = "POST",
            path = "/matches/$matchId/meeting-proposals/$proposalId/accept"
        ).toMeetingProposal()

    suspend fun updateMeetingStatus(matchId: String, status: String): BackendMeetingStatus =
        request(
            method = "POST",
            path = "/matches/$matchId/meeting-status",
            body = JSONObject().put("status", status)
        ).toMeetingStatus()

    suspend fun confirmWeMet(matchId: String): BackendMatch =
        request(method = "POST", path = "/matches/$matchId/we-met").getJSONObject("match_item").toMatch()

    private fun request(
        method: String,
        path: String,
        body: JSONObject? = null,
        requiresAuth: Boolean = true
    ): JSONObject {
        val connection = URL(environment.baseUrl.trimEnd('/') + path).openConnection() as HttpURLConnection
        try {
            connection.requestMethod = method
            connection.setRequestProperty("Accept", "application/json")
            if (requiresAuth) {
                val token = tokenStore.accessToken ?: throw ApiError.MissingToken()
                connection.setRequestProperty("Authorization", "Bearer $token")
            }
            if (body != null) {
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json")
                connection.outputStream.use { it.write(body.toString().toByteArray(StandardCharsets.UTF_8)) }
            }

            val status = connection.responseCode
            val payload = readPayload(connection, status)
            if (status !in 200..299) {
                val message = payload.optString("error", payload.toString())
                throw ApiError.RequestFailed(status, message)
            }
            return payload
        } catch (error: ApiError) {
            throw error
        } catch (error: Exception) {
            throw ApiError.Network(error)
        } finally {
            connection.disconnect()
        }
    }

    private fun readPayload(connection: HttpURLConnection, status: Int): JSONObject {
        val stream = if (status in 200..299) connection.inputStream else connection.errorStream
        val text = stream?.use { input ->
            BufferedReader(InputStreamReader(input, StandardCharsets.UTF_8)).readText()
        }.orEmpty()
        return if (text.isBlank()) JSONObject() else JSONObject(text)
    }

    private fun readError(connection: HttpURLConnection): String =
        connection.errorStream?.use { input ->
            BufferedReader(InputStreamReader(input, StandardCharsets.UTF_8)).readText()
        }.orEmpty().ifBlank { "Request failed" }
}

private fun JSONObject.toAuthSession(): AuthSession =
    AuthSession(
        accessToken = getString("access_token"),
        user = getJSONObject("user").toUser()
    )

private fun JSONObject.toUser(): BackendUser =
    BackendUser(
        id = getString("id"),
        email = getString("email"),
        status = getString("status")
    )

private fun JSONObject.toBootstrapSnapshot(): BootstrapSnapshot =
    BootstrapSnapshot(
        user = getJSONObject("user").toUser(),
        profile = nullableObject("profile")?.toProfile(),
        todayIntent = nullableObject("today_intent")?.toTodayIntent(),
        onlineSession = nullableObject("online_session")?.toOnlineSession(),
        activeMatch = nullableObject("active_match")?.toMatch(),
        requirements = getJSONObject("requirements").toBootstrapRequirements(),
        discoveryLocked = getBoolean("discovery_locked"),
        nextStep = getString("next_step")
    )

private fun JSONObject.toBootstrapRequirements(): BootstrapRequirements =
    BootstrapRequirements(
        profileRequired = getBoolean("profile_required"),
        intentRequired = getBoolean("intent_required"),
        onlineRequired = getBoolean("online_required"),
        activeMatchRequired = getBoolean("active_match_required")
    )

private fun JSONObject.toProfile(): BackendProfile =
    BackendProfile(
        id = getString("id"),
        userId = getString("user_id"),
        displayName = getString("display_name"),
        birthDate = getString("birth_date"),
        gender = getString("gender"),
        bio = getString("bio"),
        interests = getJSONArray("interests").toStringList(),
        isPublishable = getBoolean("is_publishable"),
        photos = getJSONArray("photos").mapObjects { it.toPhoto() }
    )

private fun JSONObject.toPhoto(): BackendPhoto =
    BackendPhoto(
        id = getString("id"),
        storageKey = getString("storage_key"),
        position = getInt("position"),
        isMain = getBoolean("is_main")
    )

private fun JSONObject.toTodayIntent(): BackendTodayIntent =
    BackendTodayIntent(
        id = getString("id"),
        plan = getString("plan"),
        intent = getString("intent"),
        timeToday = getString("time_today")
    )

private fun JSONObject.toOnlineSession(): BackendOnlineSession =
    BackendOnlineSession(
        id = getString("id"),
        status = getString("status")
    )

private fun JSONObject.toDiscoveryMapSnapshot(): DiscoveryMapSnapshot =
    DiscoveryMapSnapshot(
        radiusMeters = getInt("radius_m"),
        discoveryLocked = getBoolean("discovery_locked"),
        points = getJSONArray("points").mapObjects { it.toMapPoint() }
    )

private fun JSONObject.toMapPoint(): BackendMapPoint =
    BackendMapPoint(
        pointId = getString("point_id"),
        profileId = getString("profile_id"),
        userId = getString("user_id"),
        displayName = getString("display_name"),
        mainPhotoStorageKey = optNullableString("main_photo_storage_key"),
        plan = getString("plan"),
        intent = getString("intent"),
        timeToday = getString("time_today"),
        latitude = getDouble("lat"),
        longitude = getDouble("lng"),
        distanceMeters = getInt("distance_m"),
        state = getString("state")
    )

private fun JSONObject.toMatch(): BackendMatch =
    BackendMatch(
        id = getString("id"),
        otherUserId = getString("other_user_id"),
        status = getString("status")
    )

private fun JSONObject.toActiveMatchSnapshot(): ActiveMatchSnapshot =
    ActiveMatchSnapshot(
        match = getJSONObject("match_item").toMatch(),
        otherProfile = getJSONObject("other_profile").toProfile(),
        loops = getJSONArray("loops").mapObjects { it.toLoop() },
        chatUnlocked = getBoolean("chat_unlocked"),
        messages = getJSONArray("messages").mapObjects { it.toMessage() },
        latestMeetingProposal = nullableObject("latest_meeting_proposal")?.toMeetingProposal(),
        latestMeetingStatus = nullableObject("latest_meeting_status")?.toMeetingStatus(),
        flags = getJSONObject("flags").toActiveMatchFlags()
    )

private fun JSONObject.toActiveMatchFlags(): ActiveMatchFlags =
    ActiveMatchFlags(
        canSendMessage = getBoolean("can_send_message"),
        canCreateProposal = getBoolean("can_create_proposal"),
        canConfirmWeMet = getBoolean("can_confirm_we_met")
    )

private fun JSONObject.toLoop(): BackendLoop =
    BackendLoop(
        id = getString("id"),
        matchId = getString("match_id"),
        userId = getString("user_id"),
        storageKey = getString("storage_key"),
        durationMs = getInt("duration_ms")
    )

private fun JSONObject.toMessage(): BackendMessage =
    BackendMessage(
        id = getString("id"),
        matchId = getString("match_id"),
        senderUserId = getString("sender_user_id"),
        body = getString("body")
    )

private fun JSONObject.toMeetingProposal(): BackendMeetingProposal =
    BackendMeetingProposal(
        id = getString("id"),
        matchId = getString("match_id"),
        proposerUserId = getString("proposer_user_id"),
        placeName = getString("place_name"),
        proposedTime = getString("proposed_time"),
        format = getString("format"),
        status = getString("status")
    )

private fun JSONObject.toMeetingStatus(): BackendMeetingStatus =
    BackendMeetingStatus(
        id = getString("id"),
        matchId = getString("match_id"),
        userId = getString("user_id"),
        status = getString("status")
    )

private fun JSONObject.toUploadIntent(): UploadIntent =
    UploadIntent(
        storageKey = getString("storage_key"),
        uploadUrl = getString("upload_url"),
        method = getString("method"),
        headers = getJSONArray("headers").mapObjects {
            UploadHeader(name = it.getString("name"), value = it.getString("value"))
        },
        expiresAt = getString("expires_at")
    )

private fun JSONObject.nullableObject(name: String): JSONObject? =
    if (isNull(name)) null else getJSONObject(name)

private fun JSONObject.optNullableString(name: String): String? =
    if (isNull(name)) null else optString(name)

private fun JSONArray.toStringList(): List<String> =
    List(length()) { index -> getString(index) }

private fun <T> JSONArray.mapObjects(transform: (JSONObject) -> T): List<T> =
    List(length()) { index -> transform(getJSONObject(index)) }
