val mockk_version: String by project

plugins {
    kotlin("jvm")
}

group = "com.usvision.creation"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":app-model"))
    testImplementation("io.mockk:mockk:${mockk_version}")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}