plugins {
    application
    kotlin("jvm")
    id("io.ktor.plugin") version "2.3.5"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "com.usvision.web"
version = "0.0.1"

application {
    mainClass.set("com.usvision.web.ApplicationKt")
}

tasks.shadowJar {
    manifest {
        attributes(
            Pair("Main-Class", "com.usvision.web.ApplicationKt")
        )
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":app-reports"))
    implementation(project(":app-persistence"))

    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-cors")
    implementation("io.ktor:ktor-server-cors-jvm")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-status-pages")
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-host-common-jvm")
    implementation("io.ktor:ktor-server-status-pages-jvm")
    implementation("io.ktor:ktor-server-config-yaml")
}