package com.devvikram.chatmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.devvikram.chatmate.models.Users
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(private val list: List<Users>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    private var listener: onItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item_layout, parent, false)
        return UserViewHolder(view)

    }
    fun setOnItemClickListener(listener: onItemClickListener){
        this.listener = listener
    }
    interface onItemClickListener{
        fun onItemSelected (users: Users,position: Int)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = list[position]

        holder.name.text = list[position].username
        holder.lastMessageTextView.text = list[position].email
        holder.itemView.setOnClickListener{
            listener?.onItemSelected(user,position)
        }
    }
     class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val name = view.findViewById<TextView>(R.id.username_textview)!!
        val lastMessageTextView = view.findViewById<TextView>(R.id.last_message_textview)!!
         val profileImageView = view.findViewById<CircleImageView>(R.id.user_profile_image)!!


    }
}