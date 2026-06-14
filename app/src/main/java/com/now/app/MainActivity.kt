package com.now.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.now.core.api.ApiEnvironment
import com.now.core.api.AuthTokenStore
import com.now.core.api.NowBackendApi
import com.now.core.design.NowColors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val appState = remember {
                AppState(
                    backendApi = NowBackendApi(
                        environment = ApiEnvironment.localEmulator,
                        tokenStore = AuthTokenStore(context)
                    )
                )
            }
            MaterialTheme {
                Surface(color = NowColors.paper) {
                    AppNavGraph(appState = appState)
                }
            }
        }
    }
}
