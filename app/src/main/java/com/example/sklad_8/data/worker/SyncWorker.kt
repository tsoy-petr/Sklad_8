package com.example.sklad_8.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.sklad_8.R
import com.example.sklad_8.data.db.entities.MessageSync
import com.example.sklad_8.data.db.entities.TypeMessage
import com.example.sklad_8.data.repositores.SyncWorkRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SyncWorker(
    val repository: SyncWorkRepository,
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "sync_channel"
        const val NOTIFICATION_CHANNEL_NAME = "Sync"
        const val NOTIFICATION_ID = 1

        private val _syncStatus = MutableStateFlow(
            MessageSync(type = TypeMessage.INIT)
        )
        val syncStatus = _syncStatus.asStateFlow()
    }

    private val notificationManager =
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            setForeground(createForegroundInfo("Начало синхронизации"))
            repository.fetchGoods(::updateNotification)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
    private suspend fun updateNotification(message: MessageSync) {
        setForeground(createForegroundInfo(message.title))
        _syncStatus.value = message
    }

    private fun createForegroundInfo(progress: String): ForegroundInfo {
        val id = "sync_channel"
        val title = "Синхронизация данных"
        val cancel = "Завершить"
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(getId())

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        val notification = NotificationCompat.Builder(applicationContext, id)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(progress)
            .setSmallIcon(R.drawable.ic_work_notification)
            .setOngoing(true)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
            .addAction(android.R.drawable.ic_delete, cancel, intent)
            .build()

        return ForegroundInfo(NOTIFICATION_ID, notification)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

}