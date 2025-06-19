plugins {
    id("java")
}

group = "de.c4vxl"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://mvn.c4vxl.de/jNN/")
}

dependencies {
    implementation("de.c4vxl:jNN:1.0.0")               // Used for AI models
    implementation("com.google.code.gson:gson:2.13.1") // Used for configuration parsing
}