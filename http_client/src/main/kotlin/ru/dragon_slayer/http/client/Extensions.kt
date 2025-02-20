package ru.dragon_slayer.http.client

import io.ktor.http.*

fun List<Pair<String, String>>.toKtorParams(): List<HeaderValueParam> {
    return this.map { (key, value) -> HeaderValueParam(key, value) }
}

fun ContentType.toKtorContentType(): io.ktor.http.ContentType {
    return ContentType(contentType, contentSubtype, parameters = parameters.toKtorParams())
}
