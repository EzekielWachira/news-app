package com.ezzy.newsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ezzy.newsapp.R
import com.ezzy.newsapp.adapters.NewsAdapter
import com.ezzy.newsapp.ui.NewsActivity
import com.ezzy.newsapp.util.Constants.Companion.QUERY_PAGE_SIZE
import com.ezzy.newsapp.util.RecyclerViewItemDecorator
import com.ezzy.newsapp.util.Resource
import com.ezzy.newsapp.viewmodel.NewsViewModel

private const val TAG = "BreakingNewsFragment"
class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var recyclerView: RecyclerView

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsViewModel = (activity as NewsActivity).newsViewModel
        setUpRecyclerView()

        newsAdapter.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )

        }

        newsViewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let {
                        newsAdapter.differ.submitList(it.articles.toList())
                        val totalPages = it.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = newsViewModel.breakingNewsPage == totalPages
                        if (isLastPage){
                            recyclerView.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let {
                        Toast.makeText(activity, "An error occurred $it", Toast.LENGTH_SHORT).apply {
                            show()
                        }
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        recyclerView = requireView().findViewById(R.id.rvBreakingNews)

        recyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
            addItemDecoration(RecyclerViewItemDecorator(10))
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
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastitem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate){
                newsViewModel.getBreakingNews("us")
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


    private fun showProgressBar () {
        requireView().findViewById<ProgressBar>(R.id.paginationProgressBar)
            .visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideProgressBar () {
        requireView().findViewById<ProgressBar>(R.id.paginationProgressBar)
            .visibility = View.GONE
        isLoading = false
    }

}