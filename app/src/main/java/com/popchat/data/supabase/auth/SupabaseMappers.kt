package com.popchat.data.supabase.auth

import com.popchat.data.supabase.model.JsonObject
import com.popchat.data.supabase.model.JsonValue
import com.popchat.data.supabase.model.SupabaseSession
import com.popchat.data.supabase.model.SupabaseUser
import io.github.jan.supabase.gotrue.user.UserInfo
import io.github.jan.supabase.gotrue.user.UserSession
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject as KxJsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Bridges supabase-kt's `UserInfo` / `UserSession` to the app's own
 * `SupabaseUser` / `SupabaseSession` model types.
 *
 * These are deliberately separate types. The app models are what Room, the
 * repositories and the UI speak, so a library upgrade cannot ripple through the
 * whole app the way it would if the library types leaked into the domain layer.
 */
fun UserInfo.toAppUser(): SupabaseUser = SupabaseUser(
    id = id,
    email = email,
    phone = phone,
    // Stored as text, matching the model; UserEntity parses it with Instant.parse.
    createdAt = createdAt.toString(),
    updatedAt = updatedAt.toString(),
    // Both metadata fields are nullable on UserInfo but non-null in the app
    // model, so a missing value becomes an empty object: getString() then
    // returns null instead of the caller having to null-check twice.
    userMetadata = userMetadata?.toAppJsonObject() ?: JsonObject(emptyMap()),
    appMetadata = appMetadata?.toAppJsonObject() ?: JsonObject(emptyMap())
)

fun UserSession.toAppSession(): SupabaseSession = SupabaseSession(
    accessToken = accessToken,
    refreshToken = refreshToken,
    expiresAt = expiresAt.toEpochMilliseconds(),
    user = requireNotNull(user) { "Supabase returned a session with no user" }.toAppUser()
)

/**
 * Converts a kotlinx.serialization JSON object into the app's small JSON tree.
 *
 * An object nested inside a value is wrapped rather than dropped, so
 * `getJsonObject("address")` keeps working after the round trip.
 */
fun KxJsonObject.toAppJsonObject(): JsonObject = JsonObject(
    map = mapValues { (_, value) -> value.toAppJsonValue() }
)

private fun kotlinx.serialization.json.JsonElement.toAppJsonValue(): JsonValue = when (this) {
    // JsonNull is a JsonPrimitive, so it has to be matched before JsonPrimitive
    // or it would be reported as the literal string "null".
    kotlinx.serialization.json.JsonNull -> JsonValue.Null

    is JsonPrimitive -> if (isString) {
        JsonValue.String(value = content)
    } else {
        content.toDoubleOrNull()
            ?.let { JsonValue.Number(it) }
            ?: JsonValue.Null
    }

    is KxJsonObject -> JsonValue.Object(value = toAppJsonObject())
    is JsonArray -> JsonValue.Array(value = map { it.toAppJsonValue() })
}
