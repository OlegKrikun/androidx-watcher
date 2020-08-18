import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File

fun File.write(json: Json, artifacts: List<Artifact>) = writeText(
    json.encodeToString(ListSerializer(Artifact.serializer()), artifacts)
)

fun File.read(json: Json) = when {
    exists() -> json.decodeFromString(ListSerializer(Artifact.serializer()), readText())
    else -> null
}
