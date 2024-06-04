package com.devvikram.chatmate.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devvikram.chatmate.R
import com.devvikram.chatmate.models.Conversation

class ConversationAdapter(activity: Activity, private val conversationList: List<Conversation>) : RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    val SENDER_VIEW_TYPE = 1
    val RECEIVER_VIEW_TYPE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        if(viewType == SENDER_VIEW_TYPE){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sender_layout, parent, false)
            return ConversationViewHolder(view)
        }else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_receiver_layout, parent, false)
            return ConversationViewHolder(view)
        }
    }
    override fun getItemCount(): Int {
        return conversationList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (conversationList[position].sender == "Alice") {
            SENDER_VIEW_TYPE
        } else {
            RECEIVER_VIEW_TYPE
        }
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
    public class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

}