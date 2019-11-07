package com.sumanth.testapplication.network

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.sumanth.testapplication.utils.Constant.Companion.BASE_URL
import retrofit2.Retrofit

import retrofit2.converter.gson.GsonConverterFactory

object ApiFactory {
    fun create(): GithubService {
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        return retrofit.create(GithubService::class.java)
    }

}
