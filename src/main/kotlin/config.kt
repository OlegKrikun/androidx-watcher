import java.io.File
import java.util.Properties
import kotlin.system.exitProcess

class Config(file: File) {
    private val properties = Properties().apply { file.inputStream().use { load(it) } }

    val channel = properties.getProperty("channel")?.takeIf { it.isNotEmpty() } ?: exitProcess(1)
    val token = properties.getProperty("token")?.takeIf { it.isNotEmpty() } ?: exitProcess(1)
    val time = properties.getProperty("time", TIME.toString()).toLongOrNull() ?: exitProcess(1)

    override fun toString() = "channel=$channel; token=$token; time=$time"

    companion object {
        private const val TIME = 60L * 60L * 1000L
    }
}
