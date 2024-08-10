package com.devvikram.chatmate

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.devvikram.chatmate.databinding.ActivityImagePreviewChatBinding
import com.devvikram.chatmate.models.DocumentModel
import com.devvikram.chatmate.util.DocumentAdapter

class ImagePreviewChatActivity : AppCompatActivity(), DocumentAdapter.DocumentActionListener {
    private lateinit var binding: ActivityImagePreviewChatBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var documentAdapter: DocumentAdapter
    private lateinit var documentFileList: ArrayList<DocumentModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImagePreviewChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        documentFileList = intent.getParcelableArrayListExtra("document_file_list") ?: arrayListOf()

        documentAdapter = DocumentAdapter(this, documentFileList, this)
        viewPager = binding.imageViewPager
        viewPager.adapter = documentAdapter

    }


    override fun onDeleteClick(documentModel: DocumentModel) {
        val position = documentFileList.indexOf(documentModel)
        if (position != -1) {
            documentFileList.removeAt(position)
            documentAdapter.notifyItemRemoved(position)
            documentAdapter.notifyItemRangeChanged(position, documentFileList.size)
        }
    }

    override fun onDocumentSubmit(documentModel: DocumentModel) {
        val intent = Intent().apply {
            putParcelableArrayListExtra("document_file_list", documentFileList)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }



}
