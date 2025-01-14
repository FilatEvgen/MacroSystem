package configFiles

import kotlinx.serialization.json.Json
import java.io.File

object Config {
    private val json = Json { ignoreUnknownKeys = true }
    private val configFile = File("config.json")
    val appConfig: AppConfig = json.decodeFromString(configFile.readText())
}