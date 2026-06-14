package com.now.core.api

data class ApiEnvironment(
    val baseUrl: String
) {
    companion object {
        val localEmulator = ApiEnvironment("http://10.0.2.2:8080")
        val localDevice = ApiEnvironment("http://127.0.0.1:8080")
    }
}
