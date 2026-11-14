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
