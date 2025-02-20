package org.example

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("access_token") val accessToken: String,
    @SerialName("id_token") val idToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("user_id") val userId: Long,
    @SerialName("state") val state: String,
    @SerialName("scope") val scope: String
)