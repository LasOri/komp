plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.22"
}

group = "lasori"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation(libs.kotlinx.serialization)
    implementation(libs.mockk)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}