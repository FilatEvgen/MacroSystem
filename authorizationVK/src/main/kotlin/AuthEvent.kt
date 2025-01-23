import kotlinx.serialization.Serializable

sealed class AuthEvent {
    @Serializable
    data class StartAuthEvent(
        val authUrl: String
    )
    @Serializable
    data class EndAuthEvent(
        val status: String,
        val userData: User? = null,
        val error: String? = null
    )
}