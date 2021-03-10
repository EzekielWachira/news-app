package com.ezzy.newsapp.repository

import com.ezzy.newsapp.api.RetrofitInstance
import com.ezzy.newsapp.database.ArticleDatabase
import com.ezzy.newsapp.models.Article

class NewsRepository (
    val database: ArticleDatabase
) {

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
            RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    suspend fun upsert(article : Article) = database.getArticleDao().upsert(article)

    fun getSavedNews() = database.getArticleDao().getArticles()

    suspend fun deleteArticle(article: Article) = database.getArticleDao().deleteArticle(article)
}