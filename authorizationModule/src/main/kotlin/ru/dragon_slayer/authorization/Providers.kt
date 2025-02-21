package ru.dragon_slayer.authorization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.dragon_slayer.http.client.BodyBuilderUrlEncodedForm
import ru.dragon_slayer.http.client.HttpClientFactory
import ru.dragon_slayer.http.client.impl.DirectCommunicator
import java.io.File


interface IProvider {
    val appData: AppData
    fun buildAuthorizeUrl(): String
    suspend fun getAuthorizationTokens(body: BodyBuilderUrlEncodedForm): TokensResponse
    suspend fun getUserInfo(body: BodyBuilderUrlEncodedForm): UserResponse

    @Serializable
    data class TokensResponse(
        @SerialName("refresh_token") val refreshToken: String,
        @SerialName("access_token") val accessToken: String,
        @SerialName("id_token") val idToken: String,
        @SerialName("token_type") val tokenType: String,
        @SerialName("expires_in") val expiresIn: Long,
        @SerialName("user_id") val userId: Long,
        @SerialName("state") val state: String,
        @SerialName("scope") val scope: String
    )

    @Serializable
    data class UserResponse(
        @SerialName("user") val user: User
    )

    @Serializable
    data class User(
        @SerialName("user_id") val userId: String,
        @SerialName("first_name") val firstName: String,
        @SerialName("last_name") val lastName: String,
        @SerialName("phone") val phone: String,
        @SerialName("avatar") val avatar: String,
        @SerialName("email") val email: String,
        @SerialName("sex") val sex: Int,
        @SerialName("is_verified") val isVerified: Boolean,
        @SerialName("birthday") val birthday: String
    )
}
@Serializable
data class AuthConfig(
    @SerialName("vk_provider") val vk: AppData
)
@Serializable
data class AppData(
    @SerialName("client_id")val clientId: Int,
    @SerialName("redirect_uri")val redirectUri: String
)
object AuthenticateProvider {
    private const val VK_ID = "vk_id"
    private const val MAIL_RU = "mail_ru"
    private val config = Json.decodeFromString<AuthConfig>(File(System.getenv("AUTH_CONFIG_PATH")).readText())

    fun getProvider(value: String): IProvider {
        return when (value) {
            VK_ID -> VkIdProvider(config.vk)
            MAIL_RU -> MailRuProvider(config.vk)
            else -> throw IllegalArgumentException("Unknown provider: $value")
        }
    }
}

class VkIdProvider(override val appData: AppData) : IProvider {
    private val communicator = DirectCommunicator(HttpClientFactory.create())
    override fun buildAuthorizeUrl(): String {
        val pkceParams = PKCEGenerator.generatePKCE()
        return "https://id.vk.com/authorize?client_id=${appData.clientId}&redirect_uri=${appData.redirectUri}&response_type=code&scope=email&code_challenge=${pkceParams.codeChallenge}&code_challenge_method=S256&state=${pkceParams.state}"
    }

    override suspend fun getAuthorizationTokens(body: BodyBuilderUrlEncodedForm): IProvider.TokensResponse {
        val response = communicator.post(
            url = "https://id.vk.com/oauth2/auth",
            body = body
        )
        return Json.decodeFromString<IProvider.TokensResponse>(response.bodyAsText())
    }

    override suspend fun getUserInfo(body: BodyBuilderUrlEncodedForm): IProvider.UserResponse {
        val response = communicator.post(
            url = "https://id.vk.com/oauth2/user_info",
            body = body
        )
        return Json.decodeFromString<IProvider.UserResponse>(response.bodyAsText())
    }
}
class MailRuProvider(appData: AppData): IProvider {
    override val appData: AppData
        get() = TODO("Not yet implemented")

    override fun buildAuthorizeUrl(): String {
        return ""
    }

    override suspend fun getAuthorizationTokens(body: BodyBuilderUrlEncodedForm): IProvider.TokensResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getUserInfo(body: BodyBuilderUrlEncodedForm): IProvider.UserResponse {
        TODO("Not yet implemented")
    }
}