package models

import UserInfoResponse
import kotlinx.serialization.Serializable

@Serializable
sealed class Intent(private val type: String) {
    @Serializable
    data class SendAuthUrl(private val url: String): Intent("send_auth_url")

    @Serializable
    data class AuthSuccess(private val user: UserInfoResponse): Intent("auth_success")

    @Serializable
    data class AuthError(private val message: String): Intent("auth_error")
}
