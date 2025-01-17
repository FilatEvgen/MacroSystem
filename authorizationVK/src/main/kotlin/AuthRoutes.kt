import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.example.AuthService
import org.example.CodeVerifierSession
import configFiles.Config

fun Route.authRoutes() {
    route("/auth") {
        get("/initiate") {
            val (codeVerifier, codeChallenge, state) = AuthService.generatePKCE()
            call.sessions.set(CodeVerifierSession(codeVerifier))
            println("Code Verifier $codeVerifier")
            val authUrl = "https://id.vk.com/authorize?client_id=${Config.appConfig.CLIENT_ID}&redirect_uri=${Config.appConfig.REDIRECT_URI}&response_type=code&scope=vkid.personal_info&code_challenge=$codeChallenge&code_challenge_method=S256&state=$state"
            println("Auth URL: $authUrl")
            call.respondText(authUrl) // Возвращаем ссылку на авторизацию
        }

        post("/callback") {
            val code = call.parameters["code"]
            val deviceId = call.parameters["device_id"]
            val state = call.parameters["state"]

            val session = call.sessions.get<CodeVerifierSession>()
            val codeVerifier = session?.codeVerifier

            if (codeVerifier == null) {
                call.respond(HttpStatusCode.BadRequest, "Code verifier not found")
                return@post
            }

            if (deviceId.isNullOrEmpty()) {
                call.respond(HttpStatusCode.BadRequest, "Device ID not found")
                return@post
            }

            if (state.isNullOrEmpty()) {
                call.respond(HttpStatusCode.BadRequest, "State not found")
                return@post
            }

            // Обмен кода на токены
            try {
                val tokenResponse = AuthService.exchangeCodeForTokens(code, codeVerifier, deviceId, state)
                // Возвращаем токены в ответе
                call.respondText("Access Token: ${tokenResponse.accessToken}, Refresh Token: ${tokenResponse.refreshToken}, ID Token: ${tokenResponse.idToken}")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error exchanging code for tokens: ${e.message}")
            }
        }
    }
}