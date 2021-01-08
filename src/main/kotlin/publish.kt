import kotlin.math.roundToLong

private const val PUBLISH_PAUSE = 1000L
private const val PUBLISH_GROUP_SIZE = 20
private const val PUBLISH_GROUP_DELAY = 60L * 1000L

fun List<Artifact>.publish(
    context: Context
): List<Artifact> {
    val chunks = groupBy { it.key }.entries.chunked(PUBLISH_GROUP_SIZE)
    return chunks.mapIndexed { chunkIndex, chunk ->
        val chunkStart = System.currentTimeMillis()
        chunk.mapIndexedNotNull { index, (key, artifacts) ->
                val start = System.currentTimeMillis()
                publish(context, key, artifacts, chunkIndex != 0 || index != 0).also {
                    if (index != chunk.lastIndex) {
                        (System.currentTimeMillis() - start).delayIfNeeded(PUBLISH_PAUSE)
                    }
                }
            }
            .flatten()
            .also {
                if (chunkIndex != lastIndex) {
                    (System.currentTimeMillis() - chunkStart).delayIfNeeded(PUBLISH_GROUP_DELAY)
                }
            }
    }.flatten()
}

private fun publish(
    context: Context,
    key: Artifact.Key,
    artifacts: List<Artifact>,
    mute: Boolean
): List<Artifact>? {
    print("publish: ${key.title} ${key.version} -> ")
    return runCatching { notify(context, render(key, artifacts), mute) }
        .onFailure { error(it.localizedMessage) }
        .onSuccess { println(it) }
        .map { artifacts }
        .getOrNull()
}

private fun Long.delayIfNeeded(time: Long) {
    ((time - this) * 1.15).roundToLong().takeIf { it > 0 }?.let { delay ->
        delay.takeIf { it > 1500 }?.let { println("delay: ${it}ms") }
        Thread.sleep(delay)
    }
}
