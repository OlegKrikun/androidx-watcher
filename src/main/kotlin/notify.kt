import kotlinx.serialization.builtins.ListSerializer
import okhttp3.HttpUrl
import okhttp3.Request

fun notify(
    context: Context,
    message: Message,
    mute: Boolean
): String {
    val entityListSerializer = ListSerializer(Message.Entity.serializer())
    val entities = context.json.encodeToString(entityListSerializer, message.entities)
    val url = HttpUrl.Builder()
        .scheme("https")
        .host("api.telegram.org")
        .addPathSegments("bot${context.token}/sendMessage")
        .addQueryParameter("chat_id", context.chatId)
        .addQueryParameter("text", message.text)
        .addQueryParameter("entities", entities)
        .addQueryParameter("disable_notification", mute.toString())
        .addQueryParameter("disable_web_page_preview", "true")
        .build()
    val request: Request = Request.Builder()
        .url(url)
        .build()
    return context.httpClient.newCall(request).execute().use {
        when {
            it.isSuccessful -> it.message
            else -> throw RuntimeException("http [${it.code}] ${it.message}")
        }
    }
}
