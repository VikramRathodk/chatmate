
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.devvikram.chatmate.R
import com.devvikram.chatmate.conversation.MessageViewModel
import com.devvikram.chatmate.conversation.model.Conversation
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ConversationAdapter(
    private val context: Context,
    private val messageViewModel: MessageViewModel
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val sharedPreference = SharedPreference(context)
    private var conversationList = mutableListOf<Conversation>()

    fun submitList(list: List<Conversation>) {
        conversationList.clear()
        conversationList.addAll(list.sortedBy { it.timestamp })
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            SENDER_TEXT_VIEW_TYPE -> {
                val view = inflater.inflate(R.layout.item_sender_text, parent, false)
                SenderTextViewHolder(view)
            }

            SENDER_IMAGE_VIEW_TYPE -> {
                val view = inflater.inflate(R.layout.item_sender_image, parent, false)
                SenderImageViewHolder(view)
            }

            SENDER_PDF_VIEW_TYPE -> {
                val view = inflater.inflate(R.layout.item_sender_pdf, parent, false)
                SenderPdfViewHolder(view)
            }

            SENDER_VIDEO_VIEW_TYPE -> {
                val view = inflater.inflate(R.layout.item_sender_video, parent, false)
                SenderVideoViewHolder(view)
            }

            RECEIVER_TEXT_VIEW_TYPE -> {
                val view = inflater.inflate(R.layout.item_receiver_text, parent, false)
                ReceiverTextViewHolder(view)
            }

            RECEIVER_IMAGE_VIEW_TYPE -> {
                val view = inflater.inflate(R.layout.item_receiver_image, parent, false)
                ReceiverImageViewHolder(view)
            }

            RECEIVER_PDF_VIEW_TYPE -> {
                val view = inflater.inflate(R.layout.item_receiver_pdf, parent, false)
                ReceiverPdfViewHolder(view)
            }

            RECEIVER_VIDEO_VIEW_TYPE -> {
                val view = inflater.inflate(R.layout.item_receiver_video, parent, false)
                ReceiverVideoViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = conversationList.size

    override fun getItemViewType(position: Int): Int {
        val conversation = conversationList[position]
        return when {
            conversation.senderId == sharedPreference.getUid() -> {
                when {
                    conversation.fileUrl.isNotEmpty() -> {
                        when {
                            conversation.messageType.contains("image") -> SENDER_IMAGE_VIEW_TYPE
                            conversation.messageType.contains("pdf") -> SENDER_PDF_VIEW_TYPE
                            conversation.messageType.contains("video") -> SENDER_VIDEO_VIEW_TYPE
                            else -> SENDER_TEXT_VIEW_TYPE
                        }
                    }

                    else -> SENDER_TEXT_VIEW_TYPE
                }
            }

            else -> {
                when {
                    conversation.fileUrl.isNotEmpty() -> {
                        when {
                            conversation.messageType.contains("image") -> RECEIVER_IMAGE_VIEW_TYPE
                            conversation.messageType.contains("pdf") -> RECEIVER_PDF_VIEW_TYPE
                            conversation.messageType.contains("video") -> RECEIVER_VIDEO_VIEW_TYPE
                            else -> RECEIVER_TEXT_VIEW_TYPE
                        }
                    }

                    else -> RECEIVER_TEXT_VIEW_TYPE
                }
            }
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val conversation = conversationList[position]
        when (holder.itemViewType) {
            SENDER_TEXT_VIEW_TYPE -> (holder as SenderTextViewHolder).bind(conversation)
            SENDER_IMAGE_VIEW_TYPE -> (holder as SenderImageViewHolder).bind(conversation)
            SENDER_PDF_VIEW_TYPE -> (holder as SenderPdfViewHolder).bind(conversation)
            SENDER_VIDEO_VIEW_TYPE -> (holder as SenderVideoViewHolder).bind(conversation)
            RECEIVER_TEXT_VIEW_TYPE -> (holder as ReceiverTextViewHolder).bind(conversation)
            RECEIVER_IMAGE_VIEW_TYPE -> (holder as ReceiverImageViewHolder).bind(conversation)
            RECEIVER_PDF_VIEW_TYPE -> (holder as ReceiverPdfViewHolder).bind(conversation)
            RECEIVER_VIDEO_VIEW_TYPE -> (holder as ReceiverVideoViewHolder).bind(conversation)
        }
    }
    inner class SenderTextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textMessageTextview: TextView = itemView.findViewById(R.id.message_textview)
        private val textMessageTime: TextView = itemView.findViewById(R.id.text_time_textview)

        fun bind(conversation: Conversation) {
            textMessageTextview.text = conversation.message
            textMessageTime.text = formatTime(conversation.timestamp)
        }

        private fun formatTime(timestamp: Long): String {
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            return dateFormat.format(Date(timestamp))
        }

    }

    inner class SenderImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textMessageTextview: TextView = itemView.findViewById(R.id.message_textview)
        private val textMessageTime: TextView = itemView.findViewById(R.id.text_time_textview)
        private val filePreviewImageView: ImageView = itemView.findViewById(R.id.file_preview_imageview)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
        private val progressPercentageTextView: TextView = itemView.findViewById(R.id.progress_percentage_textview)

        fun bind(conversation: Conversation) {
            textMessageTextview.text = conversation.message
            textMessageTime.text = formatTime(conversation.timestamp)

            filePreviewImageView.setImageDrawable(null)
            progressBar.visibility = View.GONE
            progressPercentageTextView.visibility = View.GONE

            if (conversation.fileUrl.isNotEmpty()) {
                progressBar.visibility = View.VISIBLE
                progressPercentageTextView.visibility = View.VISIBLE

                messageViewModel.downloadAndSetFileUri(conversation, context, { progress ->
                    progressBar.progress = progress
                    progressPercentageTextView.text = "$progress%"
                }, { uri ->
                    progressBar.visibility = View.GONE
                    progressPercentageTextView.visibility = View.GONE

                    if (uri != null) {
                        Picasso.get().load(uri)
                            .placeholder(R.drawable.icon_pdf)
                            .error(R.drawable.image_item_background)
                            .into(filePreviewImageView)
                    } else {
                        filePreviewImageView.setImageResource(R.drawable.image_item_background)
                        Toast.makeText(context, "Failed to download file", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                filePreviewImageView.visibility = View.GONE
            }
        }


        private fun formatTime(timestamp: Long): String {
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            return dateFormat.format(Date(timestamp))
        }

    }
    inner class SenderPdfViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        private val textMessageTextview: TextView = itemView.findViewById(R.id.message_textview)
        private val textMessageTime: TextView = itemView.findViewById(R.id.text_time_textview)
        private val filePreviewImageView: ImageView = itemView.findViewById(R.id.file_icon_imageview)
        private val fileName = itemView.findViewById<TextView>(R.id.pdf_name_textview)

        fun bind(conversation: Conversation) {
            textMessageTextview.text = conversation.message
            textMessageTime.text = formatTime(conversation.timestamp)

            if (conversation.documentModel != null) {
                filePreviewImageView.setImageResource(R.drawable.icon_pdf)
                fileName.text = conversation.documentModel.fileName

                filePreviewImageView.setOnClickListener {
                    if (conversation.fileUrl.isNotEmpty()) {
                        messageViewModel.openPdf(conversation, context)
                    } else {
                        Toast.makeText(context, "No file found", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                filePreviewImageView.visibility = View.GONE
                fileName.text = ""
            }

            Log.d(TAG, "bind: ${conversation.fileUrl}")
        }

        private fun formatTime(timestamp: Long): String {
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            return dateFormat.format(Date(timestamp))
        }
    }
    inner class SenderVideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textMessageTextview: TextView = itemView.findViewById(R.id.message_textview)
        private val textMessageTime: TextView = itemView.findViewById(R.id.text_time_textview)
        private val videoView: VideoView = itemView.findViewById(R.id.player_view)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.download_progress_bar)

        fun bind(conversation: Conversation) {
            textMessageTextview.text = conversation.message
            textMessageTime.text = formatTime(conversation.timestamp)

            progressBar.visibility = View.VISIBLE
            val mediaController = MediaController(context)
            mediaController.setAnchorView(videoView)
            videoView.setMediaController(mediaController)


            videoView.setVideoPath(conversation.fileUrl)
            videoView.setOnPreparedListener {
                progressBar.visibility = View.GONE
                it.start()
            }
            videoView.setOnErrorListener { mp, what, extra ->
                progressBar.visibility = View.GONE
                true
            }
        }

        private fun formatTime(timestamp: Long): String {
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            return dateFormat.format(Date(timestamp))
        }
    }
    inner class ReceiverTextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val textMessageTextview: TextView = itemView.findViewById(R.id.message_textview)
        private val textMessageTime: TextView = itemView.findViewById(R.id.text_time_textview)
        fun bind(conversation: Conversation) {
            textMessageTextview.text = conversation.message
            textMessageTime.text = formatTime(conversation.timestamp)
        }
        private fun formatTime(timestamp: Long): String {
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            return dateFormat.format(Date(timestamp))
        }

    }
    inner class ReceiverImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val textMessageTextview: TextView = itemView.findViewById(R.id.message_textview)
        private val textMessageTime: TextView = itemView.findViewById(R.id.text_time_textview)
        private val filePreviewImageView: ImageView = itemView.findViewById(R.id.file_preview_imageview)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
        private val progressPercentageTextView: TextView = itemView.findViewById(R.id.progress_percentage_textview)

        fun bind(conversation: Conversation) {
            textMessageTextview.text = conversation.message
            textMessageTime.text = formatTime(conversation.timestamp)
            if (conversation.fileUrl.isNotEmpty()) {
                messageViewModel.downloadAndSetFileUri(conversation, context, { progress ->
                    progressBar.visibility = View.VISIBLE
                    progressPercentageTextView.visibility = View.VISIBLE
                    filePreviewImageView.visibility = View.GONE
                    progressBar.progress = progress
                    progressPercentageTextView.text = "$progress%"
                }, { uri ->
                    progressBar.visibility = View.GONE
                    progressPercentageTextView.visibility = View.GONE
                    if (uri != null) {
                        filePreviewImageView.visibility = View.VISIBLE
                        Picasso.get().load(uri).placeholder(R.drawable.ic_document)
                            .into(filePreviewImageView)
                    } else {
                        Toast.makeText(context, "Failed to download file", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
            }

        }
        private fun formatTime(timestamp: Long): String {
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            return dateFormat.format(Date(timestamp))}

    }
    inner class ReceiverPdfViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val textMessageTextview: TextView = itemView.findViewById(R.id.message_textview)
        private val textMessageTime: TextView = itemView.findViewById(R.id.text_time_textview)
        private val filePreviewImageView: ImageView = itemView.findViewById(R.id.file_icon_imageview)

        fun bind(conversation: Conversation) {
            if(conversation.message.isNotEmpty()){
                textMessageTextview.text = conversation.message
                textMessageTextview.visibility = View.VISIBLE
            }else{
                textMessageTextview.visibility = View.GONE
            }
            textMessageTime.text = formatTime(conversation.timestamp)
            filePreviewImageView.setImageResource(R.drawable.icon_pdf)
            filePreviewImageView.setOnClickListener {
                if (conversation.fileUrl.isNotEmpty()) {
                    messageViewModel.openPdf(conversation,context)
                }else{
                    Toast.makeText(context, "No file found", Toast.LENGTH_SHORT).show()
                }

            }
        }

        private fun formatTime(timestamp: Long): String {
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            return dateFormat.format(Date(timestamp))

        }
    }
    inner class ReceiverVideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val textMessageTime: TextView = itemView.findViewById(R.id.text_time_textview)
        private val videoView: VideoView = itemView.findViewById(R.id.player_view)

        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)

        fun bind(conversation: Conversation) {
            textMessageTime.text = formatTime(conversation.timestamp)
            Log.d(TAG, "bind: ${conversation.fileUrl}")

            progressBar.visibility = View.VISIBLE
            val mediaController = MediaController(context)
            mediaController.setAnchorView(videoView)
            videoView.setMediaController(mediaController)


            videoView.setVideoPath(conversation.fileUrl)
            videoView.setOnPreparedListener {
                progressBar.visibility = View.GONE
                it.start()
            }
            videoView.setOnErrorListener { mp, what, extra ->
                progressBar.visibility = View.GONE
                true
            }
        }
        private fun formatTime(timestamp: Long): String {
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            return dateFormat.format(Date(timestamp))
        }
    }

    companion object {
        private const val SENDER_TEXT_VIEW_TYPE = 1
        private const val SENDER_IMAGE_VIEW_TYPE = 2
        private const val SENDER_PDF_VIEW_TYPE = 3
        private const val SENDER_VIDEO_VIEW_TYPE = 4
        private const val RECEIVER_TEXT_VIEW_TYPE = 5
        private const val RECEIVER_IMAGE_VIEW_TYPE = 6
        private const val RECEIVER_PDF_VIEW_TYPE = 7
        private const val RECEIVER_VIDEO_VIEW_TYPE = 8
    }
}
