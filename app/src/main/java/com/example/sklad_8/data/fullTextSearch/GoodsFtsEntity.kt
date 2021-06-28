package com.example.sklad_8.data.fullTextSearch

import androidx.room.Entity
import androidx.room.Fts4
import com.example.sklad_8.data.db.entities.GoodEntity

@Fts4(contentEntity = GoodEntity::class)
@Entity(tableName = "goods_fts")
data class GoodsFtsEntity(
    val id: String,
    val name: String
)
