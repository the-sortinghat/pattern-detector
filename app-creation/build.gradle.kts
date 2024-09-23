val mockk_version: String by project

plugins {
    `java-library`
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "com.usvision.reports"
version = "0.0.1"

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":app-model"))
    testImplementation("io.mockk:mockk:${mockk_version}")
    testImplementation(kotlin("test"))
    implementation(project(":app-model"))
}
