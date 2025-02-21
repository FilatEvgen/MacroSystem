package ru.dragon_slayer.authorization

import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.dragon_slayer.error_handler.respondInvalidParameters
import ru.dragon_slayer.error_handler.respondNotFound
import ru.dragon_slayer.http.client.BodyBuilderUrlEncodedForm
import java.security.InvalidParameterException

suspend fun RoutingContext.handleGetUrl() {
    safeCall {
        val providerParameter = call.parameters["provider"]?: throw IllegalArgumentException("provider - the key parameter was not passed")
        val provider = AuthenticateProvider.getProvider(providerParameter)
        val authenticateUrl = provider.buildAuthorizeUrl()
        call.respond(HttpStatusCode.OK, authenticateUrl)
    }
}

suspend fun RoutingContext.handleAuthenticateCallback() {
    safeCall {
        val parameters = call.receiveParameters()
        val providerParameter = parameters["provider"]?: throw IllegalArgumentException("provider - the key parameter was not passed")
        val code = parameters["code"]?: throw IllegalArgumentException("code - the key parameter was not passed")
        val deviceId = parameters["device_id"]?: throw IllegalArgumentException("device_id - the key parameter was not passed")
        val state = parameters["state"]?: throw IllegalArgumentException("state - the key parameter was not passed")
        val codeVerifier = AuthCache.getCodeVerifier(state)?: throw NotFoundException("code verifier is not found for cache")
        val provider = AuthenticateProvider.getProvider(providerParameter)

        val tokensBody = BodyBuilderUrlEncodedForm.build(
            listOf(
                Pair("grant_type", "authorization_code"),
                Pair("code_verifier", codeVerifier),
                Pair("redirect_uri", provider.appData.redirectUri),
                Pair("code", code),
                Pair("client_id", provider.appData.clientId.toString()),
                Pair("device_id", deviceId),
                Pair("state", state)
            )
        )
        val tokensResponse = provider.getAuthorizationTokens(tokensBody)

        val userBody = BodyBuilderUrlEncodedForm.build(
            listOf(
                Pair("client_id", provider.appData.clientId.toString()),
                Pair("access_token", tokensResponse.accessToken),
            )
        )
        val userResponse = provider.getUserInfo(userBody)
        call.respond(HttpStatusCode.OK, userResponse)
    }
}
private suspend fun RoutingContext.safeCall(runBlock: suspend () -> Unit) {
    try { runBlock() } catch (e: Exception) {
        when (e) {
            is IllegalArgumentException -> call.respondInvalidParameters(e)
            is NotFoundException -> call.respondNotFound(e)
        }
    }
}