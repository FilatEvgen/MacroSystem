plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    application
}
application {
    mainClass = "MainKt"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.io.ktor.server.core)
    implementation(libs.io.ktor.server.netty)
    implementation(libs.io.ktor.serialization.kotlinx.json)
    implementation(libs.io.ktor.server.content.negotiation)
    implementation(libs.exposed.core)

    implementation(project(":database"))
    // Test implementation
//    testImplementation("io.ktor:ktor-server-test-host:3.0.2")
//    testImplementation("org.jetbrains.kotlin:kotlin-test:2.0.21")
//    testImplementation("io.ktor:ktor-client-mock:3.0.2")
//    testImplementation("com.h2database:h2:2.3.232")
}


