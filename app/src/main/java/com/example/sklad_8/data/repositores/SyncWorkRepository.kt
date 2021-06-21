package com.example.sklad_8.data.repositores

import com.example.sklad_8.data.db.SkladDatabase
import com.example.sklad_8.data.db.entities.GoodEntity
import com.example.sklad_8.data.db.entities.ImgGoodEntity
import com.example.sklad_8.data.db.entities.MessageSync
import com.example.sklad_8.data.db.entities.TypeMessage
import com.example.sklad_8.data.network.ApiService
import com.example.sklad_8.data.network.SafeApiRequest
import com.example.sklad_8.data.repositores.data.ResultGoods
import com.example.sklad_8.util.Base64Util.toBase64AsByteArray
import timber.log.Timber
import kotlin.reflect.KSuspendFunction1

class SyncWorkRepository(
    private val api: ApiService,
    private val db: SkladDatabase,
) : SafeApiRequest() {

    suspend fun fetchGoods(showMessage: suspend (MessageSync) -> Unit) {
        showMessage(MessageSync(title = "Подключение к серверу", type = TypeMessage.LOADING))

        try {

            showMessage(MessageSync(title = "Загрузка описаний данных", type = TypeMessage.LOADING))

            val data = apiRequest {
                api.getInfoAtPackegs("getInfoAtPackegs")
            }

            if (!data.errors.exist) {

                if (data.result?.size ?: 0 > 0) {

                    data.result?.forEach {

                        var prePortionsId = ""
                        var preNumberPortion: Int = 0
                        var preDataType = ""
                        val portionsId = it.portionsId
                        it.portions.forEach { part ->

                            if (part.dataType == "good") {

                                showMessage(
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
                                        dataType = part.dataType,
                                        prePortionsId = prePortionsId,
                                        preNumberPortion = preNumberPortion,
                                        preDataType = preDataType
                                    )
                                }

                                if (!goods.errors.exist) {

                                    prePortionsId = portionsId
                                    preNumberPortion = part.numberPortion
                                    preDataType = part.dataType

                                    goods.result.forEach { good ->

                                        saveGoodEntity(good)
                                        savePhoto(good, isMain = true)
                                        saveBarcodes(good)
                                        saveFeatures(good)

                                        showMessage(
                                            MessageSync(
                                                title = "Загружен: ${good.name}",
                                                type = TypeMessage.LOADING
                                            )
                                        )
                                    }
                                } else showMessage(
                                    MessageSync(
                                        title = data.errors.title,
                                        type = TypeMessage.ERROR
                                    )
                                )
                            }
                        }
                    }
                    showMessage(
                        MessageSync(
                            title = "Загрузка закончена",
                            type = TypeMessage.SUCCES
                        )
                    )
                } else {
                    showMessage(
                        MessageSync(
                            title = "Нет данных для загрузки",
                            type = TypeMessage.SUCCES
                        )
                    )
                }
            } else {
                showMessage(MessageSync(title = data.errors.title, type = TypeMessage.ERROR))
            }
        } catch (e: Exception) {
            showMessage(MessageSync(title = e.message.toString(), type = TypeMessage.ERROR))
        }
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