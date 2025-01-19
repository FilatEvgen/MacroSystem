import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoResponse(
    @SerialName("user") val user: User
)

@Serializable
data class User(
    @SerialName("user_id") val userId: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("phone") val phone: String?,
    @SerialName("avatar") val avatar: String,
    @SerialName("email") val email: String?,
    @SerialName("sex") val sex: Int, // 1 - женский, 2 - мужской, 0 - пол не указан
    @SerialName("verified") val verified: Boolean,
    @SerialName("birthday") val birthday: String?
)