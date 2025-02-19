package ru.dragon_slayer.server

import Macro
import auth_flow.authWs
import deleteMacro
import getAllMacrosForUser
import getMacroById
import insertMacros
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import updateMacro

fun Application.macrosRouting() {
    routing {
        // Получение всех макросов конкретного пользователя
        get("/macros/user/{userId}") {
            val userId = call.parameters["userId"]?.toIntOrNull() ?: return@get call.respond(
                HttpStatusCode.BadRequest,"Неправильный Id пользователя" )
            try {
                val macros = getAllMacrosForUser(userId)
                call.respond(macros)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Ошибка при получении макросов.")
            }
        }

        // Получение макроса по ID
        get("/macros/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                "Неправильный ID макроса"
            )
            try {
                val macro = getMacroById(id)
                if (macro != null) {
                    call.respond(macro)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Макрос не найден")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Ошибка при получении макроса.")
            }
        }

        // Вставка нового макроса
        post("/macros") {
            val newMacro = call.receive<Macro>()
            try {
                insertMacros(listOf(newMacro))
                call.respond(HttpStatusCode.Created, "Макрос успешно добавлен.")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Ошибка при добавлении макроса.")
            }
        }

        // Обновление существующего макроса
        put("/macros/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(
                HttpStatusCode.BadRequest,
                "Неправильный ID макроса"
            )
            val updatedMacro = call.receive<Macro>()
            try {
                updateMacro(id, updatedMacro)
                call.respond(HttpStatusCode.OK, "Макрос успешно обновлен.")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Ошибка при обновлении макроса.")
            }
        }

        // Удаление макроса
        delete("/macros/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                "Неправильный ID макроса"
            )
            try {
                deleteMacro(id)
                call.respond(HttpStatusCode.NoContent) // 204 No Content
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Ошибка при удалении макроса.")
            }
        }
    }
}