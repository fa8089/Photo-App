package com.lahsuak.smartgallary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.lahsuak.smartgallary.databinding.FragmentViewBinding
import java.io.File

class ViewFragment : Fragment(R.layout.fragment_view) {
    private val args : ViewFragmentArgs by navArgs()
    private lateinit var binding:FragmentViewBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentViewBinding.bind(view)
        val file = File(args.path)
        Glide.with(requireContext()).load(file).into(binding.imgView)
    }
}