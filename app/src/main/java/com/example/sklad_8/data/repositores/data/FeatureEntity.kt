package com.example.sklad_8.data.repositores.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.sklad_8.data.db.entities.GoodEntity
import com.example.sklad_8.data.repositores.data.FeatureEntity.Companion.TABLE_NAME

@Entity(
    tableName = TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = GoodEntity::class,
        parentColumns = ["id"],
        childColumns = ["goodId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class FeatureEntity(
    @PrimaryKey
    val id: String,
    val goodId: String,
    val name: String
) {
    companion object {
        const val TABLE_NAME = "feature_good"
    }
}