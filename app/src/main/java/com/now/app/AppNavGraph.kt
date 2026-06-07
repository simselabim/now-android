package com.now.app

import androidx.compose.runtime.Composable
import com.now.features.auth.WelcomeScreen
import com.now.features.discoverymap.DiscoveryMapScreen
import com.now.features.history.HistoryScreen
import com.now.features.match.MatchFlowScreen
import com.now.features.onboarding.CreateProfileScreen
import com.now.features.profilepreview.ProfilePreviewScreen
import com.now.features.todayintent.GoOnlineScreen

@Composable
fun AppNavGraph(appState: AppState) {
    when {
        !appState.isAuthenticated -> WelcomeScreen(appState)
        !appState.isProfileComplete -> CreateProfileScreen(appState)
        appState.showHistory -> HistoryScreen(appState)
        appState.activeMatch != null -> MatchFlowScreen(appState)
        appState.selectedPoint != null -> ProfilePreviewScreen(appState)
        !appState.isOnline -> GoOnlineScreen(appState)
        else -> DiscoveryMapScreen(appState)
    }
}
