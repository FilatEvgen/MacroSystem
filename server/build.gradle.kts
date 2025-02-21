plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.shadow)
    application
}

application {
    mainClass = "ru.dragon_slayer.server.ApplicationKt"
}

repositories {
    mavenCentral()
}

val appModules = listOf(
    project(":authorizationModule"),
    project(":database"),
    project(":errorHandler")
)
dependencies {
    implementation(libs.io.ktor.server.core)
    implementation(libs.io.ktor.server.netty)
    implementation(libs.io.ktor.serialization.kotlinx.json)
    implementation(libs.io.ktor.server.content.negotiation)
    implementation(libs.io.ktor.server.sessions)
    implementation(libs.io.ktor.server.websockets)
    implementation(libs.io.ktor.server.statusPages)
    implementation(libs.exposed.core)
    appModules.forEach { implementation(it) }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    manifest {
        attributes(
            "Main-Class" to "ru.dragon_slayer.server.ApplicationKt"
        )
    }
}

task("startServer", type = JavaExec::class) {
    description = "Dragon Slayer Server"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("ru.dragon_slayer.server.ApplicationKt")
}

