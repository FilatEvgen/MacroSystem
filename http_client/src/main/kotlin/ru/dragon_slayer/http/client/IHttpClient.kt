package ru.dragon_slayer.http.client

import io.ktor.http.*
import io.ktor.utils.io.core.*

interface IHttpClient : Closeable {

    val connectionTimeoutMs: Int

    suspend fun get(
        url: String,
        headers: Map<String, String> = emptyMap(),
        cookies: List<ICookie> = emptyList()
    ): IHttpResponse

    suspend fun post(
        url: String,
        headers: Map<String, String> = emptyMap(),
        cookies: List<ICookie> = emptyList(),
        contentType: ContentType,
        body: ByteArray
    ): IHttpResponse

    suspend fun head(
        url: String,
        headers: Map<String, String> = emptyMap(),
        cookies: List<ICookie> = emptyList()
    ): IHttpResponse
}

open class HttpClientWrapper(private val original: IHttpClient) : IHttpClient by original
