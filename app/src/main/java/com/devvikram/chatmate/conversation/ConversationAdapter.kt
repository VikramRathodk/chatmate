
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.devvikram.chatmate.R
import com.devvikram.chatmate.conversation.model.Conversation
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ConversationAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val sharedPreference = SharedPreference(context)
    private var conversationList = mutableListOf<Conversation>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun submitList(list: List<Conversation>) {
        conversationList.clear()
        conversationList.addAll(list.sortedBy { it.timestamp })
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            SENDER_VIEW_TYPE -> {
                val view = inflater.inflate(R.layout.item_sender_layout, parent, false)
                SenderViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.item_receiver_layout, parent, false)
                ReceiverViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int = conversationList.size

    override fun getItemViewType(position: Int): Int {
        val conversation = conversationList[position]
        return if (conversation.senderId == sharedPreference.getUid()) {
            SENDER_VIEW_TYPE
        } else {
            RECEIVER_VIEW_TYPE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val conversation = conversationList[position]
        when (holder.itemViewType) {
            SENDER_VIEW_TYPE -> {
                (holder as? SenderViewHolder)?.bind(conversation)
            }
            RECEIVER_VIEW_TYPE -> {
                (holder as? ReceiverViewHolder)?.bind(conversation)
            }
        }
    }

    inner class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textMessageTextview: TextView = itemView.findViewById(R.id.message_textview)
        private val textMessageTime: TextView = itemView.findViewById(R.id.text_time_textview)
        private val filePreviewImageView: ImageView = itemView.findViewById(R.id.file_preview_imageview)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
        private val progressPercentageTextView: TextView = itemView.findViewById(R.id.progress_percentage_textview)
        private val deliveredTextView: ImageView = itemView.findViewById(R.id.delivered)

        fun bind(conversation: Conversation) {
            textMessageTextview.text = conversation.message
            textMessageTime.text = formatTime(conversation.timestamp)
            val params = textMessageTextview.layoutParams as ConstraintLayout.LayoutParams

            if (conversation.fileUrl.isNotEmpty()) {
                filePreviewImageView.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
                progressPercentageTextView.visibility = View.VISIBLE
                textMessageTextview.visibility = View.GONE
                textMessageTime.visibility = View.GONE
                deliveredTextView.visibility = View.GONE
                params.width = 0
                params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                textMessageTextview.layoutParams = params

                coroutineScope.launch {
                    try {
                        val file = withContext(Dispatchers.IO) {
                            downloadFile(conversation.fileUrl) { progress ->
                                    progressBar.progress = progress
                                    progressPercentageTextView.text = "$progress%"
                            }
                        }
                        if (file.exists()) {
                            Log.d(TAG, "File downloaded successfully: ${file.absolutePath}, size: ${file.length()} bytes")
                        } else {
                            Log.e(TAG, "File does not exist: ${file.absolutePath}")
                        }
                        progressBar.visibility = View.GONE
                        textMessageTextview.visibility = View.VISIBLE
                        textMessageTime.visibility = View.VISIBLE
                        progressPercentageTextView.visibility = View.GONE
                        filePreviewImageView.visibility = View.VISIBLE
                        deliveredTextView.visibility = View.VISIBLE
                        Picasso.get().load(file).placeholder(R.drawable.ic_document).into(filePreviewImageView)
                    } catch (e: Exception) {
                        Log.d(TAG, "Error downloading file: ${e.message}")
                    }
                }
            } else {
                filePreviewImageView.visibility = View.GONE
                progressBar.visibility = View.GONE
                progressPercentageTextView.visibility = View.GONE
                params.width = ConstraintLayout.LayoutParams.MATCH_PARENT
                params.endToEnd = ConstraintLayout.LayoutParams.UNSET
                textMessageTextview.layoutParams = params
            }
        }

        private suspend fun downloadFile(url: String, onProgress: (Int) -> Unit): File {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val file = File(context.cacheDir, "downloaded_file")
                val totalBytes = response.body?.contentLength() ?: 0L
                var downloadedBytes = 0L

                response.body?.byteStream()?.use { input ->
                    file.outputStream().use { output ->
                        val buffer = ByteArray(1024)
                        var bytesRead: Int
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            downloadedBytes += bytesRead
                            val progress = (100 * downloadedBytes / totalBytes).toInt()
                            withContext(Dispatchers.Main) {
                                onProgress(progress)
                            }
                        }
                    }
                }
                return file
            } else {
                throw IOException("Failed to download file")
            }
        }



        private fun formatTime(timestamp: Long): String {
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            return dateFormat.format(Date(timestamp))
        }
    }

    inner class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textMessageTextview: TextView = itemView.findViewById(R.id.message_textview)
        private val textMessageTime: TextView = itemView.findViewById(R.id.text_time_textview)
        private val filePreviewImageView: ImageView = itemView.findViewById(R.id.file_preview_imageview)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
        private val progressPercentageTextView: TextView = itemView.findViewById(R.id.progress_percentage_textview)
        fun bind(conversation: Conversation) {
            textMessageTextview.text = conversation.message
            textMessageTime.text = formatTime(conversation.timestamp)
            val params = textMessageTextview.layoutParams as ConstraintLayout.LayoutParams

            if (conversation.fileUrl.isNotEmpty()) {
                filePreviewImageView.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
                progressPercentageTextView.visibility = View.VISIBLE
                textMessageTextview.visibility = View.GONE
                textMessageTime.visibility = View.GONE
                params.width = 0
                params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                textMessageTextview.layoutParams = params

                coroutineScope.launch {
                    try {
                        val file = withContext(Dispatchers.IO) {
                            downloadFile(conversation.fileUrl) { progress ->
                                progressBar.progress = progress
                                progressPercentageTextView.text = "$progress%"
                            }
                        }
                        if (file.exists()) {
                            Log.d(TAG, "File downloaded successfully: ${file.absolutePath}, size: ${file.length()} bytes")
                        } else {
                            Log.e(TAG, "File does not exist: ${file.absolutePath}")
                        }
                        progressBar.visibility = View.GONE
                        progressPercentageTextView.visibility = View.GONE
                        textMessageTextview.visibility = View.VISIBLE
                        textMessageTime.visibility = View.VISIBLE
                        filePreviewImageView.visibility = View.VISIBLE
                        Picasso.get().load(file).placeholder(R.drawable.ic_document).into(filePreviewImageView)
                    } catch (e: Exception) {
                        Log.d(TAG, "Error downloading file: ${e.message}")
                    }
                }
            } else {
                filePreviewImageView.visibility = View.GONE
                progressBar.visibility = View.GONE
                progressPercentageTextView.visibility = View.GONE
                params.width = ConstraintLayout.LayoutParams.MATCH_PARENT
                params.endToEnd = ConstraintLayout.LayoutParams.UNSET
                textMessageTextview.layoutParams = params
            }
        }

        private suspend fun downloadFile(url: String, onProgress: (Int) -> Unit): File {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val file = File(context.cacheDir, "downloaded_file")
                val totalBytes = response.body?.contentLength() ?: 0L
                var downloadedBytes = 0L

                response.body?.byteStream()?.use { input ->
                    file.outputStream().use { output ->
                        val buffer = ByteArray(1024)
                        var bytesRead: Int
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            downloadedBytes += bytesRead
                            val progress = (100 * downloadedBytes / totalBytes).toInt()
                            withContext(Dispatchers.Main) {
                                onProgress(progress)
                            }
                        }
                    }
                }
                return file
            } else {
                throw IOException("Failed to download file")
            }
        }



        private fun formatTime(timestamp: Long): String {
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            return dateFormat.format(Date(timestamp))
        }
    }

    companion object {
        private const val SENDER_VIEW_TYPE = 1
        private const val RECEIVER_VIEW_TYPE = 2
    }
}
