package ru.dragon_slayer.error_handler.base

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus

object Serializer {

    const val CLASS_DISCRIMINATOR_FIELD_NAME = "_type"

    @Volatile
    private var _serializer = createSerializer(EmptySerializersModule())

    val serializer get() = _serializer
    private fun createSerializer(m: SerializersModule): Json {
        return Json {
            classDiscriminator = CLASS_DISCRIMINATOR_FIELD_NAME
            encodeDefaults = false
            explicitNulls = false
            ignoreUnknownKeys = true
            isLenient = true
            serializersModule = m
        }
    }

    @Synchronized
    fun registerSerializersModule(m: SerializersModule) {
        _serializer = createSerializer(serializer.serializersModule + m)
    }
}
