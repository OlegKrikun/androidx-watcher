import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.w3c.dom.Document
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory

private val masterUrl = HttpUrl.Builder()
    .scheme("https")
    .host("dl.google.com")
    .addPathSegments("dl/android/maven2/master-index.xml")
    .build()

private val groupUrl = HttpUrl.Builder()
    .scheme("https")
    .host("dl.google.com")
    .addPathSegments("dl/android/maven2")
    .build()

fun remote(
    httpClient: OkHttpClient,
    documentFactory: DocumentBuilderFactory
) = httpClient.request(masterUrl, documentFactory)
    .elementSequence()
    .filter { it.tagName.startsWith("androidx") }
    .flatMap {
        val url = groupUrl.newBuilder()
            .addPathSegments("${it.nodeName.asPath()}/group-index.xml")
            .build()
        val group = httpClient.request(url, documentFactory)
        group.elementSequence()
            .flatMap { element ->
                element.versionSequence().map { version ->
                    Artifact(group.documentElement.nodeName, element.nodeName, version)
                }
            }
    }
    .toList()

private fun OkHttpClient.request(url: HttpUrl, factory: DocumentBuilderFactory): Document {
    val request: Request = Request.Builder()
        .url(url)
        .build()
    return newCall(request).execute().use { response ->
        check(response.isSuccessful) { "request is fail: $response" }
        val stream = checkNotNull(response.body) { "body is null: $response" }.byteStream()
        factory.newDocumentBuilder().parse(stream)
    }
}

private fun Document.elementSequence() = documentElement.childNodes.let { nodes ->
    (0 until nodes.length)
        .asSequence()
        .map { nodes.item(it) }
        .filterIsInstance<Element>()
}

private fun Element.versionSequence() = getAttribute("versions").split(',').asSequence()

private fun String.asPath() = replace('.', '/')
