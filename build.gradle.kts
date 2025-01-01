
plugins {
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kotlin.jvm)
//    id("com.github.johnrengelman.shadow") version "9.0.0-beta4" // Используйте стабильную версию
//    id("java")
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

//// Настройка задачи shadowJar
//tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
//    manifest {
//        attributes["Main-Class"] = "server.ApplicationKt" // Укажите правильный путь к вашему классу
//    }
//}