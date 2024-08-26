package com.devvikram.chatmate.util

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.devvikram.chatmate.R
import com.devvikram.chatmate.models.DocumentModel
import com.squareup.picasso.Picasso

class DocumentAdapter(
    val activity: Activity,
    private val documentList: java.util.ArrayList<DocumentModel>?,
    private val documentActionListener: DocumentActionListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val IMAGE_VIEW_CONSTANT = 1
        private const val PDF_VIEW_CONSTANT = 2
        private const val EXCEL_VIEW_CONSTANT = 3
    }

    interface DocumentActionListener {
        fun onDeleteClick(documentModel: DocumentModel)
        fun onDocumentSubmit(documentModel: DocumentModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            IMAGE_VIEW_CONSTANT -> {
                val view = inflater.inflate(R.layout.image_preview_layout, parent, false)
                ImageViewHolder(view, documentActionListener)
            }
            PDF_VIEW_CONSTANT -> {
                val view = inflater.inflate(R.layout.pdf_preview_layout, parent, false)
                PdfViewHolder(view, documentActionListener)
            }
            else -> {
                val view = inflater.inflate(R.layout.excel_preview_layout, parent, false)
                ExcelViewHolder(view, documentActionListener)
            }
        }
    }

    override fun getItemCount(): Int {
        return documentList!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val documentModel = documentList?.get(position)
        when (holder.itemViewType) {
            IMAGE_VIEW_CONSTANT -> {
                val imageViewHolder = holder as ImageViewHolder
                if (documentModel != null) {
                    Log.d(TAG, "onBindViewHolder: ${documentModel.uri}")
                }
                if (documentModel != null) {
                    if (documentList != null) {
                        imageViewHolder.bind(documentModel, documentList)
                    }
                }
            }
            PDF_VIEW_CONSTANT -> {
                val pdfViewHolder = holder as PdfViewHolder
                if (documentModel != null) {
                    pdfViewHolder.bind(documentModel)
                }
            }
            EXCEL_VIEW_CONSTANT -> {
                val excelViewHolder = holder as ExcelViewHolder
                if (documentModel != null) {
                    excelViewHolder.bind(documentModel)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val documentModel = documentList!![position]
        val documentType = documentModel.fileType
        Log.d(TAG, "getItemViewType: $documentType")
        return when (documentType) {
            "pdf" -> PDF_VIEW_CONSTANT
            "image" -> IMAGE_VIEW_CONSTANT
            "excel" -> EXCEL_VIEW_CONSTANT
            else -> super.getItemViewType(position)
        }
    }

    private class ImageViewHolder(
        itemView: View,
        private val documentActionListener: DocumentActionListener
    ) : RecyclerView.ViewHolder(itemView) {
        private val imagePreview: ImageView = itemView.findViewById(R.id.image_preview)
        private val fileNameTextView: TextView = itemView.findViewById(R.id.file_name_textview)
        private val deleteImageView: ImageView = itemView.findViewById(R.id.delete_imageview)
        private val captionEditTextView: EditText = itemView.findViewById(R.id.caption_textview)
        private val sendBtn: ImageView = itemView.findViewById(R.id.send_icon_btn)

        fun bind(documentModel: DocumentModel,documentList: ArrayList<DocumentModel>) {
            fileNameTextView.text = documentModel.fileName
            captionEditTextView.setText(documentModel.caption)
            Picasso.get().load(documentModel.uri).placeholder(R.drawable.ic_document).into(imagePreview)

            deleteImageView.setOnClickListener {
                documentActionListener.onDeleteClick(documentModel)
            }
            captionEditTextView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    documentModel.caption = s.toString()
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            sendBtn.setOnClickListener {
                documentActionListener.onDocumentSubmit(documentModel)
            }

            itemView.setOnClickListener {
                val context = itemView.context
                val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_image_preview, null)
                val dialogImageView: ImageView = dialogView.findViewById(R.id.dialog_image_view)

                Picasso.get().load(documentModel.uri).into(dialogImageView)

                val alertDialog = AlertDialog.Builder(context)
                    .setView(dialogView)
                    .setPositiveButton("Close") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()

                alertDialog.show()
            }
        }
    }


    private class PdfViewHolder(
        itemView: View,
        private val documentActionListener: DocumentActionListener
    ) : RecyclerView.ViewHolder(itemView) {
        val imagePreview: ImageView = itemView.findViewById(R.id.image_preview)
        val fileNameTextView: TextView = itemView.findViewById(R.id.file_name_textview)
        val deleteImageView: ImageView = itemView.findViewById(R.id.delete_imageview)
        val captionEditTextView: EditText = itemView.findViewById(R.id.caption_textview)
        val sendBtn: ImageView = itemView.findViewById(R.id.send_icon_btn)

        fun bind(documentModel: DocumentModel) {
            fileNameTextView.text = documentModel.fileName
            Picasso.get().load(documentModel.uri).placeholder(R.drawable.google).into(imagePreview)
            deleteImageView.setOnClickListener {
                documentActionListener.onDeleteClick(documentModel)
                Log.d(TAG, "bind: delete click")
            }
            sendBtn.setOnClickListener {
                documentActionListener.onDocumentSubmit(documentModel)
            }

        }
    }

    private class ExcelViewHolder(
        itemView: View,
        private val documentActionListener: DocumentActionListener
    ) : RecyclerView.ViewHolder(itemView) {
        val imagePreview: ImageView = itemView.findViewById(R.id.image_preview)
        val fileNameTextView: TextView = itemView.findViewById(R.id.file_name_textview)
        val deleteImageView: ImageView = itemView.findViewById(R.id.delete_imageview)
        val captionEditTextView: EditText = itemView.findViewById(R.id.caption_textview)
        val sendBtn: ImageView = itemView.findViewById(R.id.send_icon_btn)
        fun bind(documentModel: DocumentModel) {
            fileNameTextView.text = documentModel.fileName
            Picasso.get().load(documentModel.uri).placeholder(R.drawable.facebook).into(imagePreview)
            deleteImageView.setOnClickListener {
                documentActionListener.onDeleteClick(documentModel)
                Log.d(TAG, "bind: delete click")
            }
            sendBtn.setOnClickListener {
                documentActionListener.onDocumentSubmit(documentModel)
            }
        }
    }
}
