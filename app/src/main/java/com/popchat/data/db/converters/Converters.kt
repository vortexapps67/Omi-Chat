package com.popchat.data.db.converters

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.DateTimeFormatter
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.serializer

class Converters {

    @TypeConverter
    fun fromInstant(value: Instant?): Long? {
        return value?.epochMilliseconds
    }

    @TypeConverter
    fun toInstant(value: Long?): Instant? {
        return value?.let { Instant.fromEpochMilliseconds(it) }
    }

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { Json.encodeToString(serializer(), it) }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let { Json.decodeFromString(serializer(), it) }
    }

    @TypeConverter
    fun fromIntList(value: List<Int>?): String? {
        return value?.let { Json.encodeToString(serializer(), it) }
    }

    @TypeConverter
    fun toIntList(value: String?): List<Int>? {
        return value?.let { Json.decodeFromString(serializer(), it) }
    }

    @TypeConverter
    fun fromMap(value: Map<String, String>?): String? {
        return value?.let { Json.encodeToString(serializer(), it) }
    }

    @TypeConverter
    fun toMap(value: String?): Map<String, String>? {
        return value?.let { Json.decodeFromString(serializer(), it) }
    }
}