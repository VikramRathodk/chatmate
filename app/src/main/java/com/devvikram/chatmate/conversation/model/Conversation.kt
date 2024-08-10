package com.devvikram.chatmate.conversation.model

data class Conversation(
    var messageId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val fileUrl: String = "",
    val messageType: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
