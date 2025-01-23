import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

// Кэш для хранения параметров
val parameterCache = mutableMapOf<String, Map<String, String>>()

fun Route.authRoutes() {
    post("/auth/callback") {
        // Извлекаем параметры из запроса
        val code = call.parameters["code"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing code parameter")
        val deviceId = call.parameters["device_id"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing device_id parameter")
        val state = call.parameters["state"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing state parameter")

        println("Received state: $state") // Логируем полученный state
        println("Received parameters: code=$code, device_id=$deviceId, state=$state")

        // Сохраняем параметры в кэш
        val parameters = mapOf("code" to code, "device_id" to deviceId, "state" to state)
        parameterCache[state] = parameters
        println("Saved parameters: $parameters")

        // Возвращаем 200 OK
        call.respond(HttpStatusCode.OK)
    }
}