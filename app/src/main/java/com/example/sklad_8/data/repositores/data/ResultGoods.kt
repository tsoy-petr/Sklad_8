package com.example.sklad_8.data.repositores.data

data class ResultGoods(
    val barcodes: List<Barcode>,
    val code: String,
    val deletionMark: Boolean,
    val feature: List<Feature>,
    val id: String,
    val img: String,
    val imgName: String,
    val isGroup: Boolean,
    val name: String,
    val nomenclatureTypeDescription: String,
    val nomenclatureTypeId: String,
    val parentId: String,
    val vendorCode: String
)