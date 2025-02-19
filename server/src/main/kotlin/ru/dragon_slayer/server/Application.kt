package ru.dragon_slayer.server

import authModule
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    /*    val config: Config = loadConfig(".env")
initDatabase(config)*/
    val server = embeddedServer(Netty, port = 8080) {
        initPlugins()
        authModule()
        macrosRouting()
    }
    server.start(wait = true)
}