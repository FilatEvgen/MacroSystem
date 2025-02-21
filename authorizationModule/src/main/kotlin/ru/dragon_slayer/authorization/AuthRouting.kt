package ru.dragon_slayer.authorization

import io.ktor.server.routing.*

fun Route.configureAuthorizationRouting() {
    get("/v1/authenticate/url") { handleGetUrl() }
    post("/v1/authenticate/callback") { handleAuthenticateCallback() }
}