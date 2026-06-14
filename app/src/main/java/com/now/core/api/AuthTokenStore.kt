package com.now.core.api

import android.content.Context

class AuthTokenStore(context: Context) {
    private val preferences = context.getSharedPreferences("now_auth", Context.MODE_PRIVATE)

    var accessToken: String?
        get() = preferences.getString(KEY_ACCESS_TOKEN, null)
        set(value) {
            preferences.edit().apply {
                if (value == null) remove(KEY_ACCESS_TOKEN) else putString(KEY_ACCESS_TOKEN, value)
                apply()
            }
        }

    fun clear() {
        accessToken = null
    }

    private companion object {
        const val KEY_ACCESS_TOKEN = "access_token"
    }
}
