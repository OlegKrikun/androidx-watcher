private const val MAX_MESSAGE_LENGTH = 4096

private const val ICON_RELEASE = "🎉"
private const val ICON_CANDIDATE = "💡"
private const val ICON_UNSTABLE = "⚙"

fun render(key: Artifact.Key, list: List<Artifact>): Message {
    val entities = mutableListOf<Message.Entity>()
    val text = buildString {
        append(key.marker)
        append(' ')
        append(key.name) { offset, length ->
            entities.add(BoldEntity(offset, length))
        }
        append(' ')
        append("»") { offset, length ->
            entities.add(LinkEntity(offset, length, key.link))
        }
        append('\n')

        for (it in list) {
            append('\n')
            val id = it.id
            if (length + id.length > MAX_MESSAGE_LENGTH) {
                append(id.take(MAX_MESSAGE_LENGTH - length - 1) + '…')
                break
            } else {
                append(id)
            }
        }
    }
    return Message(text, entities)
}

private val releaseRegex = "^\\d+\\.\\d+\\.\\d+\$".toRegex()
private val candidateRegex = "^.+rc\\d{2}\$".toRegex()
private val Artifact.Key.marker
    get() = when {
        version.matches(releaseRegex) -> ICON_RELEASE
        version.matches(candidateRegex) -> ICON_CANDIDATE
        else -> ICON_UNSTABLE
    }
private val Artifact.Key.name get() = "$title $version"

private inline fun StringBuilder.append(
    string: String,
    onAppend: (offset: Int, length: Int) -> Unit
) {
    onAppend(length, string.length)
    append(string)
}
