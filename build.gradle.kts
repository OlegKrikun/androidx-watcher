import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.5.30"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.30"
    id("com.github.ben-manes.versions") version "0.39.0"
}

repositories { jcenter() }

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
}

version = "0.4.0"

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val libs: Set<File> = configurations.getByName("runtimeClasspath").files
tasks.withType(Jar::class) {
    archiveFileName.set("androidx-watcher.jar")
    manifest.attributes(
        "Main-Class" to "MainKt",
        "Class-Path" to libs.joinToString(" ") { "lib/${it.name}" }
    )
}

tasks.wrapper { distributionType = Wrapper.DistributionType.ALL }
