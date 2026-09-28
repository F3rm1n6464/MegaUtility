package com.example.xupermega.utils

import android.content.Context
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.example.xupermega.R
import com.jakewharton.timber.Timber

class WebViewManager(private val context: Context) {

    private var quotaDetectionListener: QuotaDetector.QuotaDetectionListener? = null

    fun configureWebView(webView: WebView) {
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        settings.userAgentString = "Mozilla/5.0 (Linux; Android 10; SM-G973F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Timber.d("Page loaded: $url")
                injectQuotaDetectionScript(view)
            }

            override fun onReceivedError(view: WebView?, request: android.webkit.WebResourceRequest?, error: android.webkit.WebResourceError?) {
                super.onReceivedError(view, request, error)
                Timber.e("WebView error: ${error?.description}")
                quotaDetectionListener?.onError("Network error: ${error?.description}")
            }

            override fun onReceivedHttpError(view: WebView?, request: android.webkit.WebResourceRequest?, errorResponse: android.webkit.WebResourceResponse?) {
                super.onReceivedHttpError(view, request, errorResponse)
                if (errorResponse?.statusCode == 429) {
                    quotaDetectionListener?.onQuotaDetected("HTTP 429 Too Many Requests")
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
            }
        }

        webView.addJavascriptInterface(QuotaDetectionInterface(), "AndroidInterface")
    }

    fun loadUrl(webView: WebView, url: String) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            webView.loadUrl("https://$url")
        } else {
            webView.loadUrl(url)
        }
    }

    fun clearAllSessionData(webView: WebView) {
        try {
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookies(null)
            cookieManager.flush()

            webView.clearCache(true)
            webView.clearHistory()
            webView.clearFormData()
            webView.clearSslPreferences()

            android.webkit.WebStorage.getInstance().deleteAllData()

            Timber.d("Session data cleared successfully")
        } catch (e: Exception) {
            Timber.e(e, "Failed to clear session data")
        }
    }

    fun injectQuotaDetectionScript(webView: WebView?) {
        webView?.evaluateJavascript(QuotaDetector.DETECTION_SCRIPT, null)
    }

    fun setQuotaDetectionListener(listener: QuotaDetector.QuotaDetectionListener) {
        this.quotaDetectionListener = listener
    }

    fun destroyWebView(webView: WebView) {
        webView.clearHistory()
        webView.clearCache(true)
        webView.loadUrl("about:blank")
        webView.removeJavascriptInterface("AndroidInterface")
        webView.webViewClient = null
        webView.webChromeClient = null
        webView.destroy()
    }

    inner class QuotaDetectionInterface {
        @android.webkit.JavascriptInterface
        fun onQuotaDetected(detectedString: String) {
            quotaDetectionListener?.onQuotaDetected(detectedString)
        }

        @android.webkit.JavascriptInterface
        fun onAdLoaded() {
            Timber.d("Ad loaded")
        }

        @android.webkit.JavascriptInterface
        fun onAdClicked() {
            Timber.d("Ad clicked")
        }

        @android.webkit.JavascriptInterface
        fun onError(error: String) {
            quotaDetectionListener?.onError(error)
        }
    }
}