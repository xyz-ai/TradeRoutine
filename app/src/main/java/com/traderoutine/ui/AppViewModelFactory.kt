package com.traderoutine.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.traderoutine.data.CheckInRepository

class AppViewModelFactory(private val repository: CheckInRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
