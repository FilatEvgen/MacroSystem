import org.example.AuthService

suspend fun handleAuthorization(code: String, deviceId: String, state: String, codeVerifier: String, send: suspend (UserInfoResponse) -> Unit) {
    val tokenResponse = AuthService.exchangeCodeForTokens(code, codeVerifier, deviceId, state)
    val userInfo = AuthService.getUserInfo(tokenResponse.accessToken)
    send(userInfo)
}