package com.devvikram.chatmate.adapters

import SharedPreference
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.devvikram.chatmate.R
import com.devvikram.chatmate.models.Conversation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ConversationAdapter(private val context: Context) :
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

        fun bind(conversation: Conversation) {
            textMessageTextview.text = conversation.message
            textMessageTime.text = formatTime(conversation.timestamp)
        }

        private fun formatTime(timestamp: Long): String {
            val dateFormat = SimpleDateFormat("HH:mm a", Locale.getDefault())
            return dateFormat.format(Date(timestamp))
        }
    }

    inner class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textMessageTextview: TextView = itemView.findViewById(R.id.message_textview)
        private val textMessageTime: TextView = itemView.findViewById(R.id.text_time_textview)

        fun bind(conversation: Conversation) {
            textMessageTextview.text = conversation.message
            textMessageTime.text = formatTime(conversation.timestamp)
        }

        private fun formatTime(timestamp: Long): String {
            val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            return dateFormat.format(Date(timestamp))
        }
    }

    companion object {
        private const val SENDER_VIEW_TYPE = 1
        private const val RECEIVER_VIEW_TYPE = 2
    }
}
