import auth_flow.authWs
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*

fun Application.authModule() {
    routing {
        authRoutes()
        //webSocket("/ws/auth") { authWs() }
    }
}