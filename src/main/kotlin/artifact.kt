import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Artifact(
    val group: String,
    val name: String,
    val version: String
) {
    @Transient
    val id = "$group:$name:$version"
    @Transient
    val key = keyOf(this)

    override fun toString() = id

    data class Key(val title: String, val link: String, val version: String)
}

private fun keyOf(a: Artifact): Artifact.Key = when {
    a.group == "androidx.annotation" && a.name.startsWith("annotation-experimental") -> a.createKey(
        "Annotation Experimental",
        "https://developer.android.com/jetpack/androidx/releases/annotation#experimental-${a.version}"
    )
    a.group == "androidx.arch.core" -> a.createKey(
        "Arch",
        "https://developer.android.com/jetpack/androidx/releases/arch#${a.version}"
    )
    a.group == "androidx.camera" -> a.createKey(
        "CameraX",
        "https://developer.android.com/jetpack/androidx/releases/camera#camera2-core-${a.version}"
    )
    a.group == "androidx.car" && a.name == "car-cluster" -> a.createKey(
        "Car",
        "https://developer.android.com/jetpack/androidx/releases/car#car-cluster-${a.version}"
    )
    a.group == "androidx.compose" -> a.createKey(
        "Jetpack Compose",
        "https://developer.android.com/jetpack/compose"
    )
    a.group == "androidx.concurrent" && a.name.startsWith("concurrent-listenablefuture") -> a.createKey(
        "Concurrent",
        "https://developer.android.com/jetpack/androidx/releases/concurrent#concurrent-listenableFuture-${a.version}"
    )
    a.group.startsWith("androidx.test.espresso") -> a.createKey(
        "Test: Espresso",
        "https://developer.android.com/jetpack/androidx/releases/test"
    )
    a.group == "androidx.test.ext" && a.name.startsWith("junit") -> a.createKey(
        "Test: Junit",
        "https://developer.android.com/jetpack/androidx/releases/test"
    )
    a.group == "androidx.test.ext" && a.name == "truth" -> a.createKey(
        "Test: Truth",
        "https://developer.android.com/jetpack/androidx/releases/test"
    )
    a.group == "androidx.test.janktesthelper" -> a.createKey(
        "Test: Janktesthelper",
        "https://developer.android.com/jetpack/androidx/releases/test"
    )
    a.group == "androidx.test.services" -> a.createKey(
        "Test: Service",
        "https://developer.android.com/jetpack/androidx/releases/test"
    )
    a.group == "androidx.test.uiautomator" -> a.createKey(
        "Test: Uiautomator",
        "https://developer.android.com/jetpack/androidx/releases/test"
    )
    a.group == "androidx.test" && a.name.startsWith("core") -> a.createKey(
        "Test: Core",
        "https://developer.android.com/jetpack/androidx/releases/test"
    )
    a.group == "androidx.test" && a.name == "monitor" -> a.createKey(
        "Test: Monitor",
        "https://developer.android.com/jetpack/androidx/releases/test"
    )
    a.group == "androidx.test" && a.name == "orchestrator" -> a.createKey(
        "Test: Orchestrator",
        "https://developer.android.com/jetpack/androidx/releases/test"
    )
    a.group == "androidx.test" && a.name == "rules" -> a.createKey(
        "Test: Rules",
        "https://developer.android.com/jetpack/androidx/releases/test"
    )
    a.group == "androidx.test" && a.name == "runner" -> a.createKey(
        "Test: Runner",
        "https://developer.android.com/jetpack/androidx/releases/test"
    )
    a.group == "androidx.ui" -> a.createKey(
        "Jetpack Compose UI",
        "https://developer.android.com/jetpack/compose"
    )
    a.group == "androidx.work" -> a.createKey("WorkManager")
    else -> a.createKey()
}

private fun Artifact.createKey(name: String? = null, link: String? = null) = when {
    name == null || link == null -> group.removePrefix("androidx.").let { groupSuffix ->
        Artifact.Key(
            name ?: groupSuffix.capitalize(),
            link ?: "https://developer.android.com/jetpack/androidx/releases/$groupSuffix#$version",
            version
        )
    }
    else -> Artifact.Key(name, link, version)
}
