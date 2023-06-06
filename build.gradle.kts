import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    alias(libs.plugins.kotlin)
}

group = "xyz.krylex"
version = "1.0.0"

repositories {
    mavenCentral()
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