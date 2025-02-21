package ru.dragon_slayer.server

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ru.dragon_slayer.error_handler.installExceptionHandler

fun main() {
    /*    val config: Config = loadConfig(".env")
initDatabase(config)*/
    val server = embeddedServer(Netty, port = 8080) {
        initPlugins()
        macrosRouting()
        installExceptionHandler()
    }
    server.start(wait = true)
}