package com.lahsuak.smartgallary

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.GridLayout
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.lahsuak.smartgallary.databinding.FragmentHomeBinding
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding

    private var list = ArrayList<Image>()
    private lateinit var adapter: GalleryAdapter
    private var mStoragePermissionGranted = false

    companion object {
        var spanCount = 2
        var sortOrder = "by_date_added"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pref = requireContext().getSharedPreferences("GALLERY", Context.MODE_PRIVATE)
        mStoragePermissionGranted = pref.getBoolean("isGranted", false)
        spanCount = pref.getInt("spanCount", 2)
        sortOrder = pref.getString("sorting","by_date_added").toString()

        binding = FragmentHomeBinding.bind(view)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)
        adapter = GalleryAdapter(requireContext(), list, object : GalleryListener {
            override fun onPhotoClick(path: String, date: String) {
                val action = HomeFragmentDirections.actionHomeFragmentToViewFragment(path, date)
                findNavController().navigate(action)
            }
        })
        binding.recyclerView.adapter = adapter

        if (mStoragePermissionGranted) {
            list = getImages(sortOrder)
            adapter.updateList(list)
        } else
            permissionSetup()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.view_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.grid1 -> {
                spanCount = 2
                binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)
                adapter.notifyDataSetChanged()
            }
            R.id.grid2 -> {
                spanCount = 3
                binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)
                adapter.notifyDataSetChanged()
            }
            R.id.grid3 -> {
                spanCount = 4
                binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)
                adapter.notifyDataSetChanged()
            }
            R.id.grid4 -> {
                spanCount = 8
                binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)
                adapter.notifyDataSetChanged()
            }
            R.id.by_date_added -> {
                sortOrder = "by_date_added"
                list = getImages(sortOrder)
                adapter.updateList(list)
            }
            R.id.by_date_modified -> {
                sortOrder = "by_date_modified"
               list =getImages(sortOrder)
                adapter.updateList(list)
            }
            R.id.by_size -> {
                sortOrder = "by_size"
                list = getImages(sortOrder)
               adapter.updateList(list)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getImages(sortOrder: String): ArrayList<Image> {
        var order:String?=null
        val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        when(sortOrder){
            "by_date_added"-> order = MediaStore.Images.Media.DATE_ADDED +" DESC"
            "by_date_modified"-> order = MediaStore.Images.Media.DATE_MODIFIED +" ASC"
            "by_size"-> order = MediaStore.Images.Media.SIZE +" DESC"
        }
        val cursor: Cursor?
        val listOfAllImages = ArrayList<Image>()
        val projection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                MediaStore.Images.Media.TITLE,
                MediaStore.MediaColumns.RELATIVE_PATH,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED
            )
        } else
            arrayOf(
                MediaStore.Images.Media.TITLE,
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED
            )
        cursor = requireContext().contentResolver.query(uri, projection, null, null, order)

        var dataPath: String
        if (cursor != null) {
            while (cursor.moveToNext()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    dataPath = ContentUris.withAppendedId(uri, cursor.getLong(3)).toString()
                } else {
                    dataPath = cursor.getString(1)
                }

                val date = getDate(cursor.getLong(5)).toString()
                listOfAllImages.add(Image(cursor.getLong(3), cursor.getString(0), dataPath, date))
            }
        }
        cursor?.close()
        return listOfAllImages
    }

    private val permissionsResultCallback = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        // Handle Permission granted/rejected
        when (it) {
            true -> {
                mStoragePermissionGranted = true
                list = getImages(sortOrder)
                adapter.updateList(list)
                println("Permission has been granted by user")
            }
            false -> {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun permissionSetup() {
        val array = Manifest.permission.READ_EXTERNAL_STORAGE

        val permission = ContextCompat.checkSelfPermission(
            requireContext(),
            array
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            permissionsResultCallback.launch(array)
        } else {
            mStoragePermissionGranted = true
            println("Permission isGranted")
        }
    }

    private fun getDate(date: Long): String? {
        var tempDate = date
        tempDate *= 1000L
        return SimpleDateFormat("d MMM yyyy, hh:mm aa", Locale.getDefault()).format(Date(tempDate))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val pref = requireContext().getSharedPreferences("GALLERY", Context.MODE_PRIVATE).edit()
        pref.putBoolean("isGranted", mStoragePermissionGranted)
        pref.putInt("spanCount", spanCount)
        pref.putString("sorting", sortOrder)
        pref.apply()
    }
}