package com.devvikram.chatmate.util

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.otaliastudios.cameraview.CameraView

class CameraActivityViewModel : ViewModel() {
    val capturedImages: MutableList<Bitmap> = mutableListOf()
    val camera: CameraView? = null
}
