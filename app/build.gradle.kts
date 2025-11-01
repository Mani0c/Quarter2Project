plugins {
    application
    id("org.beryx.runtime") version "1.12.7" // Plugin for native packaging
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // implementation("org.openjfx:javafx-controls:17") // Uncomment if using JavaFX
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    mainClass.set("Runner")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

