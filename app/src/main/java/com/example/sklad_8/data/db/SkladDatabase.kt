package com.example.sklad_8.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sklad_8.data.db.entities.GoodEntity

@Database(entities = [GoodEntity::class], version = 2, exportSchema = false)
abstract class SkladDatabase: RoomDatabase()  {

    abstract val goodsDao: GoodsDao

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