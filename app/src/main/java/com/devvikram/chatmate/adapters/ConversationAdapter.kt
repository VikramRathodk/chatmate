package com.devvikram.chatmate.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.devvikram.chatmate.R
import com.devvikram.chatmate.models.Conversation

class ConversationAdapter(activity: Activity, private val conversationList: List<Conversation>) :
    RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    private val SENDER_VIEW_TYPE = 1
    private val RECEIVER_VIEW_TYPE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        if (viewType == SENDER_VIEW_TYPE) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_sender_layout, parent, false)
            return ConversationViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_receiver_layout, parent, false)
            return ConversationViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return conversationList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (conversationList[position].senderId == "1") {
            SENDER_VIEW_TYPE
        } else {
            RECEIVER_VIEW_TYPE
        }
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val conversation = conversationList[position]
        holder.textMessageTextview.text = conversation.message
        holder.textMessageTime.text = conversation.timestamp
    }

    class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textMessageTextview: TextView = itemView.findViewById(R.id.message_textview)
        val textMessageTime: TextView = itemView.findViewById(R.id.text_time_textview)

    }

}