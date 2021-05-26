package com.example.sklad_8.data.repositores

import com.example.sklad_8.data.db.SkladDatabase
import com.example.sklad_8.data.mappers.toUI
import com.example.sklad_8.data.mappers.toUi
import com.example.sklad_8.data.network.SafeApiRequest
import com.example.sklad_8.ui.goods.GoodViewData

class GoodsRepository(
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


    fun fetchGoodParentByUUID(uuidParent: String) =
        db.goodsDao.getGood(uuidParent)

}