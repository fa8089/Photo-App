package com.lahsuak.smartgallary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lahsuak.smartgallary.databinding.DateItemBinding
import com.lahsuak.smartgallary.ui.adapter.viewholderr.GalleryViewHolder
import com.lahsuak.smartgallary.databinding.ImageItemBinding
import com.lahsuak.smartgallary.model.ImageDate
import com.lahsuak.smartgallary.ui.adapter.viewholderr.DateViewHolder
import com.lahsuak.smartgallary.util.AppConstants

class GalleryAdapter(
    private val listener: GalleryListener
) : ListAdapter<ImageDate, ViewHolder>(ImageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        return if(viewType == AppConstants.ViewType.VIEW_TYPE_IMAGE) {
            val binding =
                ImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            GalleryViewHolder(binding, listener)
        } else {
            val binding =
                DateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            DateViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]
        if(getItemViewType(position) == AppConstants.ViewType.VIEW_TYPE_DATE) {
            (holder as DateViewHolder).bind(item.image.dateAdded)
        } else{
            (holder as GalleryViewHolder).bind(item.image)
        }
    }

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)
        return if (
            currentList[position].contentType ==
            AppConstants.ViewType.VIEW_TYPE_DATE
        ) {
            AppConstants.ViewType.VIEW_TYPE_DATE
        } else {
            AppConstants.ViewType.VIEW_TYPE_IMAGE
        }
    }

    class ImageDiffCallback : DiffUtil.ItemCallback<ImageDate>() {
        override fun areItemsTheSame(oldItem: ImageDate, newItem: ImageDate) =
            oldItem.image.id == newItem.image.id

        override fun areContentsTheSame(oldItem: ImageDate, newItem: ImageDate) = oldItem == newItem
    }

    interface GalleryListener {
        fun onPhotoClick(path: String, date: String, position: Int)
    }
}