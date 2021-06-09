package com.example.sklad_8.data.db

import androidx.room.*
import com.example.sklad_8.data.repositores.data.FeatureEntity
import com.example.sklad_8.data.repositores.data.FeatureEntity.Companion.TABLE_NAME

@Dao
interface FeatureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: FeatureEntity): Long

    @Update
    fun update(entity: FeatureEntity)

    @Transaction
    fun saveFeature(entity: FeatureEntity) {
        if (insert(entity) == -1L) {
            update(entity)
        }
    }

    @Query("SELECT * FROM $TABLE_NAME WHERE goodId = :goodId")
    fun getFeaturesByGoodId(goodId: String): List<FeatureEntity>
}