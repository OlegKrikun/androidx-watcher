import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

private const val LINK_ARCH = "https://developer.android.com/jetpack/androidx/releases/arch#"
private const val LINK_COMPOSE = "https://developer.android.com/jetpack/compose"
private const val LINK_TESTS = "https://developer.android.com/jetpack/androidx/releases/test"

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
        "annotation#experimental"
    )

    a.group == "androidx.arch.core" -> a.createKey("Arch", fullLink = LINK_ARCH + a.version)

    a.group == "androidx.camera" && a.name == "camera-camera2" -> a.createKey("Camera-Camera2", "camera#camera-camera2")
    a.group == "androidx.camera" && a.name == "camera-core" -> a.createKey("Camera-Core", "camera#camera-core")
    a.group == "androidx.camera" && a.name == "camera-extensions" -> a.createKey(
        "Camera-Extensions",
        "camera#camera-extensions"
    )
    a.group == "androidx.camera" && a.name == "camera-lifecycle" -> a.createKey(
        "Camera-Lifecycle",
        "camera#camera-lifecycle"
    )
    a.group == "androidx.camera" && a.name == "camera-view" -> a.createKey("Camera-View", "camera#camera-view")

    a.group == "androidx.car" && a.name == "car-cluster" -> a.createKey("Car", "car#car-cluster")

    a.group == "androidx.compose" -> a.createKey("Jetpack Compose", fullLink = LINK_COMPOSE)

    a.group == "androidx.concurrent" && a.name.startsWith("concurrent-listenablefuture") -> a.createKey(
        "Concurrent",
        "concurrent#concurrent-listenableFuture"
    )

    a.group == "androidx.core" && a.name == "core-animation" -> a.createKey("Core-Animation", "core#core-animation")
    a.group == "androidx.core" && a.name == "core-role" -> a.createKey("Core-Role", "core#core-role")

    a.group == "androidx.customview" -> a.createKey("CustomView")

    a.group == "androidx.drawerlayout" -> a.createKey("DrawerLayout")

    a.group == "androidx.exifinterface" -> a.createKey("ExifInterface")

    a.group == "androidx.recyclerview" -> a.createKey("RecyclerView")

    a.group == "androidx.security" && a.name == "security-crypto" -> a.createKey("Security-Crypto")

    a.group == "androidx.slidingpanelayout" -> a.createKey("SlidingPaneLayout")

    a.group.startsWith("androidx.test.espresso") -> a.createKey("Test: Espresso", fullLink = LINK_TESTS)
    a.group == "androidx.test.ext" && a.name.startsWith("junit") -> a.createKey("Test: Junit", fullLink = LINK_TESTS)
    a.group == "androidx.test.ext" && a.name == "truth" -> a.createKey("Test: Truth", fullLink = LINK_TESTS)
    a.group == "androidx.test.janktesthelper" -> a.createKey("Test: Janktesthelper", fullLink = LINK_TESTS)
    a.group == "androidx.test.services" -> a.createKey("Test: Service", fullLink = LINK_TESTS)
    a.group == "androidx.test.uiautomator" -> a.createKey("Test: Uiautomator", fullLink = LINK_TESTS)
    a.group == "androidx.test" && a.name.startsWith("core") -> a.createKey("Test: Core", fullLink = LINK_TESTS)
    a.group == "androidx.test" && a.name == "monitor" -> a.createKey("Test: Monitor", fullLink = LINK_TESTS)
    a.group == "androidx.test" && a.name == "orchestrator" -> a.createKey("Test: Orchestrator", fullLink = LINK_TESTS)
    a.group == "androidx.test" && a.name == "rules" -> a.createKey("Test: Rules", fullLink = LINK_TESTS)
    a.group == "androidx.test" && a.name == "runner" -> a.createKey("Test: Runner", fullLink = LINK_TESTS)

    a.group == "androidx.ui" -> a.createKey("Jetpack Compose UI", fullLink = LINK_COMPOSE)

    a.group == "androidx.vectordrawable" && a.name == "vectordrawable-seekable" -> a.createKey(
        "Vectordrawable-Seekable",
        "vectordrawable#vectordrawable-seekable"
    )

    a.group == "androidx.work" -> a.createKey("WorkManager")

    else -> a.createKey()
}

private fun Artifact.createKey(name: String? = null, linkSuffix: String? = null, fullLink: String? = null) = when {
    name == null || fullLink == null -> group.removePrefix("androidx.").let { groupSuffix ->
        Artifact.Key(
            name ?: groupSuffix.capitalize(),
            fullLink ?: createUrl(groupSuffix, linkSuffix),
            version
        )
    }
    else -> Artifact.Key(name, fullLink, version)
}

private fun Artifact.createUrl(groupSuffix: String, linkSuffix: String? = null) = when {
    linkSuffix != null -> "https://developer.android.com/jetpack/androidx/releases/$linkSuffix-$version"
    else -> "https://developer.android.com/jetpack/androidx/releases/$groupSuffix#$version"
}
