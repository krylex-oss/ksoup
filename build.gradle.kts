import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.dokka)
    `maven-publish`
}

group = "xyz.krylex"
version = "1.1.2"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    api(libs.jsoup)
    implementation(libs.ktor.client.core)

    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.jupiter)
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events(TestLogEvent.FAILED, TestLogEvent.SKIPPED, TestLogEvent.PASSED)
        }
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    repositories {
        maven("https://repository.krylex.xyz/releases") {
            name = "krylexReleases"
            credentials {
                username = System.getenv("KRYLEX_REPOSITORY_USERNAME")
                password = System.getenv("KRYLEX_REPOSITORY_PASSWORD")
            }
        }
    }
    publications {
        register<MavenPublication>("release") {
            from(components["java"])
        }
    }
}