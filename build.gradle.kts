plugins {
    java
    application
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "cinema.app.MainApplication"
}

group = "edu.ort.lyon"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.postgresql:postgresql:42.7.4")

    implementation("org.openjfx:javafx-controls:23.0.1:win")
    implementation("org.openjfx:javafx-graphics:23.0.1:win")
    implementation("org.openjfx:javafx-base:23.0.1:win")
    implementation("org.openjfx:javafx-fxml:23.0.1:win")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "cinema.app.Launcher"
    }
    from(configurations.runtimeClasspath.get().map {
        if (it.isDirectory) it else zipTree(it)
    }) {
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
        exclude("module-info.class")
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.test {
    useJUnitPlatform()
}

tasks.named<JavaExec>("run") {
    jvmArgs = listOf(
        "--module-path", configurations.runtimeClasspath.get().asPath,
        "--add-modules", "javafx.controls,javafx.fxml",
        "--enable-native-access=javafx.graphics,javafx.controls,javafx.fxml"
    )
}

tasks.register<Exec>("jpackage") {
    dependsOn("jar")
    commandLine(
        "jpackage",
        "--input", "build/libs",
        "--main-jar", "P2026_2SIO_javafx_cinema-1.0-SNAPSHOT.jar",
        "--main-class", "cinema.app.Launcher",
        "--name", "CinemaApp",
        "--app-version", "1.0",
        "--type", "exe",
        "--dest", "build/installer",
        "--win-dir-chooser",
        "--win-shortcut",
        "--java-options", "--enable-native-access=javafx.graphics,javafx.controls,javafx.fxml",
        "--java-options", "--add-opens=javafx.graphics/com.sun.javafx.application=ALL-UNNAMED"
    )
}