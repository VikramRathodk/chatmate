package com.devvikram.chatmate.conversation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devvikram.chatmate.models.Conversation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class MessageViewModel : ViewModel() {

    private val _messages = MutableLiveData<List<Conversation>>()
    private val firestore = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    val messages: LiveData<List<Conversation>> get() = _messages

    fun sendMessage(message: Conversation, senderRoomId: String, receiverRoomId: String) {
        val senderRoomRef = firestore.collection("conversations").document(senderRoomId).collection("messages")
        senderRoomRef.add(message)
            .addOnSuccessListener { senderDocumentReference ->
                // Message added successfully to sender room
                val messageId = senderDocumentReference.id
                message.messageId = messageId // Update message ID with Firestore-generated ID

                // Update local _messages LiveData immediately
                _messages.value = _messages.value?.plus(message)

                // Now add to receiver room
                val receiverRoomRef = firestore.collection("conversations").document(receiverRoomId).collection("messages")
                receiverRoomRef.add(message)
                    .addOnFailureListener { e ->
                        // Handle failure to add message to receiver room
                        // Here you might consider rolling back the _messages update or retrying
                    }
            }
            .addOnFailureListener { e ->
                // Handle failure to add message to sender room
            }
    }

    fun loadMessages(roomId: String) {
        listenerRegistration?.remove()

        // Ensure proper cleanup and setup of snapshot listener
        val senderRoomRef = firestore.collection("conversations").document(roomId).collection("messages")
        listenerRegistration = senderRoomRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                // Handle error
                // For example, show error message or retry logic
                return@addSnapshotListener
            }

            val messages = snapshot?.toObjects(Conversation::class.java)
            _messages.value = messages // Update LiveData with new messages
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
