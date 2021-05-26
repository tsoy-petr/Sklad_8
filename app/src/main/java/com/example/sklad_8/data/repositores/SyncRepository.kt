package com.example.sklad_8.data.repositores

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Base64
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
import timber.log.Timber
import android.graphics.BitmapFactory

import android.graphics.Bitmap
import java.io.FilenameFilter
import java.io.IOException


class SyncRepository(
    private val api: ApiService,
    private val db: SkladDatabase,
    context: Context
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

                                send(MessageSync(title = "Загрузка порции товаров (${part.numberPortion})", type = TypeMessage.LOADING))

                                val goods = apiRequest {
                                    api.getPortionGoods(
                                        operation = "getPortion",
                                        portionsId = portionsId,
                                        numberPortion = part.numberPortion,
                                        dataType = part.dataType
                                    )
                                }

                                if (!goods.errors.exist) {
                                    goods.result.forEach {good->

                                        if(good.img.isNotEmpty()) {
                                            val decodeValue: ByteArray =
                                                Base64.decode(good.img, Base64.DEFAULT)

                                            val bitmap = BitmapFactory.decodeByteArray(
                                                decodeValue,
                                                0,
                                                decodeValue.size
                                            )
                                        }

                                        val goodEntity = GoodEntity(
                                            id = good.id,
                                            parent = good.parentId,
                                            isGroup = good.isGroup,
                                            name = good.name,
                                            vendorCode = good.vendorCode,
                                            imgMain = "",
                                            deletionMark = good.deletionMark
                                        )

                                        db.goodsDao.saveGood(goodEntity)
                                        send(MessageSync(title = "Загружен: ${goodEntity.name}", type = TypeMessage.LOADING))

                                    }
                                    Timber.d(goods.result.toString())
                                } else send(MessageSync(title = data.errors.title, type = TypeMessage.ERROR))


                            }

                        }

                    }

                } else {
                    send(MessageSync(title = "Нет данных для загрузки", type = TypeMessage.SUCCES))
                }

            } else {
                send(MessageSync(title = data.errors.title, type = TypeMessage.ERROR))
            }

//            val remoteDescriptionsData = apiRequest {
//                api.getPackagesDescriptions()
//            }
//            var counterPackages = 0
//            if (remoteDescriptionsData.success) {
//                send(MessageSync(title = "Загрузка описаний данных", type = TypeMessage.LOADING))
//
//                var preUuidPackage = ""
//
//                remoteDescriptionsData.data?.forEach { uuid_package ->
//                    val packageData = apiRequest { api.getPackages(uuid_package, preUuidPackage) }
//                    if (packageData.success) {
//                        packageData.data?.forEach {
//                            it?.let {
//                                send(
//                                    MessageSync(
//                                        title = "Загрузка: " + it.name,
//                                        type = TypeMessage.LOADING
//                                    )
//                                )
//                                db.goodsDao.saveGood(it)
//                                counterPackages++
//                            }
//                        }
//                        preUuidPackage = uuid_package
//                    } else {
//                        send(MessageSync(title = packageData.message, type = TypeMessage.ERROR))
//                    }
//                }
//            }
//            send(
//                MessageSync(
//                    title = "Загрузка выполнена (загружено: $counterPackages)",
//                    type = TypeMessage.SUCCES
//                )
//            )
        } catch (e: Exception) {
            send(MessageSync(title = e.message.toString(), type = TypeMessage.ERROR))
        }

        send(MessageSync(title ="Загрузка закончена", type = TypeMessage.SUCCES))

        awaitClose()

    }

    private fun savePhotoToInternalStorage(context: Context, fileName: String, bmp: Bitmap): Boolean {

        return try {
            context.openFileOutput("$fileName.jpg", MODE_PRIVATE).use{stream ->
                if(!bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                    throw IOException("Couldn't save bitmap.")
                }
            }
            true
        } catch (e: IOException) {
            false
        }
    }

}