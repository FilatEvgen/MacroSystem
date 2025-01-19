sealed class AuthEvent {
    data class StartAuthEvent(val authUrl: String) : AuthEvent()
    data class EndAuthEvent(val status: String, val userData: User? = null, val error: String? = null) : AuthEvent()
}