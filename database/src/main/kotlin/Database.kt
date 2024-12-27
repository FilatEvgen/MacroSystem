package org.example

import EventConfig
import LoopType
import Macro
import Macros
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

// Инициализация базы данных
fun initDatabase(config: Config) {
    Database.connect(
        config.database.url,
        driver = config.database.driver,
        user = config.database.user,
        password = config.database.password
    )
    transaction {
        SchemaUtils.create(Macros) // Создание таблицы
    }
}

// Вставка макросов

fun insertMacros(macros: List<Macro>) {
    transaction {
        macros.forEach { macro ->
            Macros.insert {
                it[description] = macro.description
                it[comment] = macro.comment
                it[startStopKey] = macro.startStopKey
                it[loopType] = macro.loopType.name
                it[keys] = Json.encodeToString(macro.keys) // Сериализация списка в JSON
            }
        }
    }
}

// Получение всех макросов
fun getAllMacros(): List<Macro> {
    return transaction {
        Macros.selectAll().map {
            Macro(
                description = it[Macros.description],
                comment = it[Macros.comment],
                startStopKey = it[Macros.startStopKey],
                loopType = LoopType.valueOf(it[Macros.loopType]),
                keys = Json.decodeFromString<List<EventConfig>>(it[Macros.keys]) // Десериализация из JSON
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
                    keys = Json.decodeFromString<List<EventConfig>>(it[Macros.keys]) // Десериализация из JSON
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