package com.example.sklad_8.ui.goods

data class GoodViewData(
    val id: String,
    val title: String,
    val article: String,
    val isGroup: Boolean,
    val isHeader: Boolean,
    val parent: String
)
