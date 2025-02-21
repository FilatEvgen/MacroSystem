package ru.dragon_slayer.http.client

import java.net.URLEncoder

interface IUrlParamBuilder {
    fun build(): String
    fun param(name: String, value: String): IUrlParamBuilder
}

fun UrlParamBuilder(config: IUrlParamBuilder.() -> Unit): IUrlParamBuilder {
    val builder = UrlParamBuilder()
    config.invoke(builder)
    return builder
}

class UrlParamBuilder : IUrlParamBuilder {
    private val params = LinkedHashMap<String, String>()

    override fun param(name: String, value: String): UrlParamBuilder {
        params[name] = value
        return this
    }

    override fun build(): String {
        return params.entries.joinToString(separator = "&") {
            "${
                URLEncoder.encode(
                    it.key,
                    Charsets.UTF_8
                )
            }=${URLEncoder.encode(it.value, Charsets.UTF_8)}"
        }
    }
}

fun IUrlParamBuilder.buildUrlWithParams(url: String): String {
    val separator = if (url.contains('?')) '&' else '?'
    return url + separator + build()
}
