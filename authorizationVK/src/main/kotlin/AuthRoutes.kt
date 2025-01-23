import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

// Обработчик маршрутов авторизации
fun Route.authRoutes() {
    post("/auth/callback") {
        // Извлекаем параметры из запроса
        val code = call.parameters["code"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing code parameter")
        val deviceId = call.parameters["device_id"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing device_id parameter")
        val state = call.parameters["state"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing state parameter")

        println("Received state: $state") // Логируем полученный state
        println("Received parameters: code=$code, device_id=$deviceId, state=$state")

        // Сохраняем параметры в кэш
        Cache.put(state, code, deviceId)
        println("Saved parameters: code=$code, deviceId=$deviceId for state=$state")

        // Возвращаем 200 OK
        call.respond(HttpStatusCode.OK)
    }
}