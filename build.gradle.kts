plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
    id("maven-publish")
    signing
}

group = "io.github.LasOri"
version = "0.1.0"

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

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = "io.github.LasOri"
            artifactId = "komp"
            version = "0.1.0"

            pom {
                name.set("komp")
                description.set("Kotlin Model Provider")
                url.set("https://github.com/LasOri/komp")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/LasOri/komp?tab=MIT-1-ov-file#MIT-1-ov-file")
                    }
                }
                developers {
                    developer {
                        id.set("LasOri")
                        name.set("Laszlo Ori")
                        email.set("laszlo.oeri@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/LasOri/komp.git")
                    developerConnection.set("scm:git:ssh://github.com:LasOri/komp.git")
                    url.set("https://github.com/LasOri/komp")
                }
            }
        }
    }

    repositories {
        maven {
            name = "OSSRH"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = project.findProperty("ossrhUsername") as String? ?: ""
                password = project.findProperty("ossrhPassword") as String? ?: ""
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}
