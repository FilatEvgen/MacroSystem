plugins {
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kotlin.jvm)
    id("com.gradleup.shadow") version "8.3.5"
    id("java")
    application
}
application {
    mainClass.set("MainKt")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
dependencies{
    implementation(libs.io.ktor.server.core)
    implementation(libs.io.ktor.serialization.kotlinx.json)
    implementation(project(":server"))
}
tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    manifest {
        attributes(
            "Main-Class" to "ApplicationKt"
        )
    }
}


