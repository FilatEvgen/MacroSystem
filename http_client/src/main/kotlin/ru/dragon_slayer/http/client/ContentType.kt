package ru.dragon_slayer.http.client

import io.ktor.utils.io.charsets.*

data class ContentType(
    val contentType: String,
    val contentSubtype: String,
    val parameters: List<Pair<String, String>>
) {
    override fun toString(): String {
        val sb = StringBuilder("$contentType/$contentSubtype")
        parameters.forEach { (k, v) ->
            sb.append(";").append(k).append("=").append(v)
        }
        return sb.toString()
    }
}

fun ContentType.isTypeIsEquals(other: ContentType) =
    contentType == other.contentType && contentSubtype == other.contentSubtype

fun ContentType.charsetOrNull(): Charset? {
    return try {
        parameters.find { it.first == "charset" }?.let {
            Charset.forName(it.second)
        }
    } catch (e: Exception) {
        null
    }
}

object ContentTypes {
    val UNKNOWN = ContentType(contentType = "", contentSubtype = "", parameters = emptyList())

    object APPLICATION {
        val JAVASCRIPT = ContentType("application", "javascript", parameters = emptyList())
        val X_BITTORRENT = ContentType("application", "x-bittorrent", parameters = emptyList())
        val X_WWW_FORM_URLENCODED = ContentType("application", "x-www-form-urlencoded", parameters = emptyList())
    }

    object TEXT {
        val HTML = ContentType("text", "html", parameters = emptyList())
        val CSS = ContentType("text", "css", parameters = emptyList())
    }
}
