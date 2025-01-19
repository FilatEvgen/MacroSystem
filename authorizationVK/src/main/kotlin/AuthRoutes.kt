import configFiles.Config
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.example.AuthService
import org.example.CodeVerifierSession

// Кэш для хранения параметров
val parameterCache = mutableMapOf<String, Map<String, String>>()

fun Route.authRoutes() {
    route("/auth") {
        get("/initiate") {
            val (codeVerifier, codeChallenge, state) = AuthService.generatePKCE()
            call.sessions.set(CodeVerifierSession(codeVerifier))
            println("Code Verifier: $codeVerifier")
            val authUrl = "https://id.vk.com/authorize?client_id=${Config.appConfig.CLIENT_ID}&redirect_uri=${Config.appConfig.REDIRECT_URI}&response_type=code&scope=email&code_challenge=$codeChallenge&code_challenge_method=S256&state=$state"
            println("Auth URL: $authUrl")
            call.respondText(authUrl) // Возвращаем ссылку на авторизацию
        }

        post("/callback") {
            // Извлекаем параметры из запроса
            val code = call.parameters["code"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing code parameter")
            val deviceId = call.parameters["device_id"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing device_id parameter")
            val state = call.parameters["state"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing state parameter")

            println("Received parameters: code=$code, device_id=$deviceId, state=$state")

            // Сохраняем параметры в кэш
            val parameters = mapOf("code" to code, "device_id" to deviceId, "state" to state)
            parameterCache[state] = parameters
            println("Saved parameters: $parameters")

            // Возвращаем 200 OK
            call.respond(HttpStatusCode.OK)
        }
    }
}