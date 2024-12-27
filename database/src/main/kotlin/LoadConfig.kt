package org.example

import kotlinx.serialization.json.Json
import java.io.File

fun loadConfig(filePath: String): Config {
    val jsonString = File(filePath).readText()
    return Json.decodeFromString(jsonString)
}