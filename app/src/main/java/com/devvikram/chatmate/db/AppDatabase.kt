package com.devvikram.chatmate.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.devvikram.chatmate.db.dao.UserDao
import com.devvikram.chatmate.retrofit.model.Users


@Database(entities = [Users::class ], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "chatmate_database"
                ).build()
                INSTANCE = instance
                instance

            }
        }
    }


}