package com.example.sklad_8.data.repositores

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Base64
import com.example.sklad_8.data.db.SkladDatabase
import com.example.sklad_8.data.db.entities.GoodEntity
import com.example.sklad_8.data.db.entities.MessageSync
import com.example.sklad_8.data.db.entities.TypeMessage
import com.example.sklad_8.data.network.ApiService
import com.example.sklad_8.data.network.SafeApiRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import java.io.IOException


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
                                        title = "Загрузка порции товаров (${part.numberPortion})",
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

                                        var path = ""
                                        if (good.img.isNotEmpty()) {
                                            val decodeValue: ByteArray =
                                                Base64.decode(good.img, Base64.DEFAULT)

                                            val bitmap = BitmapFactory.decodeByteArray(
                                                decodeValue,
                                                0,
                                                decodeValue.size
                                            )
                                            path = savePhotoToInternalStorage(good.imgName, bitmap)
                                        }

                                        val goodEntity = GoodEntity(
                                            id = good.id,
                                            parent = good.parentId,
                                            isGroup = good.isGroup,
                                            name = good.name,
                                            vendorCode = good.vendorCode,
                                            imgMain = path,
                                            deletionMark = good.deletionMark
                                        )

                                        db.goodsDao.saveGood(goodEntity)
                                        send(
                                            MessageSync(
                                                title = "Загружен: ${goodEntity.name}",
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

    private fun savePhotoToInternalStorage(fileName: String, bmp: Bitmap): String {

        return try {
            context.openFileOutput("$fileName.jpg", MODE_PRIVATE).use { stream ->
                if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                    throw IOException("Couldn't save bitmap.")
                }
            }
            context.getFileStreamPath(fileName).absolutePath
        } catch (e: IOException) {
            ""
        }
    }

}