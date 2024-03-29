package com.sumanth.testapplication.app

import android.app.Application
import android.content.Context


import com.sumanth.testapplication.network.ApiFactory
import com.sumanth.testapplication.network.GithubService

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class AppController : Application() {

    var userService: GithubService? = null
        get() {
            if (field == null) {
                userService = ApiFactory.create()
            }

            return field
        }

    private var scheduler: Scheduler? = null

    fun subscribeScheduler(): Scheduler? {
        if (scheduler == null) {
            scheduler = Schedulers.io()
        }

        return scheduler
    }

    fun setScheduler(scheduler: Scheduler) {
        this.scheduler = scheduler
    }

    companion object {

        private operator fun get(context: Context): AppController {
            return context.applicationContext as AppController
        }

        fun create(context: Context): AppController {
            return AppController[context]
        }
    }

}
