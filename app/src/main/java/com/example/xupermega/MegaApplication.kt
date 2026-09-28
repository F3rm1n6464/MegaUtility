package com.example.xupermega

import android.app.Application
import com.jakewharton.timber.Timber
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MegaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}