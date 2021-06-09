package com.example.sklad_8.ui.goods

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GoodViewData(
    val id: String,
    val title: String,
    val article: String,
    val isGroup: Boolean,
    val isHeader: Boolean,
    val parent: String,
    val tabId: Int = 0,
    val screenId: Int = 0
): Parcelable
