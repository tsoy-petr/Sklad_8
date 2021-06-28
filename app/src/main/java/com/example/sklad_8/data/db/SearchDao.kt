package com.example.sklad_8.data.db

import androidx.room.Dao
import androidx.room.Query
import com.example.sklad_8.data.db.entities.GoodEntity

@Dao
interface SearchDao {

    @Query(
        "SELECT * FROM goods_table INNER JOIN goods_fts ON goods_table.id == goods_fts.id WHERE goods_fts.name MATCH :words AND NOT goods_table.isGroup GROUP BY goods_table.id"
    )
    fun searchByName(words: String): List<GoodEntity>

}