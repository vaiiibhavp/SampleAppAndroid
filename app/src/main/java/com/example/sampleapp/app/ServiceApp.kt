package com.example.sampleapp.app

import android.app.Application
import android.content.Context
import com.example.sampleapp.healthManager.HealthConnectManager


class ServiceApp : Application() {

    companion object {
        var context: Context? = null
    }
    val healthConnectManager by lazy {
        HealthConnectManager(this)
    }

    override fun onCreate() {
        super.onCreate()
        context = this



    }
}