package ru.dragon_slayer.http.client

import io.ktor.client.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.core.*

class HttpClientKtor(
    private val ktorHttpClient: HttpClient,
    override val connectionTimeoutMs: Int
) : IHttpClient, Closeable by ktorHttpClient {
    override suspend fun get(url: String, headers: Map<String, String>, cookies: List<ICookie>): IHttpResponse {
        val httpResponse = ktorHttpClient.get(url) {
            cookies.forEach { c ->
                cookie(name = c.name, value = c.value)
            }
            headers.forEach { h ->
                header(key = h.key, value = h.value)
            }
        }
        return HttpResponseKtor(httpResponse, ktorHttpClient.cookies(url))
    }

    override suspend fun post(
        url: String,
        headers: Map<String, String>,
        cookies: List<ICookie>,
        contentType: ContentType,
        body: ByteArray
    ): IHttpResponse {
        val httpResponse = ktorHttpClient.post(url) {
            cookies.forEach { c ->
                cookie(name = c.name, value = c.value)
            }
            headers.forEach { h ->
                header(h.key, h.value)
            }
            contentType(contentType.toKtorContentType())
            setBody(body)
        }
        return HttpResponseKtor(httpResponse, ktorHttpClient.cookies(url))
    }

    override suspend fun head(url: String, headers: Map<String, String>, cookies: List<ICookie>): IHttpResponse {
        val httpResponse = ktorHttpClient.head(url) {
            cookies.forEach { c ->
                cookie(name = c.name, value = c.value)
            }
            headers.forEach { h ->
                header(h.key, h.value)
            }
        }
        return HttpResponseKtor(httpResponse, ktorHttpClient.cookies(url))
    }
}
