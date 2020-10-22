package com.lzx.userfulktext

import android.app.Application
import android.content.Context

class App : Application() {

    companion object {
        @JvmStatic
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}