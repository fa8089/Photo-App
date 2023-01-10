package com.lahsuak.smartgallary.ui.adapter.viewholderr

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.lahsuak.smartgallary.model.Image
import com.lahsuak.smartgallary.R
import com.lahsuak.smartgallary.databinding.ViewPagerItemBinding

class ViewPagerMediaViewHolder(private val binding: ViewPagerItemBinding)
    : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Image) {
        Glide.with(binding.root.context).load(item.path).error(R.drawable.ic_camera)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.imgPhoto)
    }
}