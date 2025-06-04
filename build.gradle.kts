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
    implementation("de.c4vxl:jNN:1.0.0")
}

tasks.test {
    useJUnitPlatform()
}