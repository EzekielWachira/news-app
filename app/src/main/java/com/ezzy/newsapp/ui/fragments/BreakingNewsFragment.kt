package com.ezzy.newsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ezzy.newsapp.R
import com.ezzy.newsapp.adapters.NewsAdapter
import com.ezzy.newsapp.ui.NewsActivity
import com.ezzy.newsapp.util.Resource
import com.ezzy.newsapp.viewmodel.NewsViewModel

private const val TAG = "BreakingNewsFragment"
class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsViewModel = (activity as NewsActivity).newsViewModel
        setUpRecyclerView()

        newsViewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let {
                        newsAdapter.differ.submitList(it.articles)
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

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        recyclerView = requireView().findViewById(R.id.rvBreakingNews)

        recyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun showProgressBar () {
        requireView().findViewById<ProgressBar>(R.id.paginationProgressBar)
            .visibility = View.VISIBLE
    }

    private fun hideProgressBar () {
        requireView().findViewById<ProgressBar>(R.id.paginationProgressBar)
            .visibility = View.GONE
    }

}