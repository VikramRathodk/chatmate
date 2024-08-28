package com.devvikram.chatmate.conversation.model

import com.devvikram.chatmate.models.DocumentModel

data class Conversation(
    var messageId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val fileUrl: String = "",
    val messageType: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val documentModel: DocumentModel? = null)
