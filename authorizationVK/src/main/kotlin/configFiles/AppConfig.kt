package configFiles

import kotlinx.serialization.Serializable

@Serializable
data class AppConfig(
    val CLIENT_ID: String,
    val CLIENT_SECRET: String,
    val REDIRECT_URI: String
)