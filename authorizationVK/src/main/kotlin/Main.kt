import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.*
import org.example.CodeVerifierSession
import java.io.File

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(Sessions) {
        cookie<CodeVerifierSession>("SESSION")
    }
    install(ContentNegotiation) {
        json()
    }
    install(WebSockets)

    val htmlBaseDir = System.getenv("HTML_BASE_DIR") ?: "static"
    frontModule(File(htmlBaseDir))
}

fun Application.frontModule(htmlBaseDir: File) {
    routing {
        staticFiles("/", htmlBaseDir, "index.html") {
        }
        get("/auth") {
            call.respondFile(File(htmlBaseDir, "index.html"))
        }
        authRoutes()

        webSocket("/ws") {
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val message = frame.readText()
                    send("Received: $message")
                }
            }
        }
    }
}