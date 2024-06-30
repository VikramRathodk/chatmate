package com.devvikram.chatmate.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class MessageViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MessageViewModel::class.java)) {
            return MessageViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
