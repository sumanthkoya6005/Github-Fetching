package com.sumanth.testapplication.ui


import android.app.Activity
import android.app.SearchManager
import android.content.ContentValues.TAG
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sumanth.testapplication.adapter.DataListAdapter
import com.sumanth.testapplication.app.AppController
import com.sumanth.testapplication.interfaces.DataListener
import com.sumanth.testapplication.model.RepoList
import com.sumanth.testapplication.model.User
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_data.*


class DataFragment : Fragment() {
    lateinit var dataToMoviesActivity: GitHubActivity
    lateinit var dataListener: DataListener
    private var moviesList: ArrayList<User>? = null
    private var hashMap: HashMap<String, List<RepoList>>? = null
    private var repoCompleteList: ArrayList<ArrayList<RepoList>>? = null
    fun setDataSelectListener(dataListener: DataListener) {
        this.dataListener = dataListener
    }

    lateinit var adapter: DataListAdapter
    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            com.sumanth.testapplication.R.layout.fragment_data,
            container,
            false
        )
    }

    private fun verifyAvailableNetwork(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (verifyAvailableNetwork(view.context)) {
            loadData()
            progress_bar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            Toast.makeText(
                context,
                "Request failed. Check your internet connection",
                Toast.LENGTH_SHORT
            ).show()
        }
        adapter = DataListAdapter(mutableListOf(), ArrayList())
        val layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        adapter.setDataListener(object : DataListener {
            override fun onSelected(users: User?) {
                dataToMoviesActivity.onSelected(users)
                Toast.makeText(context, "You clicked: ${users?.login}", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        dataToMoviesActivity = activity as GitHubActivity
    }

    private fun callRepoListURL(movies: ArrayList<User>) {
        val appController = context?.let { AppController.create(it) }
        val usersService = appController?.userService
        val urlList = ArrayList<ArrayList<RepoList>>()
        hashMap = HashMap()
        for (item in movies) {
            val disposable = usersService?.reposForUser(item.login)
                ?.subscribeOn(appController.subscribeScheduler())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ userResponse ->
                    urlList.add(userResponse)
                    hashMap!![userResponse[0].owner.login] = userResponse
                    if (urlList.size == movies.size) {
                        for (moviesList in movies) {
                            var user = moviesList
                            user.repoList = hashMap!![user.login]
                        }
                        repoCompleteList = urlList
                        updateUserDataList()
                    }
                }, {
                    handleError(it)
                })
            disposable?.let { compositeDisposable.add(it) }
        }


    }

    private fun loadData() {
        val appController = context?.let { AppController.create(it) }
        val usersService = appController?.userService
        val disposable = usersService?.getUsers()
            ?.subscribeOn(appController.subscribeScheduler())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({ userResponse ->
                moviesList = userResponse
                callRepoListURL(movies = userResponse)
            }, {
                handleError(it)
                Toast.makeText(
                    context,
                    "Max Limit Completed,Please Switch to Other Ip Addresss",
                    Toast.LENGTH_LONG
                ).show()
            })
        disposable?.let { compositeDisposable.add(it) }
    }

    private fun updateUserDataList() {
        progress_bar.visibility = View.GONE
        recycler_view_layout.visibility = View.VISIBLE
        recyclerView.visibility = View.VISIBLE
        val searchManager = context?.getSystemService(Context.SEARCH_SERVICE) as SearchManager?
        val searchView = search as SearchView
        searchView.setSearchableInfo(searchManager?.getSearchableInfo(activity?.componentName))
        searchView.maxWidth = Integer.MAX_VALUE
        // listenng to search query text change
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // filter recycler view when query submitted
                adapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                // filter recycler view when text is changed
                adapter.filter.filter(query)
                return false
            }
        })
        adapter.update(moviesList as ArrayList<User>, repoCompleteList)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun handleError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)
    }

}
