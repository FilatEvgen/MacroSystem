package ru.dragon_slayer.authorization

object AuthCache {
    private val cache = mutableMapOf<String, String>()

    fun put(state: String, codeVerifier: String) {
        cache[state] = codeVerifier
    }

    fun getCodeVerifier(state: String): String? {
        return try {
            cache[state]
        } finally {
            remove(state)
        }
    }

    private fun remove(state: String) {
        cache.remove(state)
    }
}