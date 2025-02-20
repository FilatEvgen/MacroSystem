package ru.dragon_slayer.http.client

import io.ktor.http.*
import io.ktor.utils.io.charsets.*
import io.ktor.utils.io.core.*
import kotlin.text.Charsets

interface IHttpResponse : Closeable {
    val status: HttpStatusCode
    val contentType: ContentType
    val cookies: List<ICookie>
    val headers: Map<String, List<String>>
    fun getRedirectLocations(): List<String>
    suspend fun readBytes(): ByteArray
    suspend fun bodyAsText(fallbackCharset: Charset = Charsets.UTF_8): String
}

fun IHttpResponse.getHeaderValueOrNull(name: String) = headers[name]?.firstOrNull()

private val SUCCESS_CODE = setOf(200, 301, 302)
fun IHttpResponse.isSuccess() = SUCCESS_CODE.contains(status.value)
