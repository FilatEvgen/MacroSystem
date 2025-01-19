import AuthEvent.EndAuthEvent
import org.example.AuthService
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

suspend fun handleAuthorization(code: String, deviceId: String, state: String, codeVerifier: String, send: (String) -> Unit) {
    try {
        println("Exchanging code for tokens...")
        // Обмен кода на токены
        val tokenResponse = AuthService.exchangeCodeForTokens(code, codeVerifier, deviceId, state)
        println("Token response received: $tokenResponse")

        println("Getting user info...")
        // Получение информации о пользователе
        val userInfo = AuthService.getUserInfo(tokenResponse.accessToken)
        println("User  info received: $userInfo")

        // Сохранение данных пользователя в базе данных
        insertUser (userInfo) // Вызов функции из модуля базы данных
        println("User  data inserted into the database.")

        // Создание события EndAuthEvent
        val endAuthEvent = EndAuthEvent("Success", userData = userInfo.user)

        // Отправка данных пользователю через WebSocket
        send(Json.encodeToString(endAuthEvent))
        println("EndAuthEvent sent successfully.")
    } catch (e: Exception) {
        // Обработка ошибок
        println("Error during authorization: ${e.message}")
        val endAuthEvent = EndAuthEvent("Error", error = e.message)
        send(Json.encodeToString(endAuthEvent))
    }
}