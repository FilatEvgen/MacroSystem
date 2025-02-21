package ru.dragon_slayer.server

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json

fun Application.initPlugins() {
    val jsonConfig = Json { prettyPrint = true }

    install(ContentNegotiation) {
        json(jsonConfig)
    }
}