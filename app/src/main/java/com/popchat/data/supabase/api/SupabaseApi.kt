package com.popchat.data.supabase.api

import com.popchat.data.supabase.model.*
import io.github.jan_tennert.supabase_kt.postgrest.PostgrestQueryBuilder
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.*

interface SupabaseApi {

    @GET("rest/v1/chats")
    fun getChats(
        @Query("select") select: String = "*",
        @Query("is_archived") isArchived: Boolean = false,
        @Query("order") order: String = "last_message_at.desc.nulls_last",
        @Query("limit") limit: Int = 50
    ): PostgrestQueryBuilder<List<SupabaseChat>>

    @GET("rest/v1/chats")
    fun getChatById(
        @Query("id") id: String,
        @Query("select") select: String = "*"
    ): PostgrestQueryBuilder<SupabaseChat>

    @POST("rest/v1/chats")
    fun createChat(
        @Header("Prefer") prefer: String,
        @Body chat: SupabaseChat
    ): PostgrestQueryBuilder<SupabaseChat>

    @PATCH("rest/v1/chats")
    fun updateChat(
        @Query("id") id: String,
        @Header("Prefer") prefer: String,
        @Body chat: SupabaseChat
    ): PostgrestQueryBuilder<SupabaseChat>

    @DELETE("rest/v1/chats")
    fun deleteChat(
        @Query("id") id: String
    ): PostgrestQueryBuilder<Unit>

    @GET("rest/v1/messages")
    fun getMessages(
        @Query("chat_id") chatId: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "created_at.desc",
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): PostgrestQueryBuilder<List<SupabaseMessage>>

    @GET("rest/v1/messages")
    fun getMessageById(
        @Query("id") id: String
    ): PostgrestQueryBuilder<SupabaseMessage>

    @POST("rest/v1/messages")
    fun sendMessage(
        @Header("Prefer") prefer: String,
        @Body message: SupabaseMessage
    ): PostgrestQueryBuilder<SupabaseMessage>

    @PATCH("rest/v1/messages")
    fun updateMessage(
        @Query("id") id: String,
        @Header("Prefer") prefer: String,
        @Body message: SupabaseMessage
    ): PostgrestQueryBuilder<SupabaseMessage>

    @DELETE("rest/v1/messages")
    fun deleteMessage(
        @Query("id") id: String
    ): PostgrestQueryBuilder<Unit>

    @GET("rest/v1/chat_participants")
    fun getParticipants(
        @Query("chat_id") chatId: String,
        @Query("select") select: String = "*"
    ): PostgrestQueryBuilder<List<SupabaseParticipant>>

    @POST("rest/v1/chat_participants")
    fun addParticipant(
        @Header("Prefer") prefer: String,
        @Body participant: SupabaseParticipant
    ): PostgrestQueryBuilder<SupabaseParticipant>

    @PATCH("rest/v1/chat_participants")
    fun updateParticipant(
        @Query("chat_id") chatId: String,
        @Query("user_id") userId: String,
        @Header("Prefer") prefer: String,
        @Body participant: SupabaseParticipant
    ): PostgrestQueryBuilder<SupabaseParticipant>

    @DELETE("rest/v1/chat_participants")
    fun removeParticipant(
        @Query("chat_id") chatId: String,
        @Query("user_id") userId: String
    ): PostgrestQueryBuilder<Unit>

    @GET("rest/v1/users")
    fun searchUsers(
        @Query("username") username: String,
        @Query("select") select: String = "*",
        @Query("limit") limit: Int = 20
    ): PostgrestQueryBuilder<List<SupabaseUser>>

    @GET("rest/v1/users")
    fun getUserById(
        @Query("id") id: String
    ): PostgrestQueryBuilder<SupabaseUser>

    @POST("rest/v1/users")
    fun upsertUser(
        @Header("Prefer") prefer: String,
        @Body user: SupabaseUser
    ): PostgrestQueryBuilder<SupabaseUser>

    // Storage
    @POST("storage/v1/object/chat-media/{bucket}")
    fun uploadMedia(
        @Path("bucket") bucket: String,
        @Header("Authorization") auth: String,
        @Body file: okhttp3.RequestBody
    ): PostgrestQueryBuilder<SupabaseStorageResponse>

    @GET("storage/v1/object/public/chat-media/{path}")
    fun getMediaUrl(@Path("path") path: String): String
}

@Serializable
data class SupabaseStorageResponse(
    val Key: String,
    val Id: String,
    val FullPath: String
)