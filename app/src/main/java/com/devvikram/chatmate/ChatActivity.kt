package com.devvikram.chatmate

import SharedPreference
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.devvikram.chatmate.adapters.ConversationAdapter
import com.devvikram.chatmate.conversation.MessageViewModel
import com.devvikram.chatmate.conversation.MessageViewModelFactory
import com.devvikram.chatmate.databinding.ActivityChatBinding
import com.devvikram.chatmate.models.Conversation
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File
import java.io.FileOutputStream

class ChatActivity : AppCompatActivity() {
    private val CAMERA_REQUEST_CODE = 2001
    private val CAMERA_PERMISSION_CODE = 2000
    private lateinit var binding: ActivityChatBinding
    private lateinit var messageViewModel: MessageViewModel
    private lateinit var senderRoomId: String
    private lateinit var receiverRoomId: String
    private lateinit var conversationAdapter: ConversationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = MessageViewModelFactory()
        messageViewModel = ViewModelProvider(this, factory).get(MessageViewModel::class.java)

        val intent = intent
        val receiverID = intent.getStringExtra("receiver_id")
        val receiverEmail = intent.getStringExtra("receiver_email")
        val receiverName = intent.getStringExtra("receiver_name")
        val currentUserUid = SharedPreference(applicationContext).getUid()
        senderRoomId = currentUserUid + receiverID!!
        receiverRoomId = receiverID + currentUserUid

        binding.userName.text = receiverName
        binding.userEmail.text = receiverEmail

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
        binding.cameraIconBtn.setOnClickListener{
            showCamera()
        }

//        binding.chatMessageEdittext.addTextChangedListener {
//            handleCameraIconVisibility(it.toString())
//        }
    }

    private fun showCamera() {
        val cameraPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

        Log.d("Permissions", "Camera Permission: $cameraPermission")

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.CAMERA,
                ),
                CAMERA_PERMISSION_CODE
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
        conversationAdapter = ConversationAdapter(this)
        binding.chatRecyclerview.adapter = conversationAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap?
            Log.d(TAG, "onActivityResult: $imageBitmap")
            if (imageBitmap != null) {
                val imageUri = saveBitmapToFile(imageBitmap)
                Log.d(TAG, "onActivityResult: file uri is :\n $imageUri")
            }
        }
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
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
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
        Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
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
            timestamp = System.currentTimeMillis(),
            isRead = true
        )
        messageViewModel.sendMessage(messageModel, senderRoomId, receiverRoomId)
    }

    private fun showAttachmentBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    private fun handleCameraIconVisibility(text: String) {
        val isVisible = text.isNotEmpty()
        if (isVisible && binding.cameraIconBtn.visibility == View.VISIBLE) {
            binding.cameraIconBtn.animate()
                .translationX(-binding.cameraIconBtn.width.toFloat())
                .setDuration(400)
                .withEndAction {
                    binding.cameraIconBtn.visibility = View.GONE
                }
        } else if (!isVisible && binding.cameraIconBtn.visibility == View.GONE) {
            binding.cameraIconBtn.apply {
                translationX = -width.toFloat()
                visibility = View.VISIBLE
                animate()
                    .translationX(0f)
                    .setDuration(400)
                    .withEndAction(null)
            }
        }
    }
}
