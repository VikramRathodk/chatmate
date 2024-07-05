package com.devvikram.chatmate.util

import android.app.Activity
import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.devvikram.chatmate.R
import com.devvikram.chatmate.models.DocumentModel
import com.squareup.picasso.Picasso

class DocumentAdapter(
    val activity:Activity,
    val documentList: ArrayList<DocumentModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val IMAGE_VIEW_CONSTANT = 1;
        private const val PDF_VIEW_CONSTANT = 2;
        private const val EXCEL_VIEW_CONSTANT = 3

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            DocumentAdapter.IMAGE_VIEW_CONSTANT -> {
                val view = inflater.inflate(R.layout.image_preview_layout, parent, false)
                ImageViewHolder(view)
            }
            DocumentAdapter.PDF_VIEW_CONSTANT->{
                val view = inflater.inflate(R.layout.pdf_preview_layout,parent,false)
                PdfViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.excel_preview_layout, parent, false)
                ExcelViewHolder(view)
            }
        }
    }


    override fun getItemCount(): Int {
       return documentList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            val documentModel = documentList[position]
            when (holder.itemViewType) {
                IMAGE_VIEW_CONSTANT -> {
                    val imageViewHolder = holder as ImageViewHolder
                    Log.d(TAG, "onBindViewHolder: ${documentModel.uri}")
                    imageViewHolder.bind(documentModel,documentList, object : ImageViewHolder.OnDeleteClickListener {
                        override fun onDeleteClick(documentModel: DocumentModel) {
                            documentList.remove(documentModel)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, documentList.size)
                        }
                    })
                }
                PDF_VIEW_CONSTANT -> {
                    val pdfViewHolder = holder as PdfViewHolder
                    pdfViewHolder.bind(documentModel)
                }
                EXCEL_VIEW_CONSTANT -> {
                    val excelViewHolder = holder as ExcelViewHolder
                    excelViewHolder.bind(documentModel)
                }

        }

    }
    override fun getItemViewType(position: Int): Int {
        val documentModel = documentList[position]
        val documentType = documentModel.fileType
        Log.d(TAG, "getItemViewType: $documentType")
        return when(documentType){
            "pdf"->{
                PDF_VIEW_CONSTANT
            }
            "image"->{
                IMAGE_VIEW_CONSTANT
            }
            "excel"->{
                EXCEL_VIEW_CONSTANT
            }else-> super.getItemViewType(position)
        }
    }



    private class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imagePreview: ImageView = itemView.findViewById(R.id.image_preview)
        private val fileNameTextView: TextView = itemView.findViewById(R.id.file_name_textview)
        private val deleteImageView: ImageView = itemView.findViewById(R.id.delete_imageview)

        fun bind(documentModel: DocumentModel, documentList: ArrayList<DocumentModel>, onDeleteClickListener: OnDeleteClickListener) {
            fileNameTextView.text = documentModel.fileName
            Picasso.get().load(documentModel.uri).placeholder(R.drawable.ic_document).into(imagePreview)

            deleteImageView.setOnClickListener {
                onDeleteClickListener.onDeleteClick(documentModel)
            }
        }

        interface OnDeleteClickListener {
            fun onDeleteClick(documentModel: DocumentModel)
        }
    }

    private  class PdfViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imagePreview: ImageView = itemView.findViewById(R.id.image_preview)
        val fileNameTextView: TextView = itemView.findViewById(R.id.file_name_textview)
        val deleteImageView: ImageView = itemView.findViewById(R.id.delete_imageview)
        fun bind(documentModel: DocumentModel) {
            fileNameTextView.text = documentModel.fileName
            Picasso.get().load(documentModel.uri).placeholder(R.drawable.google).into(imagePreview)
            deleteImageView.setOnClickListener{
                Log.d(TAG, "bind: delete click")
            }
        }


    }
    private class ExcelViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imagePreview: ImageView = itemView.findViewById(R.id.image_preview)
        val fileNameTextView: TextView = itemView.findViewById(R.id.file_name_textview)
        val deleteImageView: ImageView = itemView.findViewById(R.id.delete_imageview)
        fun bind(documentModel: DocumentModel) {
            fileNameTextView.text = documentModel.fileName
            Picasso.get().load(documentModel.uri).placeholder(R.drawable.facebook).into(imagePreview)
            deleteImageView.setOnClickListener{
                Log.d(TAG, "bind: delete click")
            }
        }
    }

}