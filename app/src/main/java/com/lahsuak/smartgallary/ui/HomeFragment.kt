package com.lahsuak.smartgallary.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.lahsuak.smartgallary.model.Image
import com.lahsuak.smartgallary.R
import com.lahsuak.smartgallary.ui.adapter.GalleryAdapter
import com.lahsuak.smartgallary.databinding.FragmentHomeBinding
import com.lahsuak.smartgallary.model.SortType
import com.lahsuak.smartgallary.ui.MainActivity.Companion.sortOrder
import com.lahsuak.smartgallary.ui.MainActivity.Companion.spanCount
import com.lahsuak.smartgallary.util.AppConstants
import com.lahsuak.smartgallary.util.AppConstants.SORT_DATE_ADDED
import com.lahsuak.smartgallary.util.AppConstants.SORT_DATE_MODIFIED
import com.lahsuak.smartgallary.util.AppConstants.SORT_SIZE
import com.lahsuak.smartgallary.util.AppUtil
import kotlinx.coroutines.flow.collectLatest
import me.zhanghai.android.fastscroll.FastScrollerBuilder

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var galleryAdapter: GalleryAdapter

    companion object {
        const val IMAGE_BUNDLE_KEY = "image_bundle_key"
        const val IMAGE_LIST_KEY = "image_list_key"
    }

    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        galleryAdapter = GalleryAdapter(object : GalleryAdapter.GalleryListener {
            override fun onPhotoClick(path: String, date: String, position: Int) {
                val action =
                    HomeFragmentDirections.actionHomeFragmentToViewFragment(
                        path,
                        date,
                        position
                    )
                findNavController().navigate(action)
            }
        })

        val gridLayoutManager = GridLayoutManager(requireContext(), spanCount)
        gridLayoutManager.spanSizeLookup = (object : GridLayoutManager.SpanSizeLookup() {

            override fun getSpanSize(position: Int): Int {
                return if (
                    galleryAdapter.currentList[position].contentType
                    == AppConstants.ViewType.VIEW_TYPE_DATE
                ) {
                    spanCount
                } else {
                    1
                }
            }
        })

        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = gridLayoutManager
            FastScrollerBuilder(this).build() // scrollbar
            adapter = galleryAdapter
        }
        checkPermission()
        setClickListeners()
        setImagesObserver()
    }

    private fun setImagesObserver() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.imagesDataFlow.collectLatest { images ->
                galleryAdapter.submitList(images)
                binding.progressBar.isVisible = false
                setImageListResult(images.map {
                    it.image
                })
                binding.txtTotal.text = getString(R.string.total_photos, images.size)
            }
        }
    }


    private fun setClickListeners() {
        binding.btnSort.setOnClickListener {
            setSort(it)
        }
        binding.btnView.setOnClickListener {
            setLayout(it)
        }
    }

    private fun setImageListResult(imageList: List<Image>) {
        setFragmentResult(
            IMAGE_BUNDLE_KEY,
            bundleOf(
                IMAGE_LIST_KEY to imageList,
            )
        )
    }

    private fun setLayout(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.view_menu, popupMenu.menu)
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { item ->
            spanCount = when (item.itemId) {
                R.id.grid1 -> {
                    2
                }
                R.id.grid2 -> {
                    3
                }
                R.id.grid3 -> {
                    4
                }
                R.id.grid4 -> {
                    8
                }
                else -> {
                    3
                }
            }
            val gridLayoutManager = GridLayoutManager(requireContext(), spanCount)
            gridLayoutManager.spanSizeLookup = (object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (
                        galleryAdapter.currentList[position].contentType ==
                        AppConstants.ViewType.VIEW_TYPE_DATE
                    ) {
                        spanCount
                    } else {
                        1
                    }
                }
            })
            binding.recyclerView.layoutManager = gridLayoutManager
            true
        }
    }

    private fun setSort(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.sort_menu, popupMenu.menu)
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { item ->
            sortOrder = when (item.itemId) {
                R.id.by_date_added -> {
                    SORT_DATE_ADDED
                }
                R.id.by_date_modified -> {
                    SORT_DATE_MODIFIED
                }
//                R.id.by_size -> {
//                    SORT_SIZE
//                }
                else -> {
                    SORT_DATE_MODIFIED
                }
            }
            val list = AppUtil.sortImages(galleryAdapter.currentList, SortType.from(sortOrder))
            galleryAdapter.submitList(null)
            galleryAdapter.submitList(list)
            true
        }
    }

    private val permissionsResultCallback = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        when (it) {
            true -> {
                viewModel.getImages(requireContext(), sortOrder)
            }
            false -> {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun checkPermission() {
        val readPermission = if (Build.VERSION_CODES.TIRAMISU <= Build.VERSION.SDK_INT) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        val permission = ContextCompat.checkSelfPermission(
            requireContext(),
            readPermission
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            permissionsResultCallback.launch(readPermission)
        } else {
            viewModel.getImages(requireContext(), sortOrder)
        }
    }
}