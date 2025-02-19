package configFiles

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppConfig(
    @SerialName("client_id")
    val CLIENT_ID: String,
    @SerialName("client_secret")
    val CLIENT_SECRET: String,
    @SerialName("redirect_uri")
    val REDIRECT_URI: String
)