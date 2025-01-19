import org.jetbrains.exposed.dao.id.IntIdTable

object Users : IntIdTable("users") {
    val userId = varchar("user_id", 255).uniqueIndex()
    val firstName = varchar("first_name", 255)
    val lastName = varchar("last_name", 255)
    val phone = varchar("phone", 50).nullable()
    val avatar = varchar("avatar", 255)
    val email = varchar("email", 255).nullable()
    val sex = integer("sex")
    val verified = bool("verified")
    val birthday = varchar("birthday", 50).nullable()
}