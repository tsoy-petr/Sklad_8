package com.example.sklad_8.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sklad_8.data.db.entities.GoodEntity
import com.example.sklad_8.data.db.entities.ImgGoodEntity
import com.example.sklad_8.data.fullTextSearch.GoodsFtsEntity
import com.example.sklad_8.data.repositores.data.BarcodeEntity
import com.example.sklad_8.data.repositores.data.FeatureEntity

@Database(
    entities = [GoodEntity::class,
        ImgGoodEntity::class,
        BarcodeEntity::class,
        FeatureEntity::class,
        GoodsFtsEntity::class], version = 10, exportSchema = true
)
abstract class SkladDatabase : RoomDatabase() {

    abstract val goodsDao: GoodsDao
    abstract val imgGoodDao: ImgGoodDao
    abstract val barcodeDao: BarcodeDao
    abstract val featureDao: FeatureDao
    abstract val searchDao: SearchDao

    companion object {
        @Volatile
        private var INSTANCE: SkladDatabase? = null

        fun getInstance(context: Context): SkladDatabase {

            var instance = INSTANCE

            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    SkladDatabase::class.java,
                    "sklad_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
            }

            return instance
        }
    }
}