val kotlin_version: String by project
val logback_version: String by project
val mockk_version: String by project

plugins {
    `java-library`
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
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
    implementation(project(":app-analyses"))
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    testImplementation("io.mockk:mockk:${mockk_version}")
    testImplementation(kotlin("test"))
    implementation(kotlin("reflect"))
}
