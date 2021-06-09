package com.example.sklad_8.data.db

import androidx.room.*
import com.example.sklad_8.data.db.entities.ImgGoodEntity

@Dao
interface ImgGoodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: ImgGoodEntity): Long

    @Update
    fun update(entity: ImgGoodEntity)

    @Query("SELECT * FROM img_good WHERE goodId = :uuidGood AND isMain")
    fun getMainImgGood(uuidGood: String): ImgGoodEntity?

    @Transaction
    fun saveImgGood(entity: ImgGoodEntity) {
        if (insert(entity) == -1L) {
            update(entity)
        }
    }

    @Query("SELECT COUNT(id) from img_good")
    fun getCountImgGoods() : Long

}