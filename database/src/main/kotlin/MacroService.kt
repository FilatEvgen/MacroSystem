import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

// Вставка макросов
fun insertMacros(macros: List<Macro>) {
    transaction {
        macros.forEach { macro ->
            Macros.insert {
                it[description] = macro.description
                it[comment] = macro.comment
                it[startStopKey] = macro.startStopKey
                it[loopType] = macro.loopType.name
                it[keys] = Json.encodeToString(macro.keys) // Сериализация списка в JSON]
                it[userId] = macro.userId
            }
        }
    }
}

// Получение всех макросов
fun getAllMacrosForUser (userId: Int): List<Macro> {
    return transaction {
        Macros.select(Macros.userId eq userId).map {
            Macro(
                description = it[Macros.description],
                comment = it[Macros.comment],
                startStopKey = it[Macros.startStopKey],
                loopType = LoopType.valueOf(it[Macros.loopType]),
                keys = Json.decodeFromString<List<EventConfig>>(it[Macros.keys]), // Десериализация из JSON
                userId = it[Macros.userId]
            )
        }
    }
}

// Обновление макроса
fun updateMacro(id: Int, updatedMacro: Macro) {
    transaction {
        Macros.update({ Macros.id eq id }) {
            it[description] = updatedMacro.description
            it[comment] = updatedMacro.comment
            it[startStopKey] = updatedMacro.startStopKey
            it[loopType] = updatedMacro.loopType.name
            it[keys] = Json.encodeToString(updatedMacro.keys) // Сериализация списка в JSON
            it[userId] = updatedMacro.userId
        }
    }
}

// Получение макроса по ID
fun getMacroById(id: Int): Macro? {
    return transaction {
        Macros.selectAll().where { Macros.id eq id }
            .mapNotNull {
                Macro(
                    description = it[Macros.description],
                    comment = it[Macros.comment],
                    startStopKey = it[Macros.startStopKey],
                    loopType = LoopType.valueOf(it[Macros.loopType]),
                    keys = Json.decodeFromString<List<EventConfig>>(it[Macros.keys]), // Десериализация из JSON
                    userId = it[Macros.userId]
                )
            }.singleOrNull() // Возвращает единственный элемент или null
    }
}

// Удаление макроса
fun deleteMacro(id: Int) {
    transaction {
        Macros.deleteWhere { Macros.id eq id }
    }
}