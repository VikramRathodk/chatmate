package com.devvikram.chatmate.util
import android.app.Activity
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.devvikram.chatmate.R
import com.devvikram.chatmate.dialogs.PreviewImageDialog
import com.squareup.picasso.Picasso

class CapturedImagesAdapter(private val images: MutableList<Uri> ,private val activity: Activity) : RecyclerView.Adapter<CapturedImagesAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val deleteBtn : ImageView = itemView.findViewById(R.id.remove_item_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Picasso.get().load(images[position]).placeholder(R.drawable.ic_document).into(holder.imageView)
        holder.deleteBtn.setOnClickListener{
            images.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, images.size)
        }
        holder.itemView.setOnClickListener {
            val previewDialog = PreviewImageDialog(activity,"imagename",images[position])
            previewDialog.show()
        }
    }

    override fun getItemCount(): Int = images.size
}
