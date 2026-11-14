plugins {
    application
}

group = "bw.farmers"
version = "2.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.xerial:sqlite-jdbc:3.51.0.0")
    implementation("com.formdev:flatlaf:3.5.4")
    implementation("com.formdev:flatlaf-extras:3.5.4")
    implementation("com.formdev:flatlaf-fonts-inter:4.0")
    runtimeOnly("org.slf4j:slf4j-nop:2.0.17")

    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    // Builds with any JDK 17 or newer; no toolchain download needed.
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 17
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:-serial", "-Werror"))
}

application {
    mainClass = "bw.farmers.App"
    applicationName = "farmersbw"
}

tasks.test {
    useJUnitPlatform()
    systemProperty("java.awt.headless", "true")
}

// One runnable jar with every dependency inside: build/libs/farmersbw-<version>-all.jar
val uberJar by tasks.registering(Jar::class) {
    group = "distribution"
    description = "Builds a single runnable jar with all dependencies."
    archiveClassifier = "all"
    manifest { attributes["Main-Class"] = application.mainClass }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(sourceSets.main.get().output)
    from(configurations.runtimeClasspath.map { cp -> cp.map { if (it.isDirectory) it else zipTree(it) } })
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "META-INF/versions/*/module-info.class", "module-info.class")
}

// Renders every screen with demo data into docs/screenshots (needs a desktop session).
val screenshots by tasks.registering(JavaExec::class) {
    group = "documentation"
    description = "Regenerates the README screenshots."
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass = "bw.farmers.ui.Screenshots"
    args(layout.projectDirectory.dir("docs/screenshots").asFile.absolutePath)
}

// Writes packaging/icons/farmersbw.{ico,icns,png} from the logo the app paints.
val icons by tasks.registering(JavaExec::class) {
    group = "distribution"
    description = "Regenerates the installer icons."
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass = "bw.farmers.ui.IconExporter"
    args(layout.projectDirectory.dir("packaging/icons").asFile.absolutePath)
}

// ---- native installers with a bundled Java runtime (jpackage) ----------------------------
//
//   ./gradlew jpackage                         a ready-to-run app folder (any OS)
//   ./gradlew jpackage -PinstallerType=msi     Windows installer (needs WiX 3 on the PATH)
//   ./gradlew jpackage -PinstallerType=dmg     macOS disk image
//   ./gradlew jpackage -PinstallerType=deb     Debian/Ubuntu package
//
// Output goes to build/jpackage/out. The GitHub release workflow runs these on each OS.

val jpackageInput = layout.buildDirectory.dir("jpackage/input")
val jpackageOut = layout.buildDirectory.dir("jpackage/out")

val stageJar by tasks.registering(Sync::class) {
    from(uberJar)
    into(jpackageInput)
}

val jpackage by tasks.registering(Exec::class) {
    group = "distribution"
    description = "Builds a native app or installer with its own Java runtime."
    dependsOn(stageJar)

    val type = (findProperty("installerType") as String?) ?: "app-image"
    val os = System.getProperty("os.name").lowercase()
    val icon = when {
        os.contains("win") -> "farmersbw.ico"
        os.contains("mac") -> "farmersbw.icns"
        else -> "farmersbw.png"
    }
    val jarName = uberJar.flatMap { it.archiveFileName }
    val out = jpackageOut.get().asFile

    doFirst { delete(out) }
    executable = File(System.getProperty("java.home"), "bin/jpackage").absolutePath
    argumentProviders.add(CommandLineArgumentProvider {
        val a = mutableListOf(
            "--type", type,
            "--name", "FarmersBW",
            "--app-version", project.version.toString(),
            "--vendor", "Mehdi Acho",
            "--copyright", "Copyright 2022-2026 Mehdi Acho",
            "--description", "Farmer registration for Botswana's Ministry of Agriculture",
            "--input", jpackageInput.get().asFile.absolutePath,
            "--main-jar", jarName.get(),
            "--main-class", application.mainClass.get(),
            "--dest", out.absolutePath,
            "--icon", layout.projectDirectory.file("packaging/icons/$icon").asFile.absolutePath,
            "--add-modules", "java.base,java.desktop,java.prefs,java.sql,java.logging,jdk.unsupported,jdk.accessibility",
            "--jlink-options", "--strip-debug --no-man-pages --no-header-files --compress=zip-6",
        )
        if (type != "app-image") {
            when {
                os.contains("win") -> a += listOf(
                    "--win-menu", "--win-menu-group", "FarmersBW", "--win-shortcut", "--win-dir-chooser",
                    "--win-per-user-install", "--win-upgrade-uuid", "5b0a7f3e-2c1d-4e8a-9f61-3d2b8c4a7e10",
                )
                os.contains("mac") -> a += listOf("--mac-package-name", "FarmersBW")
                else -> a += listOf(
                    "--linux-shortcut", "--linux-menu-group", "Office", "--linux-app-category", "misc",
                    "--linux-package-name", "farmersbw",
                )
            }
        }
        a
    })
}
