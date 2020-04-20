import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.list
import java.io.File

fun File.write(json: Json, artifacts: List<Artifact>) = writeText(json.stringify(Artifact.serializer().list, artifacts))

fun File.read(json: Json) = when {
    exists() -> json.parse(Artifact.serializer().list, readText())
    else -> null
}
