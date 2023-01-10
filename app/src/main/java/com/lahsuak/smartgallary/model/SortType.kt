package com.lahsuak.smartgallary.model

import com.lahsuak.smartgallary.util.AppConstants

enum class SortType(val typeName: String) {
    DATE_ADDED_SORT(AppConstants.SORT_DATE_ADDED),
    DATE_MODIFIED_SORT(AppConstants.SORT_DATE_MODIFIED),
    SIZE_SORT(AppConstants.SORT_SIZE),
    ;

    companion object {
        fun from(typeName: String): SortType {
            return when (typeName) {
                AppConstants.SORT_DATE_MODIFIED -> DATE_MODIFIED_SORT
                AppConstants.SORT_DATE_ADDED -> DATE_ADDED_SORT
                AppConstants.SORT_SIZE -> SIZE_SORT
                else -> {
                    error("Wrong $typeName value")
                }
            }
        }
    }
}