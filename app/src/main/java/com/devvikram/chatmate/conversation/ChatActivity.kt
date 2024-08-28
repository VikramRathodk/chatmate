package com.devvikram.chatmate.conversation

import ConversationAdapter
import SharedPreference
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.devvikram.chatmate.ImagePreviewChatActivity
import com.devvikram.chatmate.R
import com.devvikram.chatmate.conversation.model.Conversation
import com.devvikram.chatmate.databinding.ActivityChatBinding
import com.devvikram.chatmate.models.DocumentModel
import com.devvikram.chatmate.util.CameraActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File
import java.io.FileOutputStream

class ChatActivity : AppCompatActivity() {

    private val CAMERA_REQUEST_CODE = 2001
    private val CAMERA_PERMISSION_CODE = 2000
    private val IMAGE_PREVIEW_REQUEST_CODE = 2002
    private val GALLERY_REQUEST_CODE = 2003
    private val VIDEO_REQUEST_CODE = 2004
    private val DOCUMENT_REQUEST_CODE = 2005

    private lateinit var binding: ActivityChatBinding
    private lateinit var messageViewModel: MessageViewModel
    private lateinit var senderRoomId: String
    private lateinit var receiverRoomId: String
    private lateinit var conversationAdapter: ConversationAdapter
    private val documentFileList = ArrayList<DocumentModel>()
    private var receiverEmail: String? = null
    private var receiverName: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = MessageViewModelFactory()
        messageViewModel = ViewModelProvider(this, factory)[MessageViewModel::class.java]

        val intent = intent
        val receiverID = intent.getStringExtra("receiver_id")
        receiverEmail = intent.getStringExtra("receiver_email")
        receiverName = intent.getStringExtra("receiver_name")
        val currentUserUid = SharedPreference(applicationContext).getUid()
        senderRoomId = currentUserUid + receiverID!!
        receiverRoomId = receiverID + currentUserUid

        binding.userName.text = receiverName
        binding.userEmail.text = receiverEmail
        binding.userName.setOnClickListener {
            Log.d(TAG, "onCreate: button clicked")
            val intent = Intent(this, ImagePreviewChatActivity::class.java)
            intent.putExtra("receiver_email", receiverEmail)
            intent.putExtra("receiver_name", receiverName)
            startActivity(intent)
        }

        setupRecyclerView()
        observeViewModel()

        binding.sendIconBtn.setOnClickListener {
            val message = binding.chatMessageEdittext.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(message)
                binding.chatMessageEdittext.text.clear()
            }
        }

        binding.attachmentIconBtn.setOnClickListener {
            showAttachmentBottomSheet()
        }
        binding.cameraIconBtn.setOnClickListener {
            showCamera()
        }

    }

    private fun showCamera() {
        val cameraPermission =
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

        Log.d("Permissions", "Camera Permission: $cameraPermission")

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    android.Manifest.permission.CAMERA,
                ), CAMERA_PERMISSION_CODE
            )
        } else {
            startCameraIntent()
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        layoutManager.isSmoothScrollbarEnabled = true
        binding.chatRecyclerview.layoutManager = layoutManager
        conversationAdapter = ConversationAdapter(this, messageViewModel)
        binding.chatRecyclerview.adapter = conversationAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                documentFileList.clear()
                val capturedImagesList: ArrayList<Uri>? =
                    data?.getStringArrayListExtra("captured_images_list")?.map {
                        Uri.parse(it)
                    }?.toCollection(ArrayList())
                capturedImagesList?.forEach {
                    val uriString = it.toString()
                    Log.d(TAG, "onActivityResult: $uriString")
                    documentFileList.add(
                        DocumentModel(
                            "filename.jpg", uriString, "image", true
                        )
                    )
                }
                showPreviewOfImage(documentFileList)
                Log.d(TAG, "onActivityResult: image file size:  $capturedImagesList")
                Log.d(
                    TAG,
                    "onActivityResult: document size:  ${documentFileList.size} ,,, $documentFileList"
                )

            }
            if (requestCode == GALLERY_REQUEST_CODE) {
                val selectedImageUri: Uri? = data?.data

                if (selectedImageUri != null) {
                    documentFileList.clear()
                    documentFileList.add(
                        DocumentModel(
                            "filename.jpg", selectedImageUri.toString(), "image", true
                        )
                    )
                    showPreviewOfImage(documentFileList)
                }
            }
            if (requestCode == VIDEO_REQUEST_CODE) {
                val selectedVideoUri: Uri? = data?.data
                if (selectedVideoUri != null) {
                    documentFileList.clear()
                    documentFileList.add(
                        DocumentModel(
                            "filename.mp4", selectedVideoUri.toString(), "video", true
                        )
                    )
                    showPreviewOfImage(documentFileList)
                }
            }
            if (requestCode == DOCUMENT_REQUEST_CODE) {
                val selectedDocumentUri: Uri? = data?.data
                if (selectedDocumentUri != null) {
                    documentFileList.clear()
                    documentFileList.add(
                        DocumentModel(
                            "filename.pdf", selectedDocumentUri.toString(), "pdf", true
                        )
                        )
                    showPreviewOfImage(documentFileList)
                }
                Log.d(TAG, "onActivityResult: document size:  ${documentFileList.size} ,,, $documentFileList")

            }



            if (requestCode == IMAGE_PREVIEW_REQUEST_CODE) {
                val documentFileList =
                    data?.getParcelableArrayListExtra<DocumentModel>("document_file_list")
                if (documentFileList != null) {
                    sendImages(documentFileList)
                }
            }
        }
    }

    private fun sendImages(documentFileList: java.util.ArrayList<DocumentModel>) {
        documentFileList.map { documentModel ->
            messageViewModel.setMessageWithAttachment(
                documentModel, senderRoomId, receiverRoomId, applicationContext
            );
        }
    }

    private fun showPreviewOfImage(documentFileList: ArrayList<DocumentModel>) {
        val intent = Intent(this, ImagePreviewChatActivity::class.java)
        intent.putParcelableArrayListExtra("document_file_list", documentFileList)
        intent.putExtra("receiver_email", receiverEmail)
        intent.putExtra("receiver_name", receiverName)
        startActivityForResult(intent, IMAGE_PREVIEW_REQUEST_CODE);
    }

    private fun saveBitmapToFile(bitmap: Bitmap): Uri? {
        val directory = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "ChatMateImages")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val filename = "image_${System.currentTimeMillis()}.jpg"
        val file = File(directory, filename)
        return try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            Log.d(TAG, "Image saved to ${file.absolutePath}")

            FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Error saving image: ${e.message}")
            null
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                startCameraIntent()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCameraIntent() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
        Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
    }

    private fun observeViewModel() {
        messageViewModel.loadMessages(receiverRoomId)
        messageViewModel.messages.observe(this) { messages ->
            messages?.let {
                conversationAdapter.submitList(messages)
                binding.chatRecyclerview.post {
                    binding.chatRecyclerview.scrollToPosition(messages.size - 1)
                }
            }
        }
    }

    private fun sendMessage(message: String) {
        val messageId = "Message-${System.currentTimeMillis()}"
        val messageModel = Conversation(
            messageId = messageId,
            senderId = SharedPreference(applicationContext).getUid().toString(),
            receiverId = receiverRoomId,
            message = message,
            fileUrl = "",
            messageType = "text",
            timestamp = System.currentTimeMillis(),
            isRead = true,
            documentModel = null
        )
        messageViewModel.sendMessage(messageModel, senderRoomId, receiverRoomId)
    }

    private fun showAttachmentBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
        val videoBtn = view.findViewById<TextView>(R.id.attach_video)
        val galleryBtn = view.findViewById<TextView>(R.id.attach_gallery)
        val attachDocumentBtn = view.findViewById<TextView>(R.id.attach_document)

        attachDocumentBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "*/*"
            startActivityForResult(intent, DOCUMENT_REQUEST_CODE)
            bottomSheetDialog.dismiss()
        }

        videoBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "video/*"
            startActivityForResult(intent, VIDEO_REQUEST_CODE)
            bottomSheetDialog.dismiss()

        }
        galleryBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }
}
