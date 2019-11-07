package com.sumanth.testapplication.interfaces

import com.sumanth.testapplication.model.User

interface DataListener {
    fun onSelected(users: User?)
}
