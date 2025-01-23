import configFiles.Config
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.example.AuthService
import java.io.File

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    install(WebSockets)

    val htmlBaseDir = System.getenv("HTML_BASE_DIR") ?: "static"
    frontModule(File(htmlBaseDir))

    routing {
        authRoutes()
    }
}

fun Application.frontModule(htmlBaseDir: File) {
    routing {
        staticFiles("/", htmlBaseDir, "index.html") {
        }
        get("/auth") {
            call.respondFile(File(htmlBaseDir, "index.html"))
        }

        webSocket("/ws") {
            try {
                // Генерируем PKCE
                val (codeVerifier, codeChallenge, state) = AuthService.generatePKCE()
                println("Generated codeVerifier: $codeVerifier")
                println("Generated state: $state")

                // Генерируем URL авторизации
                val authUrl = "https://id.vk.com/authorize?client_id=${Config.appConfig.CLIENT_ID}&redirect_uri=${Config.appConfig.REDIRECT_URI}&response_type=code&scope=email&code_challenge=$codeChallenge&code_challenge_method=S256&state=$state"
                println("Auth URL: $authUrl")
                send(Frame.Text(authUrl))

                // Цикл ожидания параметров
                while (isActive) {
                    delay(4000) // Задержка перед проверкой кэша
                    println("Checking cache for state: $state")
                    val cachedData = Cache.get(state)
                    if (cachedData != null) {
                        val code = cachedData.code
                        val deviceId = cachedData.deviceId

                        // Обработка авторизации
                        handleAuthorization(code, deviceId, state, codeVerifier) { response ->
                            if (this.isActive) {
                                launch {
                                    println("Sending response: $response")
                                    send(Frame.Text(response))
                                }
                            }
                        }
                        Cache.remove(state)
                        break
                    } else {
                        println("No parameters found for state: $state")
                    }
                }
            } catch (e: Exception) {
                println("Error in WebSocket: ${e.message}")
                if (this.isActive) {
                    send(Frame.Text("Error: ${e.message}"))
                }
            }
        }
    }
}