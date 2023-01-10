package com.lahsuak.smartgallary.ui.adapter.viewholderr

import androidx.recyclerview.widget.RecyclerView
import com.lahsuak.smartgallary.databinding.DateItemBinding
import com.lahsuak.smartgallary.util.AppUtil

class DateViewHolder(private val binding: DateItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(date: Long) {
        binding.txtDate.text = AppUtil.getDateByMonthYear(date)
    }
}