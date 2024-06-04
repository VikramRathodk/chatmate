package com.devvikram.chatmate

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
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

        conversationList.add(Conversation("Alice", "Hi, how are you?", "10:00 AM"))
        conversationList.add(Conversation("Bob", "I'm good, thanks! How about you?", "10:01 AM"))
        conversationList.add(Conversation("Alice", "I'm fine, just working on a project.", "10:02 AM"))
        conversationList.add(Conversation("Bob", "That sounds interesting. Need any help?", "10:03 AM"))
// Add more items as needed

        val linearLayoutManager = LinearLayoutManager(this)
        binding.chatRecyclerview.layoutManager = linearLayoutManager
        binding.chatRecyclerview.adapter = ConversationAdapter(this, conversationList)
        binding.chatRecyclerview.scrollToPosition(binding.chatRecyclerview.adapter!!.itemCount - 1)
        binding.chatRecyclerview.isNestedScrollingEnabled = false

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