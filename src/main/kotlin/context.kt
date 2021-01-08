import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient

class Context(
    val httpClient: OkHttpClient,
    val json: Json,
    val token: String,
    val chatId: String
) {
    fun destroy() = httpClient.connectionPool.evictAll()
}
