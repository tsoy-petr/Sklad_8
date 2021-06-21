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
import android.util.Base64
import com.example.sklad_8.data.network.ApiService
import kotlinx.coroutines.flow.Flow


class GoodsRepository(
    private val api: ApiService,
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
        val currentParent = fetchGoodByUUID(currentGood.parent)
        if (currentParent == null) {
            return
        } else {
            val parent = currentParent.toUI(true)
            allParents.add(parent)
            findAllParents(parent, allParents)
        }
    }

    fun fetchGoodByUUID(uuidParent: String): GoodEntity? {
        return db.goodsDao.getGood(uuidParent)
    }

    fun fetchImgMainGood(uuidGood: String): Bitmap? {
        val imgGood = db.imgGoodDao.getMainImgGood(uuidGood) ?: return null
        val encodeValue = imgGood.imgDigit ?: return null
        return BitmapFactory.decodeByteArray(encodeValue, 0, encodeValue.size)
    }

    fun fetchBarcodes(uuidGood: String) =
        db.barcodeDao.barcodesByGoodId(uuidGood)

    suspend fun fetchReport(goodId: String) : String {
        return try {
            val report = apiRequest {
                api.getReportByGoods("getReportByGood", goodId)
            }
           report.result
        } catch (e: Exception) {
            ""
        }

    }
}