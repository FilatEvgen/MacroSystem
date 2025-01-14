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

            val authUrl = "https://id.vk.com/authorize?client_id=${Config.appConfig.CLIENT_ID}&redirect_uri=${Config.appConfig.REDIRECT_URI}&response_type=code&scope=vkid.personal_info&code_challenge=$codeChallenge&code_challenge_method=S256&state=$state"
            println("Auth URL: $authUrl")
            call.respondRedirect(authUrl)
        }

        get("/callback") {
            val code = call.parameters["code"]
            val state = call.parameters["state"]

            val session = call.sessions.get<CodeVerifierSession>()
            val codeVerifier = session?.codeVerifier

            if (codeVerifier == null) {
                call.respond(HttpStatusCode.BadRequest, "Code verifier not found")
                return@get
            }

            // Обмен кода на токены
            try {
                val tokenResponse = AuthService.exchangeCodeForTokens(code, codeVerifier)
                // Выводим токены в консоль или возвращаем их в ответе
                println("Access Token: ${tokenResponse.accessToken}")
                println("Refresh Token: ${tokenResponse.refreshToken}")
                println("ID Token: ${tokenResponse.idToken}")

                call.respondText("Authorization successful! Tokens received. Check console.")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error exchanging code for tokens: ${e.message}")
            }
        }
    }
}