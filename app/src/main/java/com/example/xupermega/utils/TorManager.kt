package com.example.xupermega.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.jakewharton.timber.Timber

class TorManager(private val context: Context) {

    companion object {
        const val ORBOT_PACKAGE_NAME = "org.torproject.android"
        const val NEW_IDENTITY_ACTION = "org.torproject.android.intent.action.NEW_IDENTITY"
        const val START_TOR_ACTION = "org.torproject.android.intent.action.START_TOR"
        const val STOP_TOR_ACTION = "org.torproject.android.intent.action.STOP_TOR"
        const val TOR_STATUS_ACTION = "org.torproject.android.intent.action.TOR_STATUS"
    }

    fun requestNewIdentity(): Boolean {
        return try {
            val intent = Intent(NEW_IDENTITY_ACTION)
            intent.setPackage(ORBOT_PACKAGE_NAME)
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            context.sendBroadcast(intent)
            Timber.d("New Identity intent sent to Orbot")
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to send New Identity intent")
            false
        }
    }

    fun isOrbotInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo(ORBOT_PACKAGE_NAME, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun startTor(): Boolean {
        return try {
            val intent = Intent(START_TOR_ACTION)
            intent.setPackage(ORBOT_PACKAGE_NAME)
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            context.sendBroadcast(intent)
            Timber.d("Start Tor intent sent to Orbot")
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to send Start Tor intent")
            false
        }
    }

    fun stopTor(): Boolean {
        return try {
            val intent = Intent(STOP_TOR_ACTION)
            intent.setPackage(ORBOT_PACKAGE_NAME)
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            context.sendBroadcast(intent)
            Timber.d("Stop Tor intent sent to Orbot")
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to send Stop Tor intent")
            false
        }
    }

    fun checkTorConnection(): Boolean {
        return isOrbotInstalled()
    }

    fun openOrbotSettings() {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", ORBOT_PACKAGE_NAME, null)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun installOrbot() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$ORBOT_PACKAGE_NAME"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$ORBOT_PACKAGE_NAME"))
            webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(webIntent)
        }
    }
}