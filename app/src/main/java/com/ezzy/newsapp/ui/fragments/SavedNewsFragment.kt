package com.ezzy.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ezzy.newsapp.R
import com.ezzy.newsapp.adapters.NewsAdapter
import com.ezzy.newsapp.ui.NewsActivity
import com.ezzy.newsapp.util.RecyclerViewItemDecorator
import com.ezzy.newsapp.viewmodel.NewsViewModel
import com.google.android.material.snackbar.Snackbar

private const val TAG = "SavedNewsFragment"
class SavedNewsFragment : Fragment() {

    private lateinit var newsViewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_saved_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsViewModel = (activity as NewsActivity).newsViewModel
        setUpRecyclerview()
        newsAdapter.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                    R.id.action_savedNewsFragment_to_articleFragment,
                    bundle
            )
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                newsViewModel.deleteArticle(article)
                Snackbar.make(
                        view,
                        "Article deleted",
                        Snackbar.LENGTH_LONG
                ).apply {
                    setAction("Undo") {
                        newsViewModel.saveArticle(article)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(recyclerView)
        }

        newsViewModel.getSavedArticles().observe(viewLifecycleOwner, Observer {
            newsAdapter.differ.submitList(it)
        })

    }

    private fun setUpRecyclerview(){
        newsAdapter = NewsAdapter()
        recyclerView = requireView().findViewById(R.id.rvSavedNews)
        recyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(RecyclerViewItemDecorator(10))
        }
    }


}