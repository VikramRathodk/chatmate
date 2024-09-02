package com.devvikram.chatmate.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.devvikram.chatmate.db.model.Conversation

@Dao
interface  ConversationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertConversation(conversations: List<Conversation>)


    @Query("SELECT * FROM Conversation WHERE senderId = :id ")
    fun getConversation(id: String): List<Conversation>
}