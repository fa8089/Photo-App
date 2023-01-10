package com.lahsuak.smartgallary.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Image(
    val id: Long,
    val name: String,
    val path: String,
    val dateAdded: Long,
    val dateModified: Long,
    val size: Long
) : Parcelable
