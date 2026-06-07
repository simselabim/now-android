# NOW Android

Native Android client for NOW.

NOW is a map-first mobile app for meeting one nearby person today. The Android app should be built natively with Kotlin, Jetpack Compose, Google Maps, FusedLocationProvider, CameraX, and a backend API shared with iOS.

## Product Core

- One active match at a time.
- One-day lifecycle for online sessions, likes, passes, matches, and temporary chat.
- Map-first discovery, no swipe deck.
- Discovery points are approximate, not exact user locations.
- `Not Now` hides a person until tomorrow.
- `Block` hides a person permanently.
- Mutual today-interest creates one active match.
- First loops unlock temporary chat.
- Meeting place and time should be confirmed through NOW for safety.

## Suggested Stack

- Kotlin
- Jetpack Compose
- Navigation Compose
- Google Maps SDK
- FusedLocationProvider
- CameraX
- Retrofit
- OkHttp
- Kotlin Serialization
- DataStore
- Hilt or Koin

## Repository Layout

```text
app/
  src/main/java/com/now/
    app/
      NowApplication.kt
      MainActivity.kt
      AppNavGraph.kt
      AppState.kt

    core/
      api/
        ApiClient.kt
        AuthInterceptor.kt
        NowApi.kt
      model/
        User.kt
        Profile.kt
        TodayIntent.kt
        MapPoint.kt
        Match.kt
        Loop.kt
        Message.kt
        Meeting.kt
      location/
        LocationService.kt
        LocationPermissionManager.kt
      media/
        CameraService.kt
        UploadService.kt
      realtime/
        WebSocketClient.kt
      storage/
        TokenStorage.kt
      design/
        Colors.kt
        Typography.kt
        Components.kt

    features/
      auth/
      onboarding/
      profile/
      todayintent/
      discoverymap/
      profilepreview/
      match/
      loops/
      chat/
      meetingproposal/
      meetingmode/
      history/
      settings/
      safety/
      moderation/
```

## First Build Target

Build a mock-driven navigation prototype first:

```text
Welcome
 -> Login/Register
 -> Create Profile
 -> Go Online
 -> Today Intent
 -> Discovery Map
 -> Profile Preview
 -> Active Match
 -> First Loops
 -> Temporary Chat
 -> Meeting Proposal
 -> Meeting Mode
 -> History
```

Do not port the web prototype. Use it only as product reference.
