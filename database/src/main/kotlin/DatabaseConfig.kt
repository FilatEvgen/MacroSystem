package org.example

import kotlinx.serialization.Serializable

@Serializable
data class DatabaseConfig(
    val url: String,
    val driver: String,
    val user: String,
    val password: String
)

@Serializable
data class Config(
    val database: DatabaseConfig
)
