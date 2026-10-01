package com.popchat.data.db.converters

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

/**
 * Room type converters for the entity column types.
 *
 * The Json calls name their serializer explicitly: `serializer()` is a reified
 * function, so the bare `Json.encodeToString(serializer(), list)` form the code
 * used before has no type argument to infer from and does not compile.
 */
class Converters {

    private val json = Json

    // kotlinx-datetime 0.6 exposes these as toEpochMilliseconds() and
    // Instant.fromEpochMilliseconds(); the 0.4-era epochMilliseconds property
    // and takeBefore-style extensions no longer exist.
    @TypeConverter
    fun fromInstant(value: Instant?): Long? = value?.toEpochMilliseconds()

    @TypeConverter
    fun toInstant(value: Long?): Instant? = value?.let { Instant.fromEpochMilliseconds(it) }

    @TypeConverter
    fun fromStringList(value: List<String>?): String? =
        value?.let { json.encodeToString(StringListSerializer, it) }

    @TypeConverter
    fun toStringList(value: String?): List<String>? =
        value?.let { json.decodeFromString(StringListSerializer, it) }

    @TypeConverter
    fun fromIntList(value: List<Int>?): String? =
        value?.let { json.encodeToString(IntListSerializer, it) }

    @TypeConverter
    fun toIntList(value: String?): List<Int>? =
        value?.let { json.decodeFromString(IntListSerializer, it) }

    @TypeConverter
    fun fromMap(value: Map<String, String>?): String? =
        value?.let { json.encodeToString(StringMapSerializer, it) }

    @TypeConverter
    fun toMap(value: String?): Map<String, String>? =
        value?.let { json.decodeFromString(StringMapSerializer, it) }

    private companion object {
        val StringListSerializer = ListSerializer(String.serializer())
        val IntListSerializer = ListSerializer(Int.serializer())
        val StringMapSerializer = MapSerializer(String.serializer(), String.serializer())
    }
}
