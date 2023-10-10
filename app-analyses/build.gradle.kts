val kotlin_version: String by project
val logback_version: String by project
val mockk_version: String by project

plugins {
    `java-library`
    kotlin("jvm")
}

group = "com.usvision.analyses"
version = "0.0.1"

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

group = "com.usvision.analyses"
version = "0.0.1"

dependencies {
    implementation(project(":app-model"))
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.mockk:mockk:${mockk_version}")
    testImplementation(kotlin("test"))
}