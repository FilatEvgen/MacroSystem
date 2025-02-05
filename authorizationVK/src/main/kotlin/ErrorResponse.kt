import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val error: String,
    val errorDescription: String? = null
)