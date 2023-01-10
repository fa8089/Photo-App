package com.lahsuak.smartgallary.util

import android.content.Context
import android.net.Uri
import com.lahsuak.smartgallary.model.ImageDate
import com.lahsuak.smartgallary.model.SortType
import java.text.SimpleDateFormat
import java.util.*

private const val CONTENT = "content"
private const val DATA = "_data"
private const val FILE = "file"

object AppUtil {
    fun getDateMonth(dateCurrent: Long, dateNext: Long): Boolean {
        var tempDate = dateCurrent
        var tempDate2 = dateNext
        tempDate *= 1000L
        tempDate2 *= 1000L
        val month1 = SimpleDateFormat(AppConstants.DATE_MONTH, Locale.getDefault()).format(
            Date(
                tempDate
            )
        )
        val month2 = SimpleDateFormat(AppConstants.DATE_MONTH, Locale.getDefault()).format(
            Date(
                tempDate2
            )
        )
        val year1 = SimpleDateFormat(AppConstants.DATE_YEAR, Locale.getDefault()).format(
            Date(
                tempDate
            )
        )
        val year2 = SimpleDateFormat(AppConstants.DATE_YEAR, Locale.getDefault()).format(
            Date(
                tempDate2
            )
        )
        return month1 == month2 && year1 == year2
    }

    fun getDateByMonthYear(date: Long): String {
        var tempDate = date
        tempDate *= 1000L
        return SimpleDateFormat(AppConstants.DATE_MONTH_YEAR, Locale.getDefault()).format(
            Date(
                tempDate
            )
        )
    }

    fun getDate(date: Long): String {
        var tempDate = date
        tempDate *= 1000L
        return SimpleDateFormat(AppConstants.DATE_FORMAT, Locale.getDefault()).format(
            Date(
                tempDate
            )
        )
    }

    fun sortImages(list: List<ImageDate>, sortOrder: SortType): List<ImageDate> {
        return try {
            val tempList = mutableListOf<ImageDate>()
            tempList.addAll(list)
            Collections.sort(tempList, ImageDate.ImageComparator(sortOrder))
            tempList
        } catch (e: UnsupportedOperationException) {
            e.printStackTrace()
            list
        }
    }

    fun getRealPath(uri: Uri, context: Context): String? {
        var realPath: String? = null
        try {
            if (uri.scheme!! == CONTENT) {
                val projection = arrayOf(DATA)
                val cursor = context.contentResolver.query(
                    uri,
                    projection, null, null, null
                )
                if (cursor != null) {
                    val id = cursor.getColumnIndexOrThrow(DATA)
                    cursor.moveToNext()
                    realPath = try {
                        cursor.getString(id)
                    } catch (e: Exception) {
                        null
                    } finally {
                        cursor.close()
                    }
                } else if (uri.scheme!! == FILE) {
                    realPath = uri.path!!
                }
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            realPath = null
        }
        return realPath
    }
}