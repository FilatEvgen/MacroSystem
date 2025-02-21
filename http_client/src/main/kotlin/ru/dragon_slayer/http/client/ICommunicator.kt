package ru.dragon_slayer.http.client

import io.ktor.utils.io.core.*

interface ICommunicator: Closeable {
    suspend fun get(
        url: String,
        headers: Map<String, String> = emptyMap(),
        cookies: List<ICookie> = emptyList()
    ): IHttpResponse

    suspend fun post(
        url: String,
        headers: Map<String, String> = emptyMap(),
        cookies: List<ICookie> = emptyList(),
        body: IBodyBuilder
    ): IHttpResponse

    suspend fun head(
        url: String,
        headers: Map<String, String> = emptyMap(),
        cookies: List<ICookie> = emptyList()
    ): IHttpResponse
}