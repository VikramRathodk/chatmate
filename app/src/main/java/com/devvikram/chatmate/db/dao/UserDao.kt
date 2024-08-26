package com.devvikram.chatmate.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.devvikram.chatmate.retrofit.model.Users


@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: List<Users>)

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<Users>

}