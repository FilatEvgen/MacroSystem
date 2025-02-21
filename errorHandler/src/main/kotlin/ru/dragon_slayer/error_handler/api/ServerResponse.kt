package ru.dragon_slayer.error_handler.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.dragon_slayer.error_handler.exception.DragonSlayerException

interface IServerResponse

@Serializable
data class ServerResponseError(
    @SerialName("error")
    val errorCode: String,
    val message: String = errorCode,
    val args: List<String> = emptyList(),
    val cause: String = ""
) : IServerResponse {
    companion object {
        const val INTERNAL_SERVER_ERROR = "internalServerError"
        const val INVALID_RESPONSE = "invalidResponse"
        const val PARSE_RESPONSE_IS_FAILED = "parseResponseIsFailed"
        const val INVALID_PARAMETERS = "invalidParameters"
        const val NOT_FOUND = "not found"
        fun internalServerError(message: String, cause: Throwable) =
            ServerResponseError(errorCode = INTERNAL_SERVER_ERROR, message = message, cause = cause.message ?: "")
    }
}

class ServerResponseException(
    errorCode: String,
    message: String,
    args: List<String>,
    cause: Throwable?
) : DragonSlayerException(errorCode = errorCode, message = message, args = args, cause = cause) {
    companion object {
        fun parseResponseIsFailed(message: String = "Parse response is failed", cause: Throwable? = null) =
            ServerResponseException(
                errorCode = ServerResponseError.PARSE_RESPONSE_IS_FAILED,
                message = message,
                args = emptyList(),
                cause = cause
            )

        fun invalidParameters(message: String = "Parameters is invalid", cause: Throwable? = null) =
            ServerResponseException(
                errorCode = ServerResponseError.INVALID_PARAMETERS,
                message = message,
                args = emptyList(), cause = cause
            )

        fun internalServerError(message: String = "Internal server error", cause: Throwable? = null) =
            ServerResponseException(
                errorCode = ServerResponseError.INTERNAL_SERVER_ERROR,
                message = message,
                args = emptyList(),
                cause = cause
            )
        fun notFound(message: String = "Not found", args: List<String> = emptyList(), cause: Throwable? = null) =
            ServerResponseException(
                errorCode = ServerResponseError.NOT_FOUND,
                message = message,
                args = args,
                cause = cause
            )
    }
}

fun DragonSlayerException.toServerResponseError() =
    ServerResponseError(
        errorCode = this.errorCode,
        message = this.message ?: "",
        args = this.args,
        cause = cause?.message ?: ""
    )

fun ServerResponseError.toServerResponseException() =
    ServerResponseException(
        errorCode = this.errorCode,
        message = this.message,
        args = this.args,
        cause = Exception(cause)
    )
