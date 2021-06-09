package com.example.sklad_8.data.repositores

import com.example.sklad_8.data.db.SkladDatabase

class CommonSettingsRepository(
    private val db: SkladDatabase
) {

    fun fetchGoodsCount() = db.goodsDao.getCountGoods()
    fun fetchImgGoodsCount() = db.imgGoodDao.getCountImgGoods()

    fun deleteAllGoods() = db.goodsDao.deleteAllGoods()

}