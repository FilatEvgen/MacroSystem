package org.example

import Macros
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
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
fun connectToTestDatabase() {
    Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        driver = "org.h2.Driver",
        user = "sa",
        password = "")
    transaction {
        SchemaUtils.create(Macros)
    }
}
//
