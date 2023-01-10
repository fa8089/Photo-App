package com.lahsuak.smartgallary.util

object AppConstants {
    const val SORT_DATE_ADDED = "date_added"
    const val SORT_DATE_MODIFIED = "date_modified"
    const val SORT_SIZE = "by_size"
    const val DESC = "DESC"
    const val DEFAULT_SPAN_COUNT = 3
    const val DATE_MONTH = "MM"
    const val DATE_YEAR = "yyyy"
    const val DATE_FORMAT = "d MMM yyyy, hh:mm aa"
    const val DATE_MONTH_YEAR = "MMM yyyy"

    object SharedPrefConstants {
        const val GALLERY_SORT_PREF_KEY = "gallery_sort_pref_key"
        const val SPAN_COUNT_KEY = "span_count_key"
        const val SORT_KEY = "sort_key"
    }

    object ViewType {
        const val VIEW_TYPE_IMAGE = 0
        const val VIEW_TYPE_DATE = 1
    }
}