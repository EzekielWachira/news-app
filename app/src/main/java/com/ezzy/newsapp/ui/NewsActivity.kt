package com.ezzy.newsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ezzy.newsapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class NewsActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var newsNavHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        newsNavHostFragment = findViewById(R.id.newsNavHostFragment)

        bottomNavigationView.setupWithNavController(
            newsNavHostFragment.findNavController()
        )

    }
}