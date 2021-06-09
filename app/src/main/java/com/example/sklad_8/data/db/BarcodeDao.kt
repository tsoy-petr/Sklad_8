package com.example.sklad_8.data.db

import androidx.room.*
import com.example.sklad_8.data.repositores.data.BarcodeEntity
import com.example.sklad_8.data.repositores.data.BarcodeEntity.Companion.TABLE_NAME
import retrofit2.http.GET

@Dao
interface BarcodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: BarcodeEntity): Long

    @Update
    fun update(entity: BarcodeEntity)

    @Transaction
    fun saveBarcode(entity: BarcodeEntity) {
        if (insert(entity) == -1L) {
            update(entity)
        }
    }

    @Query("SELECT * FROM $TABLE_NAME WHERE goodId = :uuidGood")
    fun barcodesByGoodId(uuidGood: String): List<BarcodeEntity>

    @Query("SELECT goodId FROM $TABLE_NAME WHERE barcode = :barcode")
    fun getGoodId(barcode: String): String?

}