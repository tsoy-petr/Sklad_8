package com.example.sklad_8.data.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.sklad_8.App
import com.example.sklad_8.MainActivity
import com.example.sklad_8.R
import com.example.sklad_8.data.Constants
import com.example.sklad_8.data.Constants.BROADCAST_ACTION
import com.example.sklad_8.data.prefs.SharedPrefsManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException

class BluetoothService : LifecycleService() {

    private var isFirstRun = true
    private var serviceKilled = false
    private var scanningJob: Job? = null
    private var btSocket: BluetoothSocket? = null

    private lateinit var curNotificationBuilder: NotificationCompat.Builder
    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    companion object {
        val isScanning = MutableLiveData<Boolean>()
        val scanningData = MutableLiveData<ScanningData>()
        private val _scanningEvent = MutableStateFlow(
            ScanningData(state = ScanningState.Empty)
        )
        val scanningEvent = _scanningEvent.asStateFlow()
    }

    private fun postInitialValues() {
        isScanning.postValue(false)
    }

    private fun killService() {
        serviceKilled = true
        isFirstRun = true
        pauseService()
        btSocket?.let {
            try {
                it.close()
            } catch (e: Exception) {
                Log.i("happy", e.toString())
            }
        }
        scanningJob?.cancel()
        postInitialValues()
        stopForeground(true)
        stopSelf()
    }

    private fun pauseService() {
        isScanning.postValue(false)
    }

    override fun onCreate() {
        super.onCreate()

        curNotificationBuilder = provideBaseNotificationBuilder()

        postInitialValues()

//        scanningData.observe(this, {
//            it?.let { updateBroadCast(it) }
//        })
        GlobalScope.launch {
            _scanningEvent.collect {
                updateBroadCast(it)
            }
        }
    }

    private fun updateBroadCast(scanningData: ScanningData) {

        val intent = Intent(BROADCAST_ACTION)

        val data =
            when (scanningData.state) {
                is ScanningState.Empty -> {
                    BroadcastData()
                }
                is ScanningState.Error -> {
                    BroadcastData(
                        isError = true, error = scanningData.state.message
                    )
                }
                is ScanningState.Barcode -> {
                    BroadcastData(barcode = scanningData.state.data)
                }
                is ScanningState.HasNotBluetoothPermissions -> {
                    BroadcastData(settingsNotMade = true)
                }
                is ScanningState.ScanningClose -> {
                    BroadcastData(disconnection = true)
                }
                is ScanningState.ShowSettingFragment -> {
                    BroadcastData(settingsNotMade = true)
                }
                is ScanningState.NewConnection -> {
                    BroadcastData(isConnected = true)
                }
            }
        val json = Json.encodeToString(data)
        intent.putExtra("dataScanning", json)
        sendBroadcast(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when (it.action) {
                Constants.ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        startForegroundService()
                    } else {
                        startScanning()
                    }
                }
                Constants.ACTION_PAUSE_SERVICE -> {
                    pauseService()
                }
                Constants.ACTION_STOP_SERVICE -> {
                    killService()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {

        startScanning()

        isScanning.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        startForeground(Constants.NOTIFICATION_ID, curNotificationBuilder.build())
    }

    private fun startScanning() {

        if (btSocket?.isConnected == true) {
//            scanningData.postValue(ScanningData.NewConnection)
            _scanningEvent.value = ScanningData(ScanningState.NewConnection)
            return
        }

        scanningJob = lifecycleScope.launch(Dispatchers.IO) {
            val currMac = SharedPrefsManager.getInstance().getMac()
            if (currMac.isNotEmpty()
                && bluetoothAdapter?.isEnabled == true
            ) {
                isScanning.postValue(true)
                CoroutineScope(Dispatchers.IO).launch {

                    val device = bluetoothAdapter.getRemoteDevice(currMac)

                    btSocket = try {
                        device.createRfcommSocketToServiceRecord(Constants.SOCKET_UUID)
                    } catch (e: IOException) {
                        isScanning.postValue(false)
//                        scanningData.postValue(ScanningData.Error(e.message.toString()))
                        updateEvent(ScanningState.Error(e.message.toString()))
                        null
                    }
                    bluetoothAdapter.cancelDiscovery()

                    btSocket?.apply {
                        try {
                            connect()
//                            scanningData.postValue(ScanningData.NewConnection)
                            updateEvent(ScanningState.NewConnection)
                            val buffer = ByteArray(1024)
                            while (isScanning.value!!) {
                                try {
                                    val bytes = inputStream.read(buffer)
                                    val bc = String(buffer, 0, bytes)
//                                    scanningData.postValue(ScanningData.Barcode(bc))
                                    updateEvent(ScanningState.Barcode(bc))
                                } catch (e: IOException) {
                                    isScanning.postValue(false)
//                                    scanningData.postValue(ScanningData.ScanningClose)
                                    updateEvent(ScanningState.ScanningClose)
                                    killService()
                                    break
                                }
                            }
                        } catch (e: IOException) {
                            try {
                                close()
                                isScanning.postValue(false)
//                                scanningData.postValue(ScanningData.Error(e.message.toString()))
                                updateEvent(ScanningState.Error(e.message.toString()))
                                killService()
                            } catch (e2: IOException) {
                                isScanning.postValue(false)
//                                scanningData.postValue(ScanningData.Error(e2.message.toString()))
                                updateEvent(ScanningState.Error(e2.message.toString()))
                                killService()
                            }
                        }
                    }

                }
            } else {
//                scanningData.postValue(ScanningData.ShowSettingFragment)
                updateEvent(ScanningState.ShowSettingFragment)
                killService()
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun provideMainActivityPendingIntent() =
        PendingIntent.getActivity(
            App.INSTANCE,
            0,
            Intent(App.INSTANCE, MainActivity::class.java).also {
                it.action = Constants.ACTION_SHOW_SETTING_BLUETOOTH_FRAGMENT
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    private fun provideBaseNotificationBuilder() =
        NotificationCompat.Builder(App.INSTANCE, Constants.NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_barecode_scanner_24)
            .setContentTitle("Scanning")
            .setVibrate(null)
            .setDefaults(Notification.DEFAULT_SOUND)
            .setContentIntent(provideMainActivityPendingIntent())

    private fun updateEvent(scanningState: ScanningState) {
        _scanningEvent.value = ScanningData(scanningState)
    }

}