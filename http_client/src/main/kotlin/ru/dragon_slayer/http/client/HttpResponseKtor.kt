package ru.dragon_slayer.http.client

import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.charsets.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import ru.dragon_slayer.http.client.plugins.RedirectLocations

class HttpResponseKtor(
    private val ktorClientHttpResponse: HttpResponse,
    private val ktorClientCookies: List<io.ktor.http.Cookie>
) : IHttpResponse, CoroutineScope by ktorClientHttpResponse {

    override val status: HttpStatusCode
        get() {
            val original = ktorClientHttpResponse.status
            return HttpStatusCode(original.value, original.description)
        }

    override val contentType: ContentType
        get() {
            val ct = ktorClientHttpResponse.contentType() ?: return ContentTypes.UNKNOWN
            return ContentType(
                contentType = ct.contentType,
                contentSubtype = ct.contentSubtype,
                parameters = ct.parameters.map { it.name to it.value })
        }

    override val cookies: List<ICookie>
        get() = ktorClientCookies.map {
            Cookie(
                it.name,
                it.value,
                it.expires?.timestamp
            )
        }

    override val headers: Map<String, List<String>>
        get() = ktorClientHttpResponse.headers.toMap()

    override suspend fun readBytes(): ByteArray {
        return ktorClientHttpResponse.readRawBytes()
    }

    override suspend fun bodyAsText(fallbackCharset: Charset): String {
        return ktorClientHttpResponse.bodyAsText(fallbackCharset)
    }

    override fun getRedirectLocations(): List<String> {
        val redirectLocations = ktorClientHttpResponse.call.attributes.getOrNull(RedirectLocations.key)
        return redirectLocations?.getLocation() ?: emptyList()
    }

    override fun close() {
        ktorClientHttpResponse.cancel()
    }
}
