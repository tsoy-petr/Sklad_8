package com.example.sklad_8.data.db

import androidx.room.*
import com.example.sklad_8.data.db.entities.GoodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoodsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(entity: GoodEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entities: List<GoodEntity>): List<Long>

    @Update
    fun update(entity: GoodEntity)

    @Transaction
    fun saveGood(entity: GoodEntity) {
        if (insert(entity) == -1L) {
            update(entity)
        }
    }

    @Transaction
    fun saveGoods(entities: List<GoodEntity>) {
        insert(entities)
    }

    @Query("SELECT * FROM goods_table WHERE id = :uuid")
    fun getGood(uuid: String): GoodEntity?

    @Query("SELECT * FROM goods_table WHERE parent = :uuidParent")
    fun getGoods(uuidParent: String): List<GoodEntity>

    @Query("SELECT * FROM goods_table WHERE parent = :uuidParent")
    fun getGoodsFlow(uuidParent: String): Flow<List<GoodEntity>>

    @Query("SELECT COUNT(id) from goods_table")
    fun getCountGoods() : Long

    @Query("DELETE FROM goods_table")
    fun deleteAllGoods()

}