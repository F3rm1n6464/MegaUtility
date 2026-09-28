package com.example.xupermega.model

import java.io.Serializable

enum class LogLevel {
    INFO,
    SUCCESS,
    WARNING,
    ERROR
}

data class LogEntry(
    val timestamp: Long = System.currentTimeMillis(),
    val level: LogLevel = LogLevel.INFO,
    val message: String,
    val throwable: Throwable? = null
) : Serializable {

    fun getFormattedMessage(): String {
        val time = android.text.format.DateFormat.format("HH:mm:ss", timestamp)
        val prefix = when (level) {
            LogLevel.INFO -> "[+]"
            LogLevel.SUCCESS -> "[+]"
            LogLevel.WARNING -> "[!]"
            LogLevel.ERROR -> "[!]"
        }
        return "$time $prefix $message"
    }

    fun getColorRes(): Int {
        return when (level) {
            LogLevel.INFO -> com.example.xupermega.R.color.log_text_info
            LogLevel.SUCCESS -> com.example.xupermega.R.color.log_text_success
            LogLevel.WARNING -> com.example.xupermega.R.color.log_text_warning
            LogLevel.ERROR -> com.example.xupermega.R.color.log_text_error
        }
    }
}