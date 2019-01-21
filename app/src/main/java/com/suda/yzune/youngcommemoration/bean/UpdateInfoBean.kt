package com.suda.yzune.youngcommemoration.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UpdateInfoBean(
    val id: Int,
    val VersionName: String,
    val VersionInfo: String
) : Parcelable