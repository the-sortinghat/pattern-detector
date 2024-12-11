val mockk_version: String by project
val kafka_clients_version: String by project
val kafka_json_serializer_version: String by project


plugins {
    application
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "com.usvision.async"
version = "0.0.1"

repositories {
    mavenCentral()
    maven(url = "https://packages.confluent.io/maven/")
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":app-creation"))
    implementation(project(":app-model"))
    implementation(project(":app-persistence"))

    implementation("org.apache.kafka:kafka-clients:${kafka_clients_version}")
    implementation("io.confluent:kafka-json-serializer:${kafka_json_serializer_version}")


    testImplementation("io.mockk:mockk:${mockk_version}")
    testImplementation(kotlin("test"))
}