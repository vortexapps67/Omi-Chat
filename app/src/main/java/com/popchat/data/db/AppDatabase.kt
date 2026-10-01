package com.popchat.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.popchat.data.db.converters.Converters
import com.popchat.data.db.dao.ChatDao
import com.popchat.data.db.dao.ChatParticipantDao
import com.popchat.data.db.dao.MessageDao
import com.popchat.data.db.dao.UserDao
import com.popchat.data.model.ChatEntity
import com.popchat.data.model.ChatParticipantEntity
import com.popchat.data.model.MessageEntity
import com.popchat.data.model.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ChatEntity::class,
        MessageEntity::class,
        ChatParticipantEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
    abstract fun chatParticipantDao(): ChatParticipantDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "popchat.db"
                )
                    .fallbackToDestructiveMigration()
                    .setAutoCloseTimeout(30)
                    .enableMultiInstanceInvalidation()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun destroyInstance() {
            INSTANCE?.let { it.close() }
            INSTANCE = null
        }
    }
}