package com.devvikram.chatmate.conversation

import SharedPreference
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devvikram.chatmate.models.Conversation
import com.devvikram.chatmate.models.DocumentModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MessageViewModel : ViewModel() {

    private val _messages = MutableLiveData<List<Conversation>>()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference

    private var listenerRegistration: ListenerRegistration? = null

    val messages: LiveData<List<Conversation>> get() = _messages

    fun sendMessage(message: Conversation, senderRoomId: String, receiverRoomId: String) {
        val senderRoomRef = firestore.collection("conversations").document(senderRoomId).collection("messages")
        senderRoomRef.add(message)
            .addOnSuccessListener { senderDocumentReference ->
                val messageId = senderDocumentReference.id
                message.messageId = messageId
                _messages.value = _messages.value?.plus(message)

                val receiverRoomRef = firestore.collection("conversations").document(receiverRoomId).collection("messages")
                receiverRoomRef.add(message)
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                    }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
    private fun uploadFile(messageId:String,uri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
        val fileRef = storageRef.child("chat_images/${messageId}_${uri.lastPathSegment}")

        fileRef.putFile(uri)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    onSuccess(downloadUri.toString())
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun setMessageWithAttachment(
        captionText: String,
        documentModel: DocumentModel,
        senderRoomId: String,
        receiverRoomId: String,
        applicationContext: Context
    ) {
        val messageId = "Message-${System.currentTimeMillis()}"
        uploadFile(messageId,documentModel.uri.toUri(), { fileUrl ->
            val messageModel = Conversation(
                messageId = messageId,
                senderId = SharedPreference(applicationContext).getUid().toString(),
                receiverId = receiverRoomId,
                message = captionText,
                fileUrl = fileUrl,
                messageType = documentModel.fileType,
                timestamp = System.currentTimeMillis(),
                isRead = false
            )

            sendMessage(messageModel, senderRoomId, receiverRoomId)
        }, { exception ->
            Log.e("ChatApp", "Failed to upload file: ${exception.message}")
            Toast.makeText(applicationContext, "Failed to upload attachment", Toast.LENGTH_SHORT).show()
        })
    }



    fun loadMessages(roomId: String) {
        listenerRegistration?.remove()

        val senderRoomRef = firestore.collection("conversations").document(roomId).collection("messages")
        listenerRegistration = senderRoomRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            val messages = snapshot?.toObjects(Conversation::class.java)
            _messages.value = messages
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
