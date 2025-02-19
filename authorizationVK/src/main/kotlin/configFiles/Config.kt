package configFiles

import kotlinx.serialization.json.Json
import java.io.File

object Config {
    private val configFile = File("config.json")
    val appConfig: AppConfig = Json.decodeFromString(configFile.readText())
}