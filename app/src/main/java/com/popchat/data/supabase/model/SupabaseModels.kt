package com.popchat.data.supabase.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseUser(
    val id: String,
    val email: String?,
    val phone: String?,
    val createdAt: String,
    val updatedAt: String,
    val userMetadata: JsonObject?,
    val appMetadata: JsonObject?
)

@Serializable
data class JsonObject(
    private val map: Map<String, JsonValue>
) {
    fun getString(key: String): String? {
        val found = map[key]
        // JSON has one number type, so a numeric metadata value is read back
        // as text. Nested objects and arrays have no sensible scalar
        // rendering here and return null.
        return when (found) {
            is JsonValue.String -> found.value
            is JsonValue.Number -> found.value.toString()
            is JsonValue.Boolean -> found.value.toString()
            else -> null
        }
    }

    fun getBool(key: String): Boolean? {
        val found = map[key]
        return when (found) {
            is JsonValue.Boolean -> found.value
            else -> null
        }
    }

    fun getJsonObject(key: String): JsonObject? {
        val value = map[key]
        return if (value is JsonValue.Object) value.value else null
    }
}

/**
 * Minimal JSON value model, kept app-local so the domain layer does not depend
 * on supabase-kt's serialisation types.
 *
 * The nested class names shadow the Kotlin builtins of the same name inside
 * their own scope, so the payload types are written fully qualified:
 * `data class String(val value: String)` would declare a `JsonValue.String`
 * whose `value` is another `JsonValue.String`, and the same for `Boolean`.
 */
sealed interface JsonValue {
    @Serializable
    data class String(val value: kotlin.String) : JsonValue

    @Serializable
    data class Number(val value: Double) : JsonValue

    @Serializable
    data class Boolean(val value: kotlin.Boolean) : JsonValue

    @Serializable
    data class Object(val value: JsonObject) : JsonValue

    @Serializable
    data class Array(val value: List<JsonValue>) : JsonValue

    @Serializable
    object Null : JsonValue
}

@Serializable
data class SupabaseChat(
    val id: String,
    val name: String?,
    val avatar_url: String?,
    val is_group: Boolean,
    val created_by: String?,
    val last_message_id: String?,
    val last_message_preview: String?,
    val last_message_at: String?,
    val unread_count: Int,
    val is_archived: Boolean,
    val is_pinned: Boolean,
    val created_at: String,
    val updated_at: String
)

@Serializable
data class SupabaseMessage(
    val id: String,
    val chat_id: String,
    val sender_id: String,
    val content: String,
    val type: String,
    val media_url: String?,
    val media_type: String?,
    val media_size: Long?,
    val reply_to_id: String?,
    val is_edited: Boolean,
    val is_deleted: Boolean,
    val created_at: String,
    val updated_at: String,
    val delivered_at: String?,
    val read_at: String?
)

@Serializable
data class SupabaseParticipant(
    val chat_id: String,
    val user_id: String,
    val role: String,
    val joined_at: String,
    val last_read_message_id: String?,
    val is_muted: Boolean,
    val muted_until: String?
)

@Serializable
data class SupabaseRealtimeMessage(
    val event: String,
    val table: String,
    val schema: String,
    val payload: RealtimePayload
)

@Serializable
data class RealtimePayload(
    val data: JsonObject,
    val oldRecord: JsonObject?
)

@Serializable
data class AuthResponse(
    val user: SupabaseUser?,
    val session: SupabaseSession?,
    val error: AuthError?
)

@Serializable
data class SupabaseSession(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long,
    val user: SupabaseUser
)

@Serializable
data class AuthError(
    val message: String,
    val code: String?
)

@Serializable
data class PaginatedResponse<T>(
    val data: List<T>,
    val count: Int?,
    val nextCursor: String?
)