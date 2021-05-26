package com.example.sklad_8.data.network.responses

data class SyncPackagesDescriptionsResponse(
    val success: Boolean,
    val message: String,
    val data: List<String>?
)
