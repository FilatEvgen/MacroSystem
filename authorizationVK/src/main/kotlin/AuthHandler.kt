import AuthEvent.EndAuthEvent
import org.example.AuthService
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

suspend fun handleAuthorization(code: String, deviceId: String, state: String, codeVerifier: String, send: (String) -> Unit) {
    try {
        // Обмен кода на токены
        val tokenResponse = AuthService.exchangeCodeForTokens(code, codeVerifier, deviceId, state)

        // Получение информации о пользователе
        val userInfo = AuthService.getUserInfo(tokenResponse.accessToken)

        // Сохранение данных пользователя в базе данных
        insertUser (userInfo) // Вызов функции из модуля базы данных

        // Создание события EndAuthEvent
        val endAuthEvent = EndAuthEvent("Success", userData = userInfo.user)

        // Отправка данных пользователю через WebSocket
        send(Json.encodeToString(endAuthEvent))
    } catch (e: Exception) {
        // Обработка ошибок
        val endAuthEvent = EndAuthEvent("Error", error = e.message)
        send(Json.encodeToString(endAuthEvent))
    }
}