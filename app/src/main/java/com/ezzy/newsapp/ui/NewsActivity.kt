package com.ezzy.newsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ezzy.newsapp.R
import com.ezzy.newsapp.database.ArticleDatabase
import com.ezzy.newsapp.repository.NewsRepository
import com.ezzy.newsapp.viewmodel.NewsViewModel
import com.ezzy.newsapp.viewmodel.NewsViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class NewsActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var newsNavHostFragment: FragmentContainerView
    lateinit var newsViewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        newsNavHostFragment = findViewById(R.id.newsNavHostFragment)

        bottomNavigationView.setupWithNavController(
            newsNavHostFragment.findNavController()
        )

        val newsRepository = NewsRepository(ArticleDatabase(this))
        val newsViewModelFactory = NewsViewModelFactory(application,newsRepository)
        newsViewModel = ViewModelProvider(this, newsViewModelFactory)
            .get(NewsViewModel::class.java)


    }
}