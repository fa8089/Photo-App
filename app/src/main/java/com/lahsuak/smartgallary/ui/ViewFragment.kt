package com.lahsuak.smartgallary.ui

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.*
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.lahsuak.smartgallary.model.Image
import com.lahsuak.smartgallary.R
import com.lahsuak.smartgallary.ui.adapter.ViewPagerMediaAdapter
import com.lahsuak.smartgallary.databinding.FragmentViewBinding

class ViewFragment : Fragment(R.layout.fragment_view) {
    private val args: ViewFragmentArgs by navArgs()
    private lateinit var binding: FragmentViewBinding
    private lateinit var viewPagerAdapter: ViewPagerMediaAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentViewBinding.bind(view)
        setUpResultListener()
        viewPagerAdapter = ViewPagerMediaAdapter()
        binding.viewpager.adapter = viewPagerAdapter

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun setUpResultListener() {
        setFragmentResultListener(HomeFragment.IMAGE_BUNDLE_KEY) { _, bundle ->
            val images =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    bundle.getParcelableArrayList(HomeFragment.IMAGE_LIST_KEY, Image::class.java)
                } else {
                    bundle.getParcelableArrayList(HomeFragment.IMAGE_LIST_KEY)
                }.orEmpty()
            viewPagerAdapter.submitList(images)
            binding.viewpager.setCurrentItem(args.position, true)
        }
    }

    private fun hideSystemUI() {
        val windowInsetsController = WindowInsetsControllerCompat(
            requireActivity().window,
            requireActivity().window.decorView
        )
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    private fun showSystemUI() {
        val insetsController = WindowInsetsControllerCompat(
            requireActivity().window,
            requireActivity().window.decorView
        )
        insetsController.show(WindowInsetsCompat.Type.systemBars())
    }

    override fun onDestroy() {
        super.onDestroy()
        showSystemUI()
    }
}