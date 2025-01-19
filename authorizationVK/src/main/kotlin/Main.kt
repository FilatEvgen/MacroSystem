import AuthEvent.StartAuthEvent
import configFiles.Config
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.AuthService
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
            // Отправка ссылки на авторизацию при подключении
            val (codeVerifier, codeChallenge, state) = AuthService.generatePKCE()
            call.sessions.set(CodeVerifierSession(codeVerifier))
            val authUrl =
                "https://id.vk.com/authorize?client_id=${Config.appConfig.CLIENT_ID}&redirect_uri=${Config.appConfig.REDIRECT_URI}&response_type=code&scope=email&code_challenge=$codeChallenge&code_challenge_method=S256&state=$state"

            // Отправляем событие StartAuthEvent
            send(Frame.Text(Json.encodeToString(StartAuthEvent(authUrl))))

            // Ожидание сообщений от клиента
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val message = frame.readText()
                    val parameters = parameterCache[state] ?: continue

                    // Извлекаем код из сообщения
                    val code = message
                    val deviceId = "test_device"

                    // Вызов функции обработки авторизации
                    handleAuthorization(code, deviceId, state, codeVerifier) { response ->
                        launch {
                            send(Frame.Text(response))
                        }
                    }
                }
            }
        }
    }
}