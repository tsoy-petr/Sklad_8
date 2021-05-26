package com.example.sklad_8.data.network.responses

import com.example.sklad_8.data.db.entities.GoodEntity

data class SyncPackagesResponse(
    val success: Boolean,
    val message: String,
    val data: List<GoodEntity?>?
)
