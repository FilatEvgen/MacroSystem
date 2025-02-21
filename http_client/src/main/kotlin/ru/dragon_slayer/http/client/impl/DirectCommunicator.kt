package ru.dragon_slayer.http.client.impl

import ru.dragon_slayer.http.client.*

class DirectCommunicator(
    private val httpClient: IHttpClient
): ICommunicator {
    override suspend fun get(
        url: String,
        headers: Map<String, String>,
        cookies: List<ICookie>
    ): IHttpResponse = httpClient.get(
        url = url,
        headers = headers,
        cookies = cookies
    )

    override suspend fun post(
        url: String,
        headers: Map<String, String>,
        cookies: List<ICookie>,
        body: IBodyBuilder
    ): IHttpResponse = httpClient.post(
        url = url,
        headers = headers,
        cookies = cookies,
        contentType = body.contentType(),
        body = body.bodyContent()
    )

    override suspend fun head(
        url: String,
        headers: Map<String, String>,
        cookies: List<ICookie>
    ): IHttpResponse = httpClient.head(
        url = url,
        headers = headers,
        cookies = cookies
    )

    override fun close() {
        httpClient.close()
    }
}