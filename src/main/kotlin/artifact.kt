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
    val groupSuffix = group.removePrefix("androidx.")

    override fun toString() = id
}
