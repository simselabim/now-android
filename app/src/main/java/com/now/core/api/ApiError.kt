package com.now.core.api

sealed class ApiError(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class MissingToken : ApiError("Missing access token")
    class InvalidResponse(message: String) : ApiError(message)
    class RequestFailed(val statusCode: Int, message: String) : ApiError(message)
    class Network(cause: Throwable) : ApiError(cause.message ?: "Network error", cause)
}
