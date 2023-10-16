plugins {
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.serialization") version "1.8.22"
}

group = "com.usvision"
version = "0.0.1"

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}
