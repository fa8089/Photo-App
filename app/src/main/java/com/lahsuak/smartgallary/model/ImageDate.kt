package com.lahsuak.smartgallary.model

import java.util.Comparator

data class ImageDate(
    val contentType: Int,
    val image: Image
){
    class ImageComparator(var sort: SortType) : Comparator<ImageDate> {
        override fun compare(m1: ImageDate, m2: ImageDate): Int {
            val result = when (sort) {
                SortType.DATE_ADDED_SORT -> m2.image.dateAdded.compareTo(m1.image.dateAdded)
                SortType.DATE_MODIFIED_SORT -> m1.image.dateAdded.compareTo(m2.image.dateAdded)
                SortType.SIZE_SORT -> m2.image.size.compareTo(m1.image.size)
            }
            return if (result > 0)
                1
            else if (result < 0)
                -1
            else
                0
        }
    }
}