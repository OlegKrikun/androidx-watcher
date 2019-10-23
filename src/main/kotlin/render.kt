private const val SEPARATOR = "\n\n"

fun List<Artifact>.render() = groupBy {
    it.key
}.map { (key, items) ->
    val (name, link, version) = key
    "*$name $version* [»]($link)\n" + items.joinToString("\n") { "`${it.id}`" }
}.fold(mutableListOf("")) { acc, text ->
    acc.apply {
        val last = last()
        if (last.length + SEPARATOR.length + text.length < 4096) {
            removeAt(lastIndex)
            add(last + SEPARATOR + text)
        } else {
            add(text)
        }
    }
}

private data class Key(val name: String, val link: String, val version: String)

private val Artifact.key: Key
    get() = when {
        group == "androidx.annotation" && name.startsWith("annotation-experimental") -> createKey(
            "Annotation Experimental",
            "https://developer.android.com/jetpack/androidx/releases/annotation#experimental-$version"
        )
        group == "androidx.arch.core" -> createKey(
            "Arch",
            "https://developer.android.com/jetpack/androidx/releases/arch#$version"
        )
        group == "androidx.camera" -> createKey(
            "CameraX",
            "https://developer.android.com/jetpack/androidx/releases/camera#camera2-core-$version"
        )
        group == "androidx.car" && name == "car-cluster" -> createKey(
            "Car",
            "https://developer.android.com/jetpack/androidx/releases/car#car-cluster-$version"
        )
        group == "androidx.compose" -> createKey(
            "Jetpack Compose",
            "https://developer.android.com/jetpack/compose"
        )
        group == "androidx.concurrent" && name.startsWith("concurrent-listenablefuture") -> createKey(
            "Concurrent",
            "https://developer.android.com/jetpack/androidx/releases/concurrent#concurrent-listenableFuture-$version"
        )
        group.startsWith("androidx.test.espresso") -> createKey(
            "Test: Espresso",
            "https://developer.android.com/jetpack/androidx/releases/test"
        )
        group == "androidx.test.ext" && name.startsWith("junit") -> createKey(
            "Test: Junit",
            "https://developer.android.com/jetpack/androidx/releases/test"
        )
        group == "androidx.test.ext" && name == "truth" -> createKey(
            "Test: Truth",
            "https://developer.android.com/jetpack/androidx/releases/test"
        )
        group == "androidx.test.janktesthelper" -> createKey(
            "Test: Janktesthelper",
            "https://developer.android.com/jetpack/androidx/releases/test"
        )
        group == "androidx.test.services" -> createKey(
            "Test: Service",
            "https://developer.android.com/jetpack/androidx/releases/test"
        )
        group == "androidx.test.uiautomator" -> createKey(
            "Test: Uiautomator",
            "https://developer.android.com/jetpack/androidx/releases/test"
        )
        group == "androidx.test" && name.startsWith("core") -> createKey(
            "Test: Core",
            "https://developer.android.com/jetpack/androidx/releases/test"
        )
        group == "androidx.test" && name == "monitor" -> createKey(
            "Test: Monitor",
            "https://developer.android.com/jetpack/androidx/releases/test"
        )
        group == "androidx.test" && name == "orchestrator" -> createKey(
            "Test: Orchestrator",
            "https://developer.android.com/jetpack/androidx/releases/test"
        )
        group == "androidx.test" && name == "rules" -> createKey(
            "Test: Rules",
            "https://developer.android.com/jetpack/androidx/releases/test"
        )
        group == "androidx.test" && name == "runner" -> createKey(
            "Test: Runner",
            "https://developer.android.com/jetpack/androidx/releases/test"
        )
        group == "androidx.ui" -> createKey(
            "Jetpack Compose UI",
            "https://developer.android.com/jetpack/compose"
        )
        group == "androidx.work" -> createKey("WorkManager")
        else -> createKey()
    }

private fun Artifact.createKey(
    name: String = groupSuffix.capitalize(),
    link: String = "https://developer.android.com/jetpack/androidx/releases/$groupSuffix#$version",
    ver: String = version
) = Key(name, link, ver)
