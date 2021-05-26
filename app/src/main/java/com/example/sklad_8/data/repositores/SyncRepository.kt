package com.example.sklad_8.data.repositores

import android.util.Log
import com.example.sklad_8.data.db.SkladDatabase
import com.example.sklad_8.data.db.entities.MessageSync
import com.example.sklad_8.data.db.entities.TypeMessage
import com.example.sklad_8.data.network.ApiService
import com.example.sklad_8.data.network.SafeApiRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class SyncRepository(
    private val api: ApiService,
    private val db: SkladDatabase
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

        awaitClose()

    }

}