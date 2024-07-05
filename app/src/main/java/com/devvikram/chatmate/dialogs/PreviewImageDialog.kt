package com.devvikram.chatmate.dialogs

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import com.devvikram.chatmate.databinding.PreviewImageLayoutBinding
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PreviewImageDialog(context: Context, private val imageName: String, private val uri: Uri) : AlertDialog(context) {
    private lateinit var binding: PreviewImageLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = PreviewImageLayoutBinding.inflate(layoutInflater)
        setView(binding.root)

//        val imageFile = bitmapToFile(bitmap)
        Picasso.get().load(uri).into(binding.imagePreview)
        binding.fileNameTextview.text = imageName

        binding.closeBtn.setOnClickListener {
            dismiss()
        }
        super.onCreate(savedInstanceState)

    }

    private fun bitmapToFile(bitmap: Bitmap): File {
        val filesDir = context.filesDir
        val imageFile = File(filesDir, "preview_image.png")
        try {
            val fos = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return imageFile
    }
}
