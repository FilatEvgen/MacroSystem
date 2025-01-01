import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.connectToTestDatabase
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplicationTest {
    @BeforeTest
    fun setup() {
        connectToTestDatabase()
        // Добавление начальных данных
        transaction {
            val macro = Macro(
                userId = 1, // Убедитесь, что вы указываете userId
                description = "Example Macro",
                comment = "This is an example macro.",
                startStopKey = 1,
                loopType = LoopType.INFINITE,
                keys = listOf(EventConfig(eventKey = 1))
            )
            insertMacros(listOf(macro)) // Вставка начального макроса
        }
    }


    @Test
    fun testGetMacrosForUser () {
        testApplication {
            application { module() }

            // Выполнение запроса
            val response = client.get("/macros/user/1")
            assertEquals(HttpStatusCode.OK, response.status)

            // Проверка содержимого ответа
            val macros = response.body<List<Macro>>() // Предполагаем, что ответ - это список макросов
            assertTrue(macros.isNotEmpty(), "Список макросов не должен быть пустым")
            assertEquals("Example Macro", macros[0].description)
            assertEquals("This is an example macro.", macros[0].comment)
            assertEquals(1, macros[0].startStopKey)
            assertEquals(LoopType.INFINITE, macros[0].loopType)
            assertTrue(macros[0].keys.isNotEmpty(), "Список ключей не должен быть пустым")
        }
    }

    @Test
    fun testGetMacroById() {
        testApplication {
            application { module() }

            val response = client.get("/macros/1")
            assertEquals(HttpStatusCode.OK, response.status)

            // Проверка содержимого ответа
            val macro = response.body<Macro>() // Предполагаем, что ответ - это макрос
            assertEquals("Example Macro", macro.description)
            assertEquals("This is an example macro.", macro.comment)
            assertEquals(1, macro.startStopKey)
            assertEquals(LoopType.INFINITE, macro.loopType)
            assertTrue(macro.keys.isNotEmpty(), "Список ключей не должен быть пустым")
        }
    }

    @Test
    fun testInsertMacro() {
        testApplication {
            application { module() }

            val newMacro = Macro(
                description = "New Macro",
                comment = "Test Macro",
                startStopKey = 2,
                loopType = LoopType.ONCE,
                keys = listOf(EventConfig(eventKey = 2)),
                userId = 1
            )

            val response = client.post("/macros") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(newMacro)) // Используем сериализацию
            }
            assertEquals(HttpStatusCode.Created, response.status)

            // Проверка, что новый макрос был добавлен
            val createdMacroResponse = client.get("/macros/user/1")
            val createdMacros = createdMacroResponse.body<List<Macro>>()
            assertTrue(createdMacros.any { it.description == "New Macro" }, "Созданный макрос должен быть в списке макросов пользователя")
        }
    }

    @Test
    fun testUpdateMacro() {
        testApplication {
            application { module() }

            val updatedMacro = Macro(
                description = "Updated Macro",
                comment = "Updated Test Macro",
                startStopKey = 1,
                loopType = LoopType.ONCE,
                keys = listOf(EventConfig(eventKey = 1)),
                userId = 1
            )

            val response = client.put("/macros/1") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(updatedMacro)) // Используем сериализацию
            }
            assertEquals(HttpStatusCode.OK, response.status)

            // Проверка обновленного макроса
            val updatedMacroResponse = client.get("/macros/1")
            val updatedMacroFromResponse = updatedMacroResponse.body<Macro>()
            assertEquals("Updated Macro", updatedMacroFromResponse.description)
            assertEquals("Updated Test Macro", updatedMacroFromResponse.comment)
            assertEquals(LoopType.ONCE, updatedMacroFromResponse.loopType)
            assertTrue(updatedMacroFromResponse.keys.isNotEmpty(), "Список ключей не должен быть пустым")
        }
    }

    @Test
    fun testDeleteMacro() {
        testApplication {
            application { module() }

            // Удаление макроса
            val response = client.delete("/macros/1")
            assertEquals(HttpStatusCode.NoContent, response.status)

            // Проверка, что макрос был удален
            val deletedMacroResponse = client.get("/macros/1")
            assertEquals(HttpStatusCode.NotFound, deletedMacroResponse.status)
        }
    }
}