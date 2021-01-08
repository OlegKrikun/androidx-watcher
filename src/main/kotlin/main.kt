import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit
import javax.xml.parsers.DocumentBuilderFactory

private typealias Reader = () -> List<Artifact>?
private typealias Writer = (List<Artifact>) -> Unit
private typealias Publisher = (List<Artifact>) -> List<Artifact>

fun main() {
    val config = Config(File("androidx-watcher.properties"))
    println("started with: $config")

    while (true) {
        val context = Context(
            httpClient = defaultHttpClient(),
            json = Json { allowStructuredMapKeys = true },
            token = config.token,
            chatId = config.channel
        )
        val dataFile = File("androidx.json")
        val nextTime = try {
            update(
                config.defaultTime,
                config.shortTime,
                localReader = {
                    dataFile.read(context.json)
                },
                localWriter = {
                    dataFile.write(context.json, it)
                },
                remoteReader = {
                    remote(context.httpClient, DocumentBuilderFactory.newInstance())
                },
                publisher = {
                    it.publish(context)
                }
            )
        } catch (e: Exception) {
            error("error: ${e.localizedMessage}")
            e.printStackTrace()
            config.shortTime
        } finally {
            context.destroy()
        }
        Thread.sleep(nextTime)
    }
}

private inline fun update(
    defaultTime: Long,
    shortTime: Long,
    localReader: Reader,
    localWriter: Writer,
    remoteReader: Reader,
    publisher: Publisher
): Long {
    val remote = remoteReader()?.sortedBy { it.id }
    val local = localReader()
    return when {
        remote.isNullOrEmpty() -> {
            error("remote is empty")
            shortTime
        }
        local.isNullOrEmpty() -> {
            println("data file not found")
            localWriter(remote)
            println("data file initialized with ${remote.size} artifacts")
            shortTime
        }
        remote.size < local.size -> {
            error("remote is smaller when local")
            shortTime
        }
        remote.size == local.size -> {
            println("all artifacts are up to date")
            defaultTime
        }
        else -> (remote - local).let { new ->
            println("found ${new.size} new artifacts")

            val notified = publisher(new)
            (local + notified)
                .sortedBy { it.id }
                .let {
                    localWriter(it)
                    println("data file updated with ${it.size} artifacts")
                }

            if (new.size == notified.size) {
                defaultTime
            } else {
                shortTime
            }
        }
    }
}

private fun defaultHttpClient() = OkHttpClient.Builder()
    .addNetworkInterceptor {
        val userAgentRequest = it.request().newBuilder()
            .header("User-Agent", "AndroidX Releases Telegram Channel (https://t.me/androidxreleases)")
            .build()
        it.proceed(userAgentRequest)
    }
    .readTimeout(1, TimeUnit.MINUTES)
    .writeTimeout(1, TimeUnit.MINUTES)
    .connectTimeout(1, TimeUnit.MINUTES)
    .build()

