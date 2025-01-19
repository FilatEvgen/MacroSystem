import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

// Вставка пользователя
fun insertUser (userInfo: UserInfoResponse) {
    transaction {
        Users.insert {
            it[userId] = userInfo.user.userId // Идентификатор пользователя VK
            it[firstName] = userInfo.user.firstName
            it[lastName] = userInfo.user.lastName
            it[phone] = userInfo.user.phone ?: ""
            it[avatar] = userInfo.user.avatar
            it[email] = userInfo.user.email
            it[sex] = userInfo.user.sex
            it[verified] = userInfo.user.verified
            it[birthday] = userInfo.user.birthday
        }
    }
}

// Получение пользователя по userId
fun getUserById(userId: String): UserInfoResponse? {
    return transaction {
        Users.selectAll().where { Users.userId eq userId }
            .mapNotNull {
                UserInfoResponse(
                    user = User(
                        userId = it[Users.userId],
                        firstName = it[Users.firstName],
                        lastName = it[Users.lastName],
                        phone = it[Users.phone]?: "",
                        avatar = it[Users.avatar],
                        email = it[Users.email],
                        sex = it[Users.sex],
                        verified = it[Users.verified],
                        birthday = it[Users.birthday]?: ""
                    )
                )
            }.singleOrNull()
    }
}

// Обновление пользователя
fun updateUser (userInfo: UserInfoResponse) {
    transaction {
        Users.update({ Users.userId eq userInfo.user.userId }) {
            it[firstName] = userInfo.user.firstName
            it[lastName] = userInfo.user.lastName
            it[phone] = userInfo.user.phone ?: ""
            it[avatar] = userInfo.user.avatar
            it[email] = userInfo.user.email
            it[sex] = userInfo.user.sex
            it[verified] = userInfo.user.verified
            it[birthday] = userInfo.user.birthday
        }
    }
}