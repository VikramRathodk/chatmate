package com.devvikram.chatmate.util

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.devvikram.chatmate.databinding.ActivityCameraBinding
import com.google.android.material.snackbar.Snackbar
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.Mode
import com.otaliastudios.cameraview.controls.PictureFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.coroutines.CoroutineContext

class CameraActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var binding: ActivityCameraBinding
    private val capturedImageUris = mutableListOf<Uri>()
    private lateinit var adapter: CapturedImagesAdapter

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        job = Job()
        setupViews()
    }

    private fun setupViews() {
        adapter = CapturedImagesAdapter(capturedImageUris, this)
        binding.capturedImageRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CameraActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = this@CameraActivity.adapter
        }

        binding.camera.apply {
            setLifecycleOwner(this@CameraActivity)
            mode = Mode.PICTURE
            pictureFormat = PictureFormat.JPEG
            addCameraListener(object : CameraListener() {
                override fun onPictureTaken(result: PictureResult) {
                    result.toBitmap { bitmap ->
                        bitmap?.let {
                            launch(Dispatchers.Default) {
                                try {
                                    val imageUri = saveBitmapToFile(it)
                                    withContext(Dispatchers.Main) {
                                        capturedImageUris.add(imageUri)
                                        adapter.notifyItemInserted(capturedImageUris.size - 1)
                                    }
                                } catch (e: IOException) {
                                    showMessageSnackBar("Failed to save image: ${e.message}")
                                } catch (e: Exception) {
                                    showMessageSnackBar("Failed to capture image: ${e.message}")
                                }
                            }
                        }
                    }
                }
            })
        }

        binding.captureButton.setOnClickListener {
            binding.camera.takePicture()
        }
        binding.doneCaptureButton.setOnClickListener {
            val imageUris = ArrayList(capturedImageUris.map { it.toString() })
            val intent = Intent()
            intent.putStringArrayListExtra("captured_images_list", imageUris)
            Log.d(TAG, "setupViews: $imageUris")
            setResult(RESULT_OK, intent)
            finish()
        }

    }

    private fun showMessageSnackBar(message: String) {
        Log.e("CameraActivity", message)
        runOnUiThread {
            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private suspend fun saveBitmapToFile(bitmap: Bitmap): Uri = withContext(Dispatchers.IO) {
        val filesDir = applicationContext.filesDir
        val imageFile = File(filesDir, "captured_image_${System.currentTimeMillis()}.png")

        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 1024, 1024, true)

        val fos = FileOutputStream(imageFile)
        resizedBitmap.compress(Bitmap.CompressFormat.PNG, 90, fos)
        fos.flush()
        fos.close()

        resizedBitmap.recycle()

        // Return URI instead of File
        Uri.fromFile(imageFile)
    }

}
