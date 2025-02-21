package ru.dragon_slayer.authorization

import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.dragon_slayer.http.client.BodyBuilderUrlEncodedForm
import java.security.InvalidParameterException

suspend fun RoutingContext.handleGetUrl() {
    val providerParameter = call.parameters["provider"]?: throw InvalidParameterException("provider - the key parameter was not passed")
    val provider = AuthenticateProvider.getProvider(providerParameter)
    val authenticateUrl = provider.buildAuthorizeUrl()
    call.respond(HttpStatusCode.OK, authenticateUrl)
}

suspend fun RoutingContext.handleAuthenticateCallback() {
    val parameters = call.receiveParameters()
    val providerParameter = parameters["provider"]?: throw InvalidParameterException("provider - the key parameter was not passed")
    val code = parameters["code"]?: throw InvalidParameterException("code - the key parameter was not passed")
    val deviceId = parameters["device_id"]?: throw InvalidParameterException("device_id - the key parameter was not passed")
    val state = parameters["state"]?: throw InvalidParameterException("state - the key parameter was not passed")
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

    try {
        val tokensResponse = provider.getAuthorizationTokens(tokensBody)
        val userBody = BodyBuilderUrlEncodedForm.build(
            listOf(
                Pair("client_id", provider.appData.clientId.toString()),
                Pair("access_token", tokensResponse.accessToken),
            )
        )
        val userResponse = provider.getUserInfo(userBody)
        call.respond(HttpStatusCode.OK, userResponse)
    } catch (e: Exception) {
        e.printStackTrace()
        call.respond(HttpStatusCode.InternalServerError, e.localizedMessage)
    }
}