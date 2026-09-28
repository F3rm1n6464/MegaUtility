package com.example.xupermega.utils

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.xupermega.model.LogEntry
import com.example.xupermega.model.LogLevel
import com.jakewharton.timber.Timber
import java.util.concurrent.ConcurrentLinkedQueue

class LogManager(private val context: Context) {

    val applicationContext: Context = context

    private val logQueue = ConcurrentLinkedQueue<LogEntry>()
    private val maxLogs = 500
    val logsLiveData = MutableLiveData<List<LogEntry>>()

    fun log(level: LogLevel, message: String, throwable: Throwable? = null) {
        val entry = LogEntry(level = level, message = message, throwable = throwable)
        logQueue.add(entry)

        while (logQueue.size > maxLogs) {
            logQueue.poll()
        }

        val logList = logQueue.toList()
        logsLiveData.postValue(logList)

        when (level) {
            LogLevel.INFO -> Timber.d(message)
            LogLevel.SUCCESS -> Timber.d(message)
            LogLevel.WARNING -> Timber.w(throwable, message)
            LogLevel.ERROR -> Timber.e(throwable, message)
        }
    }

    fun info(message: String) {
        log(LogLevel.INFO, message)
    }

    fun success(message: String) {
        log(LogLevel.SUCCESS, message)
    }

    fun warning(message: String, throwable: Throwable? = null) {
        log(LogLevel.WARNING, message, throwable)
    }

    fun error(message: String, throwable: Throwable? = null) {
        log(LogLevel.ERROR, message, throwable)
    }

    fun clearLogs() {
        logQueue.clear()
        logsLiveData.postValue(emptyList())
    }

    fun getLogs(): List<LogEntry> {
        return logQueue.toList()
    }

    fun getStringResource(@Suppress("UNUSED_PARAMETER") resId: Int): String {
        return context.getString(resId)
    }
}