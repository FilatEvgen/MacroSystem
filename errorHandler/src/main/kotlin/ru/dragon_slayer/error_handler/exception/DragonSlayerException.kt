package ru.dragon_slayer.error_handler.exception

open class DragonSlayerException(
    val errorCode: String,
    message: String = "",
    val args: List<String> = emptyList(),
    cause: Throwable? = null
) : Exception(message.ifEmpty { errorCode }, cause) {
    companion object {
        const val ERROR_CODE_BAD_PASSWORD = "badPassword"
        const val ERROR_ACCESS_DENIED = "accessDenied"
        const val ERROR_INTERNAL_ERROR = "internalError"
        fun internalError(message: String, cause: Throwable) = DragonSlayerException(
            errorCode = ERROR_INTERNAL_ERROR,
            message = message,
            cause = cause
        )

        fun accessDenied() =
            DragonSlayerException(errorCode = ERROR_ACCESS_DENIED, message = "Access denied")

        fun badUsernameOrPassword() = DragonSlayerException(
            errorCode = ERROR_CODE_BAD_PASSWORD,
            message = ERROR_CODE_BAD_PASSWORD//"Неверный логин или пароль"
        )
    }
}

fun Exception.errorCodeOrMessage(): String {
    if (this is DragonSlayerException) {
        return this.errorCode
    }
    return message ?: toString()
}

fun Exception.message(): String {
    val message = this.message
    if (message != null && message.isNotBlank()) {
        return message
    }
    return this.errorCodeOrMessage()
}
