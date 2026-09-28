package com.example.xupermega.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.xupermega.utils.TorManager
import com.jakewharton.timber.Timber

class TorService : Service() {

    private var torManager: TorManager? = null

    override fun onCreate() {
        super.onCreate()
        torManager = TorManager(this)
        Timber.d("TorService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("TorService started")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("TorService destroyed")
    }

    fun requestNewIdentity(): Boolean {
        return torManager?.requestNewIdentity() == true
    }

    fun isOrbotRunning(): Boolean {
        return torManager?.isOrbotInstalled() == true
    }
}