package com.ezzy.newsapp.repository

import com.ezzy.newsapp.api.RetrofitInstance
import com.ezzy.newsapp.database.ArticleDatabase

class NewsRepository (
    val database: ArticleDatabase
) {

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)
}