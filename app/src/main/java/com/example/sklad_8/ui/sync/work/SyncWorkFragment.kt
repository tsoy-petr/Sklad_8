package com.example.sklad_8.ui.sync.work

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.sklad_8.R
import com.example.sklad_8.data.worker.SyncWorker
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

class SyncWorkFragment : Fragment(R.layout.fragment_sync_work) {

    private lateinit var tv_message_sync: TextView
    private lateinit var tv_message_sync_inner: TextView
    private lateinit var btnStart: Button
    private lateinit var btnCurrStatus: Button
    private lateinit var btnStopSync: Button
    private val workManager by lazy { WorkManager.getInstance(requireContext().applicationContext) }

    @SuppressLint("EnqueueWork")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_message_sync = view.findViewById(R.id.tv_message_sync)
        tv_message_sync_inner = view.findViewById(R.id.tv_inner)
        btnStopSync = view.findViewById(R.id.btn_stop_sync)
        btnStopSync.setOnClickListener {
            workManager.cancelUniqueWork("sync")
        }
        btnCurrStatus = view.findViewById(R.id.btn_cur_status)
        btnCurrStatus.setOnClickListener {
            workManager.getWorkInfosForUniqueWork("sync").apply {
                this.get().forEach {
                    Snackbar.make(
                        requireView(),
                        it.toString(), Snackbar.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
        btnStart = view.findViewById(R.id.btn_start_sync_work)
        btnStart.setOnClickListener {

            val request = OneTimeWorkRequestBuilder<SyncWorker>()
                .build()

            workManager.beginUniqueWork(
                "sync",
                ExistingWorkPolicy.KEEP,
                request
            )
                .enqueue()
        }
//        val status = workManager.getWorkInfosForUniqueWorkLiveData("sync")
//        status.observe(viewLifecycleOwner, {
//            val info = ""
//            it.forEach { workInfo ->
//                info.plus(" -> ${workInfo.state}")
//            }
//            tv_message_sync.text = info
//        })

        lifecycleScope.launchWhenResumed {
            SyncWorker.syncStatus.collect {
                tv_message_sync_inner.text = it.toString()
            }
//            SyncWorker.syncStatus.c(viewLifecycleOwner, Observer {
//                tv_message_sync_inner.text = it.toString()
//            })
        }
    }

}