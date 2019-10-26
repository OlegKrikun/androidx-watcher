import java.io.File
import java.util.Properties
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

class Config(file: File) {
    private val properties = Properties().apply { file.inputStream().use { load(it) } }

    val channel = properties.getProperty("channel")?.takeIf { it.isNotEmpty() } ?: exitProcess(1)
    val token = properties.getProperty("token")?.takeIf { it.isNotEmpty() } ?: exitProcess(1)
    val defaultTime = properties.getProperty("time", DEFAULT_TIME.toString()).toLongOrNull() ?: exitProcess(1)
    val shortTime = properties.getProperty("short_time", SHORT_TIME.toString()).toLongOrNull() ?: exitProcess(1)

    override fun toString() = "channel=$channel; token=$token; time=$defaultTime; short_time=$shortTime"

    companion object {
        private val DEFAULT_TIME = TimeUnit.HOURS.toMillis(1)
        private val SHORT_TIME = TimeUnit.MINUTES.toMillis(1)
    }
}
