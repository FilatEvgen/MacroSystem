plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.io.ktor.server.core)
    implementation(libs.io.ktor.server.netty)
    implementation(libs.io.ktor.serialization.kotlinx.json)
    implementation(libs.io.ktor.server.content.negotiation)
    implementation(libs.io.ktor.server.sessions)
    implementation(libs.io.ktor.client.core)
    implementation(libs.io.ktor.client.cio)
    implementation(libs.io.ktor.client.json)
    implementation(libs.io.ktor.client.serialization)
    implementation(libs.io.ktor.client.content.negotiation)
    implementation(libs.io.ktor.server.websockets)
    implementation(project(":common"))
    implementation(project(":database"))
}
