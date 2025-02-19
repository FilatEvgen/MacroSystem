package auth_flow

import Cache
import configFiles.Config
import handleAuthorization
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import models.Intent
import org.example.AuthService
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

suspend fun WebSocketServerSession.authWs() {
    val config = Config.appConfig
    val scope = CoroutineScope(SupervisorJob() + coroutineContext)
    var state = ""
    var codeVerifier = ""
    var codeChallenge: String

    scope.launch {
        AuthService.generatePKCE().also {
            codeVerifier = it.first
            codeChallenge = it.second
            state = it.third
        }
        val authUrl = "https://id.vk.com/authorize?client_id=${config.CLIENT_ID}&redirect_uri=${config.REDIRECT_URI}&response_type=code&scope=email&code_challenge=$codeChallenge&code_challenge_method=S256&state=$state"
        sendSerialized(Intent.SendAuthUrl(authUrl))
    }

    incoming.tryReceive()

    try {
        withTimeoutOrNull(5.minutes) {
            while (isActive) {
                delay(5.seconds)
                val cachedData = Cache.get(state)
                if (cachedData != null) {
                    val code = cachedData.code
                    val deviceId = cachedData.deviceId

                    try {
                        handleAuthorization(code, deviceId, state, codeVerifier) { userInfoResponse ->
                            sendSerialized(Intent.AuthSuccess(userInfoResponse))
                            closeSession(scope)
                        }
                    } catch (e: Exception) {
                        handleAuthError(e.localizedMessage, scope)
                    }

                    Cache.remove(state)
                    break
                }
            }
        }?: run { handleAuthError("Authorization timeout", scope) }
    } catch (e: Exception) {
        handleAuthError("Internal server error: ${e.localizedMessage}", scope)
    }
}

private suspend fun WebSocketServerSession.handleAuthError(message: String, scope: CoroutineScope) {
    sendSerialized(Intent.AuthError(message))
    closeSession(scope)
}

private suspend fun WebSocketServerSession.closeSession(scope: CoroutineScope) {
    scope.cancel()
    close()
}