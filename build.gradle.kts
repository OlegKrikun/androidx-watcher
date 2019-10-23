import com.netflix.gradle.plugins.deb.Deb
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.50"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.3.50"
    id("nebula.deb") version "7.5.0"
    id("com.github.johnrengelman.shadow") version "5.1.0"
}

repositories { jcenter() }

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.50")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.13.0")
    implementation("com.squareup.okhttp3:okhttp:4.2.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType(Jar::class) {
    manifest.attributes("Main-Class" to "MainKt")
}

tasks.create("deb", Deb::class) {
    group = "build"
    version = "0.1.0"

    preInstall(file("scripts/preInstall.sh"))
    postInstall(file("scripts/postInstall.sh"))
    preUninstall(file("scripts/preUninstall.sh"))
    postUninstall(file("scripts/postUninstall.sh"))

    from(tasks["shadowJar"].outputs.files) { into("/opt/androidx-watcher") }
    from(File(project.rootDir, "androidx-watcher.service")) { into("/opt/androidx-watcher") }
    from(File(project.rootDir, "androidx-watcher.properties.example")) { into("/opt/androidx-watcher") }

    link("/etc/systemd/system/androidx-watcher.service", "/opt/androidx-watcher/androidx-watcher.service")
}
