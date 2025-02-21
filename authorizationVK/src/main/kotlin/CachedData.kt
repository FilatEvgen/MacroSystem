data class CachedData(
    val code: String,
    val deviceId: String
)

object Cache {
    private val cache = mutableMapOf<String, CachedData>()

    fun put(state: String, code: String, deviceId: String) {
        cache[state] = CachedData(code, deviceId)
    }

    fun get(state: String): CachedData? {
        return cache[state]
    }

    fun remove(state: String) {
        cache.remove(state)
    }
}