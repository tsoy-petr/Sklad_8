package com.example.sklad_8.data.worker

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sklad_8.data.db.SkladDatabase
import com.example.sklad_8.data.db.entities.MessageSync
import com.example.sklad_8.data.db.entities.TypeMessage
import com.example.sklad_8.data.network.RemoteService
import com.example.sklad_8.data.network.SafeApiRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import timber.log.Timber

class SyncWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    companion object {
        private val _isSync = MutableLiveData<Boolean>()
        val isSync: LiveData<Boolean> = _isSync
        val _syncMessage = MutableLiveData<MessageSync>()
        val syncMessage: LiveData<MessageSync> = _syncMessage

        private val _syncStatus = MutableStateFlow(
            MessageSync(type = TypeMessage.INIT)
        )
        val syncStatus = _syncStatus.asStateFlow()
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val appContext = applicationContext
            val db = SkladDatabase.getInstance(context = appContext)
            val api = RemoteService.getInstance(appContext)
            val safeApiRequest = object : SafeApiRequest() {}

//            _syncMessage.postValue(
//                MessageSync(
//                    title = "Загрузка описаний данных",
//                    type = TypeMessage.LOADING
//                )
//            )
            updateMessage {
                copy(
                    title = "Загрузка описаний данных",
                    type = TypeMessage.LOADING
                )
            }

            val data = safeApiRequest.apiRequest { api.getInfoAtPackegs("getInfoAtPackegs") }

            if (!data.errors.exist) {

                if (data.result?.size ?: 0 > 0) {

                    data.result?.forEach {

                        val portionsId = it.portionsId
                        it.portions.forEach { part ->

                            if (part.dataType == "good") {

//                                _syncMessage.postValue(
//                                    MessageSync(
//                                        title = "Загрузка порции товаров (${part.numberPortion} из ${it.portions.size})",
//                                        type = TypeMessage.LOADING
//                                    )
//                                )
                                updateMessage {
                                    copy(
                                        title = "Загрузка порции товаров (${part.numberPortion} из ${it.portions.size})",
                                        type = TypeMessage.LOADING
                                    )
                                }
                                val goods = safeApiRequest.apiRequest {
                                    api.getPortionGoods(
                                        operation = "getPortion",
                                        portionsId = portionsId,
                                        numberPortion = part.numberPortion,
                                        dataType = part.dataType
                                    )
                                }

                                if (!goods.errors.exist) {
                                    goods.result.forEach { good ->

//                                        saveGoodEntity(good)
//                                        savePhoto(good, isMain = true)
//                                        saveBarcodes(good)
//                                        saveFeatures(good)

//                                        _syncMessage.postValue(
//                                            MessageSync(
//                                                title = "Загружен: ${good.name}",
//                                                type = TypeMessage.LOADING
//                                            )
//                                        )
                                        updateMessage {
                                            copy(
                                                title = "Загружен: ${good.name}",
                                                type = TypeMessage.LOADING
                                            )
                                        }
                                    }
                                } else {
//                                    _syncMessage.postValue(
//                                        MessageSync(
//                                            title = data.errors.title,
//                                            type = TypeMessage.ERROR
//                                        )
//                                    )
                                    updateMessage {
                                        copy(
                                            title = data.errors.title,
                                            type = TypeMessage.ERROR
                                        )
                                    }
                                }
                            }
                        }
                    }
//                    _syncMessage.postValue(
//                        MessageSync(
//                            title = "Загрузка закончена",
//                            type = TypeMessage.SUCCES
//                        )
//                    )
                    updateMessage {
                        copy(
                            title = "Загрузка закончена",
                            type = TypeMessage.SUCCES
                        )
                    }
                } else {
//                    _syncMessage.postValue(
//                        MessageSync(
//                            title = "Нет данных для загрузки",
//                            type = TypeMessage.SUCCES
//                        )
//                    )
                    updateMessage {
                        copy(
                            title = "Нет данных для загрузки",
                            type = TypeMessage.SUCCES
                        )
                    }
                }
            } else {
                updateMessage {
                    copy(
                        title = data.errors.title,
                        type = TypeMessage.ERROR
                    )
                }
//                _syncMessage.postValue(
//                    MessageSync(
//                        title = data.errors.title,
//                        type = TypeMessage.ERROR
//                    )
//                )
            }
            Result.success()
        } catch (throwable: Throwable) {

            updateMessage {
                copy(
                    title = throwable.message.toString(), type = TypeMessage.ERROR
                )
            }
//            _syncMessage.postValue(
//                MessageSync(
//                    title = throwable.message.toString(),
//                    type = TypeMessage.ERROR
//                )
//            )
//            _isSync.postValue(false)
//            Timber.e(throwable, "Error applying blur")
            Result.failure(
            )
        }
    }

    private fun updateMessage(mapper: MessageSync.() -> MessageSync = { this }) {
        val data = _syncStatus.value.mapper()
        _syncStatus.value = data
    }
}