plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "DragonSlayerBackend"
include("server")
include("database")
include("authorizationVK")
include("common")
include("http_client")
