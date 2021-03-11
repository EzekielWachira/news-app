package com.ezzy.newsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ezzy.newsapp.R
import com.ezzy.newsapp.adapters.NewsAdapter
import com.ezzy.newsapp.ui.NewsActivity
import com.ezzy.newsapp.util.Constants
import com.ezzy.newsapp.util.Constants.Companion.QUERY_PAGE_SIZE
import com.ezzy.newsapp.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.ezzy.newsapp.util.Resource
import com.ezzy.newsapp.viewmodel.NewsViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "SearchNewsFragment"
class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {

    private lateinit var newsViewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var recyclerView: RecyclerView

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsViewModel = (activity as NewsActivity).newsViewModel
        setUpRecyclerview()

        newsAdapter.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                    R.id.action_searchNewsFragment_to_articleFragment,
                    bundle
            )
        }

        var job: Job? = null
        requireView().findViewById<EditText>(R.id.etSearch)
                .addTextChangedListener {
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                it?.let {
                    if (it.toString().isNotEmpty()){
                        newsViewModel.searchNews(it.toString())
                    }
                }
            }
        }

        newsViewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let {
                        newsAdapter.differ.submitList(it.articles.toList())
                        val totalPages = it.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = newsViewModel.searchNewsPage == totalPages
                        if (isLastPage){
                            recyclerView.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let {
                        Log.d(TAG, "onViewCreated: $it")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun setUpRecyclerview(){
        newsAdapter = NewsAdapter()
        recyclerView = requireView().findViewById(R.id.rvSearchNews)
        recyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }
    }

    val scrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerVieww: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerVieww.layoutManager as LinearLayoutManager
            val firstItemVisiblePosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastitem = firstItemVisiblePosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstItemVisiblePosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastitem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate){
                newsViewModel.searchNews(
                        requireView().findViewById<EditText>(R.id.etSearch).text.toString()
                )
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }
    }

    private fun showProgressBar(){
        requireView().findViewById<ProgressBar>(R.id.paginationProgress)
                .visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideProgressBar(){
        requireView().findViewById<ProgressBar>(R.id.paginationProgress)
                .visibility = View.INVISIBLE
        isLoading = false
    }

}