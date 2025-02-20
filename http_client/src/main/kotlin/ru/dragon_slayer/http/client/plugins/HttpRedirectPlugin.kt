package ru.dragon_slayer.http.client.plugins

import io.ktor.util.*

class RedirectLocations {
    companion object {
        val key: AttributeKey<RedirectLocations> = AttributeKey(name = "RedirectLocations")
    }

    private val locations = ArrayList<String>()
    fun getLocation(): List<String> = locations
    fun addLocation(location: String) {
        locations.add(location)
    }
}
