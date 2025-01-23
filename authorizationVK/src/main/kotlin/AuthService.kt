package org.example

import UserInfoResponse
import configFiles.AppConfig
import configFiles.Config
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*

object AuthService {
    private val config: AppConfig = Config.appConfig // Загружаем конфигурацию

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

    suspend fun exchangeCodeForTokens(
        code: String?,
        codeVerifier: String,
        deviceId: String,
        state: String
    ): TokenResponse {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        try {
            // Отправляем POST-запрос на обмен кода на токены
            val response: TokenResponse = client.post("https://id.vk.com/oauth2/auth") {
                contentType(ContentType.Application.FormUrlEncoded)
                parameter("grant_type", "authorization_code")
                parameter("code_verifier", codeVerifier) // Передаем codeVerifier
                parameter("redirect_uri", config.REDIRECT_URI) // Убедитесь, что это правильный URI
                parameter("code", code)
                parameter("client_id", config.CLIENT_ID)
                parameter("device_id", deviceId)
                parameter("state", state) // Добавляем state, если это необходимо
            }.body()

            client.close()
            return response
        } catch (e: Exception) {
            // Обработка ошибок
            println("Error during token exchange: ${e.message}")
            throw e
        }
    }

    suspend fun getUserInfo(accessToken: String): UserInfoResponse {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }

        // Отправляем POST-запрос для получения информации о пользователе
        val response: UserInfoResponse = client.post("https://id.vk.com/oauth2/user_info") {
            contentType(ContentType.Application.FormUrlEncoded)
            parameter("client_id", Config.appConfig.CLIENT_ID)
            parameter("access_token", accessToken)
        }.body()

        client.close()
        return response
    }
}