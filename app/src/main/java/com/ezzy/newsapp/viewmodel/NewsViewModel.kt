package com.ezzy.newsapp.viewmodel

import androidx.lifecycle.ViewModel
import com.ezzy.newsapp.repository.NewsRepository

class NewsViewModel(
    val newsRepository: NewsRepository
) : ViewModel() {

}