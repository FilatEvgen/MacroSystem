plugins {
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kotlin.jvm)
    id ("com.gradleup.shadow") version "9.0.0-beta4"
    id ("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.jar {
    archiveBaseName.set("macro_system")
    archiveVersion.set(version.toString())
}

tasks.shadowJar {
    manifest {
        attributes("Main-Class" to "server.Application") // Укажите ваш основной класс
    }
}