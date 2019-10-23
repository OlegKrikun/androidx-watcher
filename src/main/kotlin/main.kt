import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.OkHttpClient
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

private typealias Reader = () -> List<Artifact>?
private typealias Writer = (List<Artifact>) -> Unit

fun main() {
    val config = Config(File("androidx-watcher.properties"))
    println("started with: $config")

    while (true) {
        val dataFile = File("androidx.json")
        val json = Json(JsonConfiguration.Stable)
        val httpClient = defaultHttpClient()
        try {
            update(
                localReader = {
                    dataFile.read(json)
                },
                localWriter = {
                    dataFile.write(json, it)
                },
                remoteReader = {
                    remote(httpClient, DocumentBuilderFactory.newInstance())
                },
                remoteWriter = {
                    it.render().forEachIndexed { i, msg ->
                        notify(httpClient, config.token, config.channel, msg, mute = i != 0)
                    }
                })
        } catch (e: Exception) {
            error("error: ${e.localizedMessage}")
            e.printStackTrace()
        }
        Thread.sleep(config.time)
    }
}

private inline fun update(
    localReader: Reader,
    localWriter: Writer,
    remoteReader: Reader,
    remoteWriter: Writer
) {
    val remote = remoteReader()
    val local = localReader()
    when {
        remote.isNullOrEmpty() -> {
            error("remote is empty")
        }
        local.isNullOrEmpty() -> {
            println("data file not found")
            localWriter(remote)
            println("data file initialized with ${remote.size} artifacts")
        }
        remote.size < local.size -> {
            error("remote is smaller when local")
        }
        else -> {
            val new = (remote - local)
            when {
                new.isEmpty() -> println("all artifacts are up to date")
                else -> {
                    println("found ${new.size} new artifacts")
                    remoteWriter(new)
                    localWriter(remote)
                    println("data file updated with ${remote.size} artifacts")
                }
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
    .build()

private fun error(string: String) = System.err.println(string)
