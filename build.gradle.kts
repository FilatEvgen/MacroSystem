plugins {
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kotlin.jvm)
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.jar {
    archiveBaseName.set("macros_system")
    archiveVersion.set(version.toString())
}