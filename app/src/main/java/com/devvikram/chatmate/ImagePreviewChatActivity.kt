package com.devvikram.chatmate

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.devvikram.chatmate.databinding.ActivityImagePreviewChatBinding
import com.devvikram.chatmate.models.DocumentModel
import com.devvikram.chatmate.util.DocumentAdapter

class ImagePreviewChatActivity : AppCompatActivity() {
    private lateinit var binding : ActivityImagePreviewChatBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var documentAdapter : DocumentAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImagePreviewChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val documentFileList = intent.getParcelableArrayListExtra<DocumentModel>("document_file_list")

        documentAdapter = DocumentAdapter(this,documentFileList!!)
        viewPager = binding.imageViewPager
        viewPager.adapter = documentAdapter


        binding.sendIconBtn.setOnClickListener {
            sendBackFiles(documentFileList)

        }

    }

    private fun sendBackFiles(documentFileList: ArrayList<DocumentModel>) {
        val intent = Intent().apply {
            putParcelableArrayListExtra("document_file_list", documentFileList)
            putExtra("caption", binding.captionTextview.text.toString())
        }

        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}