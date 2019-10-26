import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit
import javax.xml.parsers.DocumentBuilderFactory

private typealias Reader = () -> List<Artifact>?
private typealias Writer = (List<Artifact>) -> Unit
private typealias Publisher = (Artifact.Key, List<Artifact>, Boolean) -> String

private const val PUBLISH_PAUSE = 1000L

fun main() {
    val config = Config(File("androidx-watcher.properties"))
    println("started with: $config")

    while (true) {
        val dataFile = File("androidx.json")
        val json = Json(JsonConfiguration.Stable)
        val httpClient = defaultHttpClient()
        val nextTime = try {
            update(
                config.defaultTime,
                config.shortTime,
                localReader = {
                    dataFile.read(json)
                },
                localWriter = {
                    dataFile.write(json, it)
                },
                remoteReader = {
                    remote(httpClient, DocumentBuilderFactory.newInstance())
                },
                publisher = { key, list, mute ->
                    notify(httpClient, config.token, config.channel, render(key, list), mute)
                })
        } catch (e: Exception) {
            error("error: ${e.localizedMessage}")
            e.printStackTrace()
            config.shortTime
        } finally {
            httpClient.connectionPool.evictAll()
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

            val notified = new.groupBy { it.key }
                .entries
                .mapIndexedNotNull { index, (key, artifacts) ->
                    print("publish: ${key.title} ${key.version} -> ")
                    runCatching { publisher(key, artifacts, index != 0) }
                        .onFailure { error(it.localizedMessage) }
                        .onSuccess { println(it) }
                        .getOrNull()
                        ?.let { artifacts }
                        .also { Thread.sleep(PUBLISH_PAUSE) }
                }
                .flatten()

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

private fun error(string: String) = System.err.println(string)
