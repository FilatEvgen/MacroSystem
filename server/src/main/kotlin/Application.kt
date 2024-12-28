import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import org.example.Config
import org.example.initDatabase
import org.example.loadConfig

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    // Инициализация базы данных
    val config: Config = loadConfig("config.json")
    initDatabase(config)

    routing {
        macrosRouting() // Вызов функции для маршрутов макросов
    }
}

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}