package models

import UserInfoResponse
import kotlinx.serialization.Serializable

@Serializable
sealed class Intent(private val type: String) {
    @Serializable
    data class SendAuthUrl(private val data: String): Intent("send_auth_url")

    @Serializable
    data class AuthSuccess(private val data: String): Intent("auth_success")

    @Serializable
    data class AuthError(private val data: String): Intent("auth_error")
}
