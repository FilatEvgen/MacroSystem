package ru.dragon_slayer.error_handler

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

suspend fun RoutingContext.badRequest(message: String) {
    call.respond(HttpStatusCode.BadRequest, message)
}