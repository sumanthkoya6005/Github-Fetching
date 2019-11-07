package com.sumanth.testapplication.network


import com.sumanth.testapplication.model.RepoList
import com.sumanth.testapplication.model.User
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path


interface GithubService {

    @GET("users/{user}/repos")
    fun reposForUser(@Path("user") user: String): Observable<ArrayList<RepoList>>

    @GET("/users")
    fun getUsers(): Observable<ArrayList<User>>

}
