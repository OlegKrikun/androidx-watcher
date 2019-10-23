import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

fun notify(httpClient: OkHttpClient, token: String, chatId: String, text: String): String? {
    val url = HttpUrl.Builder()
        .scheme("https")
        .host("api.telegram.org")
        .addPathSegments("bot$token/sendMessage")
        .addQueryParameter("chat_id", chatId)
        .addEncodedQueryParameter("text", text)
        .addQueryParameter("disable_web_page_preview", "true")
        .addQueryParameter("parse_mode", "markdown")
        .build()
    val request: Request = Request.Builder()
        .url(url)
        .build()
    return httpClient.newCall(request).execute().use { it.body?.string() }
}