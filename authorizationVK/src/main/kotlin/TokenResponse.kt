package org.example

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("id_token") val idToken: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("user_id") val userId: Int
)