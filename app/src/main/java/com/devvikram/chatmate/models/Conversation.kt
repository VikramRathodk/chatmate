package com.devvikram.chatmate.models

data class Conversation(val messageId:String, val senderId: String,val receiverId: String, val message: String, val timestamp: String, val isRead: Boolean)
