package com.example.xupermega.repository

import android.webkit.WebView
import com.example.xupermega.utils.QuotaDetector
import com.example.xupermega.utils.WebViewManager

class WebViewRepositoryImpl(
    private val webViewManager: WebViewManager,
    private val quotaDetector: QuotaDetector
) : WebViewRepository {

    private var webView: WebView? = null

    override fun initializeWebView(webView: WebView) {
        this.webView = webView
        webViewManager.configureWebView(webView)
        quotaDetector.setWebView(webView)
    }

    override fun loadUrl(url: String) {
        webView?.let { webViewManager.loadUrl(it, url) }
    }

    override fun clearSessionData() {
        webView?.let { webViewManager.clearAllSessionData(it) }
    }

    override fun injectQuotaDetectionScript() {
        webView?.let { quotaDetector.injectDetectionScript(it) }
    }

    override fun setQuotaDetectionListener(listener: QuotaDetector.QuotaDetectionListener) {
        quotaDetector.setListener(listener)
    }

    override fun destroy() {
        webView?.let { webViewManager.destroyWebView(it) }
        webView = null
    }
}