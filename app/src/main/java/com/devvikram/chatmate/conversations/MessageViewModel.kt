package com.devvikram.chatmate.conversations

import SharedPreference
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.devvikram.chatmate.db.AppDatabase
import com.devvikram.chatmate.db.model.Conversation
import com.devvikram.chatmate.models.DocumentModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException

class MessageViewModel(application: Application) : AndroidViewModel(application) {

    private val _messages = MutableLiveData<List<Conversation>?>()
    private val firestore = FirebaseFirestore.getInstance()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var listenerRegistration: ListenerRegistration? = null
    private val appDatabase = AppDatabase.getDatabase(application)

    val messages: MutableLiveData<List<Conversation>?> get() = _messages

    fun sendMessage(message: Conversation, senderRoomId: String, receiverRoomId: String) {
        val senderRoomRef =
            firestore.collection("conversations").document(senderRoomId).collection("messages")
        senderRoomRef.add(message)
            .addOnSuccessListener { senderDocumentReference ->
                val messageId = senderDocumentReference.id
                message.messageId = messageId
                _messages.value = _messages.value?.plus(message)

                val receiverRoomRef = firestore.collection("conversations").document(receiverRoomId)
                    .collection("messages")
                receiverRoomRef.add(message)
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                    }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    private fun uploadFile(
        messageId: String,
        uri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
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
        documentModel: DocumentModel,
        senderRoomId: String,
        receiverRoomId: String,
        applicationContext: Context
    ) {
        val messageId = "Message-${System.currentTimeMillis()}"
        uploadFile(messageId, documentModel.uri.toUri(), { fileUrl ->
            val messageModel = Conversation(
                messageId = messageId,
                senderId = SharedPreference(applicationContext).getUid().toString(),
                receiverId = receiverRoomId,
                message = documentModel.caption,
                fileUrl = fileUrl,
                messageType = documentModel.fileType,
                timestamp = System.currentTimeMillis(),
                isRead = false,
                roomPrimaryKey = 0
            )

            sendMessage(messageModel, senderRoomId, receiverRoomId)
        }, { exception ->
            Log.e("ChatApp", "Failed to upload file: ${exception.message}")
            Toast.makeText(applicationContext, "Failed to upload attachment", Toast.LENGTH_SHORT)
                .show()
        })
    }


    //    fun loadMessages(roomId: String) {
//        listenerRegistration?.remove()
//
//        val senderRoomRef = firestore.collection("conversations").document(roomId).collection("messages")
//        listenerRegistration = senderRoomRef.addSnapshotListener { snapshot, e ->
//            if (e != null) {
//                return@addSnapshotListener
//            }
//            val messages = snapshot?.toObjects(com.devvikram.chatmate.db.model.Conversation::class.java)
//            if(messages!=null){
//                viewModelScope.launch {
//                    appDatabase.conversationDao().insertConversation(messages)
//                }
//            }
//
//            _messages.value = messages!!
//        }
//    }
    fun loadMessages(roomId: String) {
        viewModelScope.launch {
            val cachedMessages = withContext(Dispatchers.IO) {
                appDatabase.conversationDao().getConversation(roomId)
            }
            if (cachedMessages.isNotEmpty()) {
                _messages.value = cachedMessages
            } else {
                loadMessagesFromFirestore(roomId)
            }
        }
    }


    private fun loadMessagesFromFirestore(roomId: String) {
        listenerRegistration?.remove()

        val senderRoomRef =
            firestore.collection("conversations").document(roomId).collection("messages")

        listenerRegistration = senderRoomRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            val messages = snapshot?.toObjects(Conversation::class.java)


            if (messages != null) {
                viewModelScope.launch(Dispatchers.IO) {
                    appDatabase.conversationDao().insertConversation(messages)
                    withContext(Dispatchers.Main) {
                        _messages.value = messages
                    }
                }
            }
        }
    }


    private fun getLocalFile(context: Context, fileName: String): File {
        return File(context.filesDir, fileName)
    }

    private suspend fun downloadFileIfNeeded(
        context: Context,
        url: String,
        fileName: String,
        onProgress: (Int) -> Unit
    ): Uri? {
        val localFile = getLocalFile(context, fileName)

        if (localFile.exists()) {
            return Uri.fromFile(localFile)
        }

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val totalBytes = response.body?.contentLength() ?: 0L
                    var downloadedBytes = 0L

                    response.body?.byteStream()?.use { input ->
                        localFile.outputStream().use { output ->
                            val buffer = ByteArray(1024)
                            var bytesRead: Int
                            while (input.read(buffer).also { bytesRead = it } != -1) {
                                output.write(buffer, 0, bytesRead)
                                downloadedBytes += bytesRead
                                val progress = (100 * downloadedBytes / totalBytes).toInt()
                                withContext(Dispatchers.Main) {
                                    onProgress(progress)
                                }
                            }
                        }
                    }
                    Uri.fromFile(localFile)
                } else {
                    null
                }
            } catch (e: IOException) {
                Log.e("ChatApp", "Error downloading file: ${e.message}")
                null
            }
        }
    }

    fun downloadAndSetFileUri(
        conversation: Conversation,
        context: Context,
        onProgress: (Int) -> Unit,
        onDownloaded: (Uri?) -> Unit
    ) {
        val fileName =
            "file_${conversation.messageId}_${conversation.fileUrl.substringAfterLast("/")}"

        coroutineScope.launch {
            val uri = downloadFileIfNeeded(context, conversation.fileUrl, fileName, onProgress)
            onDownloaded(uri)
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }

    fun openPdf(conversation: Conversation, context: Context) {
        val fileName =
            "file_${conversation.messageId}_${conversation.fileUrl.substringAfterLast("/")}"
        val localFile = getLocalFile(context, fileName)
        val fileUri = Uri.fromFile(localFile)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(fileUri, "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(conversation.fileUrl))
            context.startActivity(browserIntent)
        }
    }
}
