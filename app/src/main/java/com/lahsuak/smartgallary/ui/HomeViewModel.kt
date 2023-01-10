package com.lahsuak.smartgallary.ui

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import com.lahsuak.smartgallary.model.Image
import com.lahsuak.smartgallary.model.ImageDate
import com.lahsuak.smartgallary.util.AppConstants
import com.lahsuak.smartgallary.util.AppUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {
    private val _imagesMutableDataFlow =
        MutableStateFlow<List<ImageDate>>(emptyList())
    val imagesDataFlow: StateFlow<List<ImageDate>>
        get() = _imagesMutableDataFlow

    fun getImages(context: Context, sortOrder: String) {
        val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val order = when (sortOrder) {
            AppConstants.SORT_DATE_ADDED ->
                MediaStore.Images.Media.DATE_ADDED + " " + AppConstants.DESC
            AppConstants.SORT_DATE_MODIFIED ->
                MediaStore.Images.Media.DATE_MODIFIED + " " + AppConstants.DESC
            AppConstants.SORT_SIZE ->
                MediaStore.Images.Media.SIZE + " " + AppConstants.DESC
            else ->
                MediaStore.Images.Media.DATE_ADDED + " " + AppConstants.DESC
        }
        val listOfAllImages = mutableListOf<Image>()
        val projection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                MediaStore.Images.Media.TITLE,
                MediaStore.MediaColumns.RELATIVE_PATH,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.SIZE
            )
        } else
            arrayOf(
                MediaStore.Images.Media.TITLE,
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.SIZE
            )
        val cursor = context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            order
        )

        if (cursor != null) {
            while (cursor.moveToNext()) {
                val dataPath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentUris.withAppendedId(uri, cursor.getLong(3)).toString()
                } else {
                    cursor.getString(1)
                }

                listOfAllImages.add(
                    Image(
                        cursor.getLong(3),
                        cursor.getString(0),
                        dataPath,
                        cursor.getLong(5),
                        cursor.getLong(6),
                        cursor.getLong(7),
                    )
                )
            }
        }
        cursor?.close()
        _imagesMutableDataFlow.value = listOfAllImages.zipWithNext { current, next ->
            if (AppUtil.getDateMonth(current.dateAdded, next.dateAdded)) {
                ImageDate(
                    AppConstants.ViewType.VIEW_TYPE_IMAGE,
                    image = current
                )
            } else {
                ImageDate(
                    AppConstants.ViewType.VIEW_TYPE_DATE,
                    image = current
                )
            }
        }
    }
}