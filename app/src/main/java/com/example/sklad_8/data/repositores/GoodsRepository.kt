package com.example.sklad_8.data.repositores

import android.content.Context
import com.example.sklad_8.data.db.SkladDatabase
import com.example.sklad_8.data.db.entities.GoodEntity
import com.example.sklad_8.data.mappers.toUI
import com.example.sklad_8.data.mappers.toUi
import com.example.sklad_8.data.network.SafeApiRequest
import com.example.sklad_8.ui.goods.GoodViewData
import java.io.File
import android.graphics.BitmapFactory

import android.graphics.Bitmap




class GoodsRepository(
    private val context: Context,
    private val db: SkladDatabase
) : SafeApiRequest() {

    fun fetchGoodsByParent(
        good: GoodViewData? = null,
        isFirst: Boolean = false
    ): List<GoodViewData> {

        var uuidParent = ""
        val data = mutableListOf<GoodViewData>()
        if (good != null) {
            if (!good.isHeader) {
                uuidParent = good.id
                findAllParents(good, data)
                data.add(good.copy(isHeader = true))
            } else {
                uuidParent = good.parent
                findAllParents(good, data)
            }
        }
        data.addAll(
            db.goodsDao.getGoods(uuidParent).toUi()
        )
        return data
    }

    private fun findAllParents(currentGood: GoodViewData, allParents: MutableList<GoodViewData>) {
        if (currentGood.parent.isEmpty()) return
        val currentParent = fetchGoodParentByUUID(currentGood.parent)
        if (currentParent == null) {
            return
        } else {
            val parent = currentParent.toUI(true)
            allParents.add(parent)
            findAllParents(parent, allParents)
        }
    }


    fun fetchGoodParentByUUID(uuidParent: String, withImage: Boolean = false): GoodEntity? {
        val good = db.goodsDao.getGood(uuidParent)
        good?.let {
            if (withImage && good.imgMain.isNotEmpty() == true) {
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                val bitmap = BitmapFactory.decodeFile(good.imgMain, options)
                good.btmImg = bitmap
                return good
            }
        }
        return good
    }


}