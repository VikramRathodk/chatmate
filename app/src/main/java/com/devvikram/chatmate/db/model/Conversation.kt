package com.devvikram.chatmate.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Conversation")
data class Conversation(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "senderId")
    val senderId: String = "",
    @ColumnInfo(name = "receiverId")
    val receiverId: String = "",
    @ColumnInfo(name = "messageType")
    val messageType: String = "",
    @ColumnInfo(name = "roomPrimaryKey")
    val roomPrimaryKey: Int = 0,
    @ColumnInfo(name = "isRead")
    val isRead: Boolean = false,
    @ColumnInfo(name = "messageId")
    var messageId: String = "",
    @ColumnInfo(name = "fileUrl")
    val fileUrl: String = "",
    @ColumnInfo(name = "message")
    val message: String = "",
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = 0L
)
