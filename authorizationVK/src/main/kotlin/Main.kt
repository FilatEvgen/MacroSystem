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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
            val (codeVerifier, codeChallenge, state) = AuthService.generatePKCE()
            call.sessions.set(CodeVerifierSession(codeVerifier))

            val authUrl = "https://id.vk.com/authorize?client_id=${Config.appConfig.CLIENT_ID}&redirect_uri=${Config.appConfig.REDIRECT_URI}&response_type=code&scope=email&code_challenge=$codeChallenge&code_challenge_method=S256&state=$state"

            println("Auth URL: $authUrl")

            while (true) {
                delay(1000)
                println("Checking parameters for state: $state")

                val parameters = parameterCache[state]
                if (parameters != null) {
                    val code = parameters["code"]
                    val deviceId = parameters["device_id"]

                    if (code != null && deviceId != null) {
                        handleAuthorization(code, deviceId, state, codeVerifier) { response ->
                            launch {
                                send(Frame.Text(response))
                            }
                        }

                        break
                    } else {
                        println("Code or Device ID is null for state: $state")
                    }
                } else {
                    println("No parameters found for state: $state")
                }
            }
        }
    }
}
