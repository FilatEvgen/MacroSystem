package ru.dragon_slayer.http.client

interface ICookie {
    val name: String
    val value: String
    val expires: Long?
}

data class Cookie(
    override val name: String,
    override val value: String,
    override val expires: Long? = null
) : ICookie


fun ICookie.isExpired(now: Long = System.currentTimeMillis()): Boolean {
    return this.expires?.let { it <= now } ?: false
}
