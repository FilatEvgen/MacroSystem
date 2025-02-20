package ru.dragon_slayer.http.client

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.cookies.*

interface IHttpClientFactory {
    fun create(
        userAgent: String? = null,
        connectionTimeoutMs: Int = 10_000
    ): IHttpClient
}

object HttpClientFactory: IHttpClientFactory {
    private const val DEFAULT_USER_AGENT =
        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:35.0) Gecko/20100101 Firefox/35.0"

    override fun create(userAgent: String?, connectionTimeoutMs: Int): IHttpClient {
        return apacheKtor(userAgent, connectionTimeoutMs)
    }

    private fun apacheKtor(userAgent: String?, connectionTimeoutMs: Int = 10_000): IHttpClient {
        val targetUserAgent = userAgent?: DEFAULT_USER_AGENT
        val ktorHttpClient = HttpClient(Apache) {
            engine {
                socketTimeout = connectionTimeoutMs
                connectTimeout = connectionTimeoutMs
                connectionRequestTimeout = connectionTimeoutMs
            }
            install(UserAgent) { agent = targetUserAgent }
            install(ContentEncoding) {
                deflate(1.0F)
                gzip(0.9F)
            }
            install(HttpCookies)
            install(HttpTimeout) {
                requestTimeoutMillis = connectionTimeoutMs.toLong()
            }
        }
        return HttpClientKtor(ktorHttpClient, connectionTimeoutMs)
    }
}