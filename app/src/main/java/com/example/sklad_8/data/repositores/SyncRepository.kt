package com.example.sklad_8.data.repositores

import android.content.Context
import android.util.Log
import com.example.sklad_8.data.db.SkladDatabase
import com.example.sklad_8.data.db.entities.GoodEntity
import com.example.sklad_8.data.db.entities.MessageSync
import com.example.sklad_8.data.db.entities.TypeMessage
import com.example.sklad_8.data.network.ApiService
import com.example.sklad_8.data.network.SafeApiRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import com.example.sklad_8.data.db.entities.ImgGoodEntity
import com.example.sklad_8.data.repositores.data.ResultGoods
import com.example.sklad_8.util.Base64Util.toBase64AsByteArray
import timber.log.Timber

class SyncRepository(
    private val api: ApiService,
    private val db: SkladDatabase,
    private val context: Context
) : SafeApiRequest() {

    @ExperimentalCoroutinesApi
    fun getGoods() = callbackFlow {

        send(MessageSync(title = "Подключение к серверу", type = TypeMessage.LOADING))

        try {

            send(MessageSync(title = "Загрузка описаний данных", type = TypeMessage.LOADING))

            val data = apiRequest {
                api.getInfoAtPackegs("getInfoAtPackegs")
            }

            if (!data.errors.exist) {

                if (data.result?.size ?: 0 > 0) {

                    data.result?.forEach {

                        val portionsId = it.portionsId
                        it.portions.forEach { part ->

                            if (part.dataType == "good") {

                                send(
                                    MessageSync(
                                        title = "Загрузка порции товаров (${part.numberPortion} из ${it.portions.size})",
                                        type = TypeMessage.LOADING
                                    )
                                )

                                val goods = apiRequest {
                                    api.getPortionGoods(
                                        operation = "getPortion",
                                        portionsId = portionsId,
                                        numberPortion = part.numberPortion,
                                        dataType = part.dataType
                                    )
                                }

                                if (!goods.errors.exist) {
                                    goods.result.forEach { good ->

                                        saveGoodEntity(good)
                                        savePhoto(good, isMain = true)
                                        saveBarcodes(good)
                                        saveFeatures(good)

                                        send(
                                            MessageSync(
                                                title = "Загружен: ${good.name}",
                                                type = TypeMessage.LOADING
                                            )
                                        )
                                    }
                                } else send(
                                    MessageSync(
                                        title = data.errors.title,
                                        type = TypeMessage.ERROR
                                    )
                                )
                            }
                        }
                    }
                    send(MessageSync(title = "Загрузка закончена", type = TypeMessage.SUCCES))
                } else {
                    send(MessageSync(title = "Нет данных для загрузки", type = TypeMessage.SUCCES))
                }
            } else {
                send(MessageSync(title = data.errors.title, type = TypeMessage.ERROR))
            }
        } catch (e: Exception) {
            send(MessageSync(title = e.message.toString(), type = TypeMessage.ERROR))
        }
        awaitClose()
    }

    private fun saveBarcodes(good: ResultGoods) {
        good.barcodes?.forEach {
           Timber.i(it.toString())
            db.barcodeDao.saveBarcode(
                it.copy(
                    goodId = good.id
                )
            )
        }
    }
    private fun saveFeatures(good: ResultGoods) {
        good.feature?.forEach {
            db.featureDao.saveFeature(it.copy(goodId = good.id))
        }
    }
    private fun saveGoodEntity(good: ResultGoods) {
        val goodEntity = GoodEntity(
            id = good.id,
            parent = good.parentId,
            isGroup = good.isGroup,
            name = good.name,
            vendorCode = good.vendorCode,
            deletionMark = good.deletionMark
        )
        db.goodsDao.saveGood(goodEntity)
    }

    private fun savePhoto(good: ResultGoods, isMain: Boolean = false) {

        if (good.img.isNotEmpty()) {
            db.imgGoodDao.saveImgGood(
                ImgGoodEntity(
                    id = good.imgName,
                    goodId = good.id,
                    isMain = isMain,
                    imgDigit = good.img.toBase64AsByteArray()
                )
            )
        }
    }

}