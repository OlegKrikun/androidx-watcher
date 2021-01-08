@file:Suppress("unused", "FunctionName")

import kotlinx.serialization.Serializable

fun BoldEntity(
    offset: Int,
    length: Int
) = Message.Entity("bold", offset, length, null)

fun LinkEntity(
    offset: Int,
    length: Int,
    url: String
) = Message.Entity("text_link", offset, length, url)

data class Message(
    val text: String,
    val entities: List<Entity>
) {
    @Serializable
    data class Entity(
        val type: String,
        val offset: Int,
        val length: Int,
        val url: String?
    )
}
