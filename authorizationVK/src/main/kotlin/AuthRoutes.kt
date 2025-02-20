import configFiles.Config
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.AuthService
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*

// Обработчик маршрутов авторизации
fun Route.authRoutes() {
    val config = Config.appConfig
    get("/auth/url") {
        println("Start route")
        try {
            val (codeVerifier, codeChallenge, state) = AuthService.generatePKCE()
            AuthCache.put(state, codeVerifier)
            val authUrl = "https://id.vk.com/authorize?client_id=${config.CLIENT_ID}&redirect_uri=${config.REDIRECT_URI}&response_type=code&scope=email&code_challenge=$codeChallenge&code_challenge_method=S256&state=$state"
            println(codeVerifier)
            call.respond(HttpStatusCode.OK, authUrl)
        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError, e.localizedMessage)
        }
    }
    post("/auth/callback") {
        val code = call.parameters["code"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing code parameter")
        val deviceId = call.parameters["device_id"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing device_id parameter")
        val state = call.parameters["state"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing state parameter")
        val codeVerifier = AuthCache.getCodeVerifier(state)?: return@post call.respond(HttpStatusCode.InternalServerError, "Not found code verifier")

        val tokenResponse = AuthService.exchangeCodeForTokens(code, codeVerifier, deviceId, state)
        val userInfo = AuthService.getUserInfo(tokenResponse.accessToken)

        call.respond(HttpStatusCode.OK, userInfo)
    }
}

fun generatePKCE(): Triple<String, String, String> {
    val codeVerifier = generateCodeVerifier()
    val codeChallenge = generateCodeChallenge(codeVerifier)
    val state = generateState() // Генерируем отдельный state
    return Triple(codeVerifier, codeChallenge, state)
}

private fun generateState(): String {
    val random = ByteArray(16)
    SecureRandom().nextBytes(random)
    return Base64.getUrlEncoder().withoutPadding().encodeToString(random)
}

private fun generateCodeVerifier(): String {
    val random = ByteArray(32)
    SecureRandom().nextBytes(random)
    return Base64.getUrlEncoder().withoutPadding().encodeToString(random)
}

private fun generateCodeChallenge(codeVerifier: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hash = digest.digest(codeVerifier.toByteArray())
    return Base64.getUrlEncoder().withoutPadding().encodeToString(hash)
}