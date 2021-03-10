package com.ezzy.newsapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.ezzy.newsapp.R
import com.ezzy.newsapp.ui.NewsActivity
import com.ezzy.newsapp.viewmodel.NewsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class ArticleFragment : Fragment(R.layout.fragment_article) {

    private lateinit var newsViewModel: NewsViewModel
    val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsViewModel = (activity as NewsActivity).newsViewModel

        val article = args.article
        requireView().findViewById<WebView>(R.id.webView).apply {
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }
        requireView().findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            newsViewModel.saveArticle(article)
            Snackbar.make(view, "Article Saved", Snackbar.LENGTH_SHORT).show()
        }

    }

}