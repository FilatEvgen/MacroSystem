package configFiles

import kotlinx.serialization.json.Json
import java.io.File

object Config {
    private val configFile = File(System.getenv("AUTH_CFG_PATH"))
    val appConfig: AppConfig = Json.decodeFromString(configFile.readText())
}