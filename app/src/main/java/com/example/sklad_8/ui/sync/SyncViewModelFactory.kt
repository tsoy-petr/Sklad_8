package com.example.sklad_8.ui.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sklad_8.data.repositores.SyncRepository

class SyncViewModelFactory(
    private val repository: SyncRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SyncViewModel(repository) as T
    }
}