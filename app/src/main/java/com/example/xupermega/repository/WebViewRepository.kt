package com.example.xupermega.repository

import android.webkit.WebView

interface WebViewRepository {
    fun initializeWebView(webView: WebView)
    fun loadUrl(url: String)
    fun clearSessionData()
    fun injectQuotaDetectionScript()
    fun setQuotaDetectionListener(listener: QuotaDetector.QuotaDetectionListener)
    fun destroy()
}