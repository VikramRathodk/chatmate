package com.devvikram.chatmate

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.devvikram.chatmate.adapters.ConversationAdapter
import com.devvikram.chatmate.databinding.ActivityChatBinding
import com.devvikram.chatmate.models.Conversation
import com.google.android.material.bottomsheet.BottomSheetDialog

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val conversationList = mutableListOf<Conversation>()

        conversationList.add(
            Conversation(
                messageId = "1",
                senderId = "1",
                receiverId = "2",
                message = "Hi, how are you?",
                timestamp = "10:00 AM",
                isRead = false
            )
        )

        conversationList.add(
            Conversation(
                messageId = "2",
                senderId = "2",
                receiverId = "1",
                message = "I'm good, thanks! How about you?",
                timestamp = "10:01 AM",
                isRead = true
            )
        )

        conversationList.add(
            Conversation(
                messageId = "3",
                senderId = "1",
                receiverId = "2",
                message = "I'm fine, just working on a project.",
                timestamp = "10:02 AM",
                isRead = false
            )
        )


        val linearLayoutManager = LinearLayoutManager(this)
        binding.chatRecyclerview.layoutManager = linearLayoutManager
        binding.chatRecyclerview.adapter = ConversationAdapter(this, conversationList)
        binding.chatRecyclerview.scrollToPosition(binding.chatRecyclerview.adapter!!.itemCount - 1)
        binding.chatRecyclerview.isNestedScrollingEnabled = false

        binding.chatMessageEdittext.addTextChangedListener {
            if (binding.chatMessageEdittext.text.toString().isNotEmpty()) {
                if (binding.cameraIconBtn.visibility == View.VISIBLE) {
                    binding.cameraIconBtn.animate()
                        .translationX(-binding.cameraIconBtn.width.toFloat())
                        .setDuration(400)
                        .withEndAction {
                            binding.cameraIconBtn.visibility = View.GONE
                        }
                }
            } else {
                if (binding.cameraIconBtn.visibility == View.GONE) {
                    binding.cameraIconBtn.apply {
                        translationX = -binding.cameraIconBtn.width.toFloat()
                        visibility = View.VISIBLE
                        animate()
                            .translationX(0f)
                            .setDuration(400)
                            .withEndAction(null)
                    }
                }
            }
        }


        binding.sendIconBtn.setOnClickListener {
//            add message to list
            conversationList.add(
                Conversation(
                    messageId = "1",
                    senderId = "1",
                    receiverId = "2",
                    message = "Hi, how are you?",
                    timestamp = "10:00 AM",
                    isRead = false
                )
            )
            binding.chatRecyclerview.adapter!!.notifyDataSetChanged()
            binding.chatRecyclerview.scrollToPosition(binding.chatRecyclerview.adapter!!.itemCount - 1)
            binding.chatMessageEdittext.setText("")

        }


        binding.attachmentIconBtn.setOnClickListener {
            //open bottom sheet
            val bottomSheetDialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)

            val dialogDismissBtn = view.findViewById<Button>(R.id.dismiss_dialog_btn)
            dialogDismissBtn.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.setContentView(view)
            bottomSheetDialog.show()

        }

    }
}