package com.devvikram.chatmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.devvikram.chatmate.models.Users

class UserAdapter(private val list: MutableList<Users>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    private var selectedPosition = -1
    private var listener: onItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item_layout, parent, false)
        return UserViewHolder(view)

    }
    fun setOnItemClickListener(listener: onItemClickListener){
        this.listener = listener
    }
    interface onItemClickListener{
        fun onItemSelected (position: Int)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        holder.name.text = list[position].username

//        if (selectedPosition == position){
//            holder.itemView.setBackgroundColor(Color.GREEN)
//        }else{
//            holder.itemView.setBackgroundColor(Color.WHITE)
//        }

        holder.itemView.setOnClickListener{
//            selectedPosition = position
            listener?.onItemSelected(position)
            notifyDataSetChanged()
        }
    }
     class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val name = view.findViewById<TextView>(R.id.name)!!

    }
}