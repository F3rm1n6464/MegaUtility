package com.example.xupermega.utils

import android.webkit.WebView
import com.jakewharton.timber.Timber

class QuotaDetector {

    companion object {
        const val DETECTION_SCRIPT = """
            (function() {
                var quotaStrings = [
                    'Quota Exceeded',
                    'Rate Limited',
                    'Too Many Requests',
                    '429',
                    'quota exceeded',
                    'rate limit',
                    'limit reached',
                    'exceeded quota',
                    'API quota',
                    'request limit',
                    'throttled',
                    'capacity exceeded'
                ];
                var html = document.documentElement.outerHTML.toLowerCase();
                for (var i = 0; i < quotaStrings.length; i++) {
                    if (html.includes(quotaStrings[i].toLowerCase())) {
                        if (window.AndroidInterface) {
                            window.AndroidInterface.onQuotaDetected(quotaStrings[i]);
                        }
                        return;
                    }
                }
            })();
        """.trimIndent()
    }

    interface QuotaDetectionListener {
        fun onQuotaDetected(detectedString: String)
        fun onError(error: String)
    }

    private var listener: QuotaDetectionListener? = null
    private var webView: WebView? = null

    fun setListener(listener: QuotaDetectionListener) {
        this.listener = listener
    }

    fun setWebView(webView: WebView) {
        this.webView = webView
    }

    fun injectDetectionScript(webView: WebView?) {
        webView?.evaluateJavascript(DETECTION_SCRIPT, null)
    }

    fun injectDetectionScript() {
        injectDetectionScript(webView)
    }

    fun checkForQuotaManually(htmlContent: String): String? {
        val quotaStrings = listOf(
            "Quota Exceeded",
            "Rate Limited",
            "Too Many Requests",
            "429",
            "quota exceeded",
            "rate limit",
            "limit reached",
            "exceeded quota",
            "API quota",
            "request limit",
            "throttled",
            "capacity exceeded"
        )

        val lowerHtml = htmlContent.lowercase()
        for (quotaString in quotaStrings) {
            if (lowerHtml.contains(quotaString.lowercase())) {
                return quotaString
            }
        }
        return null
    }

    fun setCustomQuotaStrings(strings: List<String>) {
    }

    inner class QuotaDetectionInterface {
        @android.webkit.JavascriptInterface
        fun onQuotaDetected(detectedString: String) {
            Timber.d("Quota detected via JS: $detectedString")
            listener?.onQuotaDetected(detectedString)
        }

        @android.webkit.JavascriptInterface
        fun onError(error: String) {
            Timber.e("Quota detection error: $error")
            listener?.onError(error)
        }
    }
}