package com.example.sklad_8.data.repositores.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.sklad_8.data.db.entities.GoodEntity
import com.example.sklad_8.data.repositores.data.BarcodeEntity.Companion.TABLE_NAME

@Entity(
    tableName = TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = GoodEntity::class,
        parentColumns = ["id"],
        childColumns = ["goodId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class BarcodeEntity(
    @PrimaryKey
    val barcode: String,
    val goodId: String,
    val characteristicNomenclatureDescription: String? = null,
    val characteristicNomenclatureId: String? = null
) {
    companion object {
        const val TABLE_NAME = "barcode_good"
    }
}