package ru.dragon_slayer.error_handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import ru.dragon_slayer.error_handler.api.ServerResponseError
import ru.dragon_slayer.error_handler.api.ServerResponseException
import ru.dragon_slayer.error_handler.api.toServerResponseError
import ru.dragon_slayer.error_handler.base.Serializer
import ru.dragon_slayer.error_handler.exception.DragonSlayerException

interface IExceptionFilter {
    fun isImportant(throwable: Throwable): Boolean

    companion object {
        val EXCEPTION_FILTER_NO_OP = object : IExceptionFilter {
            override fun isImportant(throwable: Throwable) = true
        }

        fun notImportantByCode(errorCodes: Set<String>) = object : IExceptionFilter {
            override fun isImportant(throwable: Throwable): Boolean {
                if (throwable is DragonSlayerException) {
                    return !errorCodes.contains(throwable.errorCode)
                }
                return true
            }
        }
    }
}


fun Application.installExceptionHandler(exceptionFilter: IExceptionFilter = IExceptionFilter.EXCEPTION_FILTER_NO_OP) {
    install(StatusPages) {
        addCommonExceptionHandler(exceptionFilter)
    }
}

fun StatusPagesConfig.addCommonExceptionHandler(exceptionFilter: IExceptionFilter) {
    exception<Throwable> { call, throwable ->
        handleServerException(throwable, call, null, exceptionFilter)
    }
}

private suspend fun handleServerException(
    throwable: Throwable,
    call: ApplicationCall,
    body: String?,
    exceptionFilter: IExceptionFilter
) {
    val errorResponse = if (throwable is DragonSlayerException) {
        throwable.toServerResponseError()
    } else {
        ServerResponseError.internalServerError(message = "Unhandled exception", cause = throwable)
    }
    call.respondText(contentType = ContentType.Application.Json, status = HttpStatusCode.InternalServerError) {
        Serializer.serializer.encodeToString(ServerResponseError.serializer(), errorResponse)
    }
}

suspend fun ApplicationCall.respondInvalidParameters(e: IllegalArgumentException) {
    val errorResponse = ServerResponseException.invalidParameters(e.message?: "Parameters is invalid").toServerResponseError()
    respondText(contentType = ContentType.Application.Json, status = HttpStatusCode.BadRequest) {
        Serializer.serializer.encodeToString(ServerResponseError.serializer(), errorResponse)
    }
}

suspend fun ApplicationCall.respondNotFound(e: NotFoundException) {
    val errorResponse = ServerResponseException.notFound(e.message?: "Not found").toServerResponseError()
    respondText(contentType = ContentType.Application.Json, status = HttpStatusCode.NotFound) {
        Serializer.serializer.encodeToString(ServerResponseError.serializer(), errorResponse)
    }
}

