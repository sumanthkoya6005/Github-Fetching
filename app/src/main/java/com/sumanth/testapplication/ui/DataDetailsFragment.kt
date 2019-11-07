package com.sumanth.testapplication.ui


import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.sumanth.testapplication.R
import com.sumanth.testapplication.adapter.DataRepoListAdapter
import com.sumanth.testapplication.interfaces.DataListener
import com.sumanth.testapplication.model.User
import kotlinx.android.synthetic.main.fragment_data_details.*


class DataDetailsFragment : Fragment() {

    lateinit var adapter: DataRepoListAdapter
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getSerializable(REPO_BEAN) as User
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_data_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUI(view)
    }


    private fun loadImage(view: ImageView, imageUrl: String) {
        val mDefaultBackground = view.context.resources.getDrawable(R.drawable.ic_menu_gallery)
        username.text = String.format(
            context!!.getString(R.string.user_title),
            user.repoList?.get(0)?.owner?.login
        )
        email.text = String.format(context!!.getString(R.string.user_email), NOT_AVAILABLE)
        location.text = String.format(context!!.getString(R.string.location), NOT_AVAILABLE)
        joinDate.text = String.format(context!!.getString(R.string.date), NOT_AVAILABLE)
        followers.text = String.format(context!!.getString(R.string.followers), "25")
        following.text = String.format(context!!.getString(R.string.following), "30")
        bio.text =
            String.format(context!!.getString(R.string.bio), user.repoList?.get(0)?.owner?.login)
        Glide.with(view.context)
            ?.load(imageUrl)
            ?.centerCrop()
            ?.error(mDefaultBackground)?.into(view)
    }

    private fun loadUI(view: View) {
        adapter = DataRepoListAdapter(mutableListOf())
        val layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        adapter.setDataListener(object : DataListener {
            override fun onSelected(users: User?) {
            }
        })
        loadImage(inside_movie_image, user.avatar_url)
        updateUserDataList()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: User?): DataDetailsFragment =
            DataDetailsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(REPO_BEAN, param1)
                }
            }

        var REPO_BEAN: String = " Repo bean"
        var NOT_AVAILABLE: String = "Not Available"
    }

    private fun updateUserDataList() {
        recyclerView.visibility = View.VISIBLE
        val searchManager = context?.getSystemService(Context.SEARCH_SERVICE) as SearchManager?
        val searchView = search_view as SearchView
        searchView.setSearchableInfo(searchManager?.getSearchableInfo(activity?.componentName))
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                adapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                adapter.filter.filter(query)
                return false
            }
        })
        adapter.update(user.repoList!!)
    }
}
