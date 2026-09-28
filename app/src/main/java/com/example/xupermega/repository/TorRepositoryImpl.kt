package com.example.xupermega.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.example.xupermega.utils.TorManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TorRepositoryImpl(private val torManager: TorManager) : TorRepository {

    override suspend fun requestNewIdentity(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                torManager.requestNewIdentity()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun isOrbotInstalled(): Boolean {
        return withContext(Dispatchers.IO) {
            torManager.isOrbotInstalled()
        }
    }

    override suspend fun checkTorConnection(): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val connected = torManager.checkTorConnection()
                Result.success(connected)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override fun getOrbotPackageName(): String {
        return TorManager.ORBOT_PACKAGE_NAME
    }
}