package com.lahsuak.smartgallary.ui.adapter.viewholderr

import android.app.Activity
import android.net.Uri
import android.os.Build
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.lahsuak.smartgallary.ui.adapter.GalleryAdapter
import com.lahsuak.smartgallary.databinding.ImageItemBinding
import com.lahsuak.smartgallary.model.Image
import com.lahsuak.smartgallary.util.AppUtil
import java.io.File

class GalleryViewHolder(
    private val binding: ImageItemBinding,
    private val listener: GalleryAdapter.GalleryListener
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(image: Image) {
        val context = binding.root.context
        Thread {
            val path: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                AppUtil.getRealPath(Uri.parse(image.path), context)!!
            } else image.path
            val file = File(path)
            if (file.exists()) {
                (context as Activity).runOnUiThread {
                    Glide.with(context).load(path)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(binding.image)
                }
            }
        }.start()
        binding.root.setOnClickListener {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val path1: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    image.path
                    AppUtil.getRealPath(Uri.parse(image.path), context)!!
                } else
                    image.path
                listener.onPhotoClick(path1, AppUtil.getDate(image.dateAdded), position)
            }
        }
    }
}
