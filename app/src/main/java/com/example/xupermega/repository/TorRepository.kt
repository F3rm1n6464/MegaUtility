package com.example.xupermega.repository

import com.example.xupermega.model.LogEntry

interface TorRepository {
    suspend fun requestNewIdentity(): Result<Unit>
    suspend fun isOrbotInstalled(): Boolean
    suspend fun checkTorConnection(): Result<Boolean>
    fun getOrbotPackageName(): String
}