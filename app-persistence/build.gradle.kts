val mockk_version: String by project
val kotlinx_coroutine_version: String by project
val mongodb_version: String by project

plugins {
    `java-library`
    kotlin("jvm")
}

group = "com.usvision.persistence"
version = "0.0.1"

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":app-model"))
    implementation(project(":app-reports"))
    implementation(project(":app-creation"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinx_coroutine_version")
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:$mongodb_version")

    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:$mockk_version")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
}