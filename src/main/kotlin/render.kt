private const val MAX_MESSAGE_LENGTH = 4096

private const val SEPARATOR = "\n"
private const val PREFIX = "\n"
private const val SUFFIX = "\n"
private const val TRUNCATED_SUFFIX = "...$SUFFIX"

fun render(key: Artifact.Key, list: List<Artifact>): String {
    val title = "${key.version.marker()} *${key.title} ${key.version}* [»](${key.link})$SEPARATOR"
    val body = list.joinToString(SEPARATOR, PREFIX, SUFFIX) { it.id }
    val resultLength = title.length + body.length
    return when {
        resultLength <= MAX_MESSAGE_LENGTH -> title + body
        else -> {
            title + body.dropLast(resultLength + TRUNCATED_SUFFIX.length - MAX_MESSAGE_LENGTH) + TRUNCATED_SUFFIX
        }
    }
}

private const val ICON_RELEASE = "\uD83C\uDF89"
private const val ICON_CANDIDATE = "\uD83D\uDCA1"
private const val ICON_UNSTABLE = "⚙"

private val releaseRegex = "^\\d+\\.\\d+\\.\\d+\$".toRegex()
private val candidateRegex = "^.+rc\\d{2}\$".toRegex()

private fun String.marker() = when {
    matches(releaseRegex) -> ICON_RELEASE
    matches(candidateRegex) -> ICON_CANDIDATE
    else -> ICON_UNSTABLE
}
