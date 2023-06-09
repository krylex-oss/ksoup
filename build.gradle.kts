import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    alias(libs.plugins.kotlin)
    `maven-publish`
}

group = "xyz.krylex"
version = "1.1.1"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    api(libs.jsoup)
    implementation(libs.bundles.ktor)
    implementation(libs.logback)

    testImplementation(libs.bundles.ktor.tests)
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