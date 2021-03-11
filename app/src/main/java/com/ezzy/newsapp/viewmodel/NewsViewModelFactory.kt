package com.ezzy.newsapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ezzy.newsapp.repository.NewsRepository
import java.lang.IllegalArgumentException

class NewsViewModelFactory(
    val app: Application,
    val newsRepository: NewsRepository
) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewsViewModel(app, newsRepository) as T
        }
        throw IllegalArgumentException("View model not found")
    }
}