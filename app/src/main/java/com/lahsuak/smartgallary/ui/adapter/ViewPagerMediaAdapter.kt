package com.lahsuak.smartgallary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.lahsuak.smartgallary.model.Image
import com.lahsuak.smartgallary.ui.adapter.viewholderr.ViewPagerMediaViewHolder
import com.lahsuak.smartgallary.databinding.ViewPagerItemBinding

class ViewPagerMediaAdapter :
    ListAdapter<Image, ViewPagerMediaViewHolder>(MediaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerMediaViewHolder {
        val binding =
            ViewPagerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewPagerMediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerMediaViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class MediaDiffCallback : DiffUtil.ItemCallback<Image>() {
        override fun areItemsTheSame(oldItem: Image, newItem: Image) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Image, newItem: Image) = oldItem == newItem
    }
}