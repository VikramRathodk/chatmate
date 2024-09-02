package com.devvikram.chatmate.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.devvikram.chatmate.db.dao.ConversationDao
import com.devvikram.chatmate.db.dao.UserDao
import com.devvikram.chatmate.db.model.Conversation
import com.devvikram.chatmate.retrofit.model.Users

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS Conversation (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                senderId TEXT NOT NULL,
                receiverId TEXT NOT NULL,
                messageType TEXT NOT NULL,
                roomPrimaryKey INTEGER NOT NULL DEFAULT 0,
                isRead INTEGER NOT NULL DEFAULT 0,
                messageId TEXT NOT NULL,
                fileUrl TEXT NOT NULL,
                message TEXT NOT NULL,
                timestamp INTEGER NOT NULL
            )
        """)
    }
}



@Database(
    entities = [Users::class,Conversation::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun conversationDao(): ConversationDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "chatmate_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance

            }
        }
    }


}