package ru.dragon_slayer.server

import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun Application.initPlugins() {
    val jsonConfig = Json { prettyPrint = true }

    install(ContentNegotiation) {
        json(jsonConfig)
    }
    install(StatusPages) {
        exception<Throwable> { _, cause ->
            cause.printStackTrace()
        }
    }
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(jsonConfig)
        pingPeriod = 5.seconds
        timeout = Duration.INFINITE
    }
}