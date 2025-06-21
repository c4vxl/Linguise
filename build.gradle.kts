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

tasks.jar {
    manifest.attributes["Main-Class"] = "de.c4vxl.Main"

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().filter { it.name != "module-info.class" }
        .map { if (it.isDirectory) it else zipTree(it) })
}