package com.example.myapp.presentation

import androidx.lifecycle.ViewModel
import com.example.myapp.data.AppRepository
import com.example.myapp.data.ListItem

class AppViewModel : ViewModel() {
    private val repository = AppRepository()

    val screenItems: List<ListItem> = repository.getScreenContent()
}