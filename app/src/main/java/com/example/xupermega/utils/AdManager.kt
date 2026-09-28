package com.example.xupermega.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.view.Window
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.example.xupermega.R
import com.example.xupermega.model.AdConfig
import com.jakewharton.timber.Timber

class AdManager(private val context: Context) {

    private var adDialog: Dialog? = null
    private var adWebView: WebView? = null
    private var adLoadListener: AdLoadListener? = null

    interface AdLoadListener {
        fun onAdLoaded()
        fun onAdFailed(error: String)
        fun onAdClosed()
        fun onAdClicked()
    }

    fun setAdLoadListener(listener: AdLoadListener) {
        this.adLoadListener = listener
    }

    fun loadAndShowInterstitialAd(adZoneId: String = AdConfig.AD_ZONE_ID) {
        if (context is Activity) {
            (context as Activity).runOnUiThread {
                createAndShowAdDialog(adZoneId)
            }
        }
    }

    private fun createAndShowAdDialog(adZoneId: String) {
        adDialog = Dialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.layout_webview_ads)
            window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            window?.setBackgroundDrawableResource(android.R.color.black)
            setCancelable(true)
            setCanceledOnTouchOutside(false)
        }

        adWebView = adDialog?.findViewById(R.id.wvAdContainer)
        val progressBar = adDialog?.findViewById<ProgressBar>(R.id.pbAdLoading)

        adWebView?.let { webView ->
            val settings = webView.settings
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.databaseEnabled = true
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            settings.userAgentString = "Mozilla/5.0 (Linux; Android 10; SM-G973F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"

            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    progressBar?.visibility = android.view.View.GONE
                    webView.visibility = android.view.View.VISIBLE
                    adLoadListener?.onAdLoaded()
                    Timber.d("A-Ads interstitial loaded")
                }

                override fun onReceivedError(view: WebView?, request: android.webkit.WebResourceRequest?, error: android.webkit.WebResourceError?) {
                    super.onReceivedError(view, request, error)
                    progressBar?.visibility = android.view.View.GONE
                    adLoadListener?.onAdFailed(error?.description.toString() ?: "Unknown error")
                    Timber.e("A-Ads load error: ${error?.description}")
                }
            }

            webView.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                }
            }

            webView.addJavascriptInterface(AdJavaScriptInterface(), "AndroidInterface")

            val htmlContent = AdConfig.getInterstitialHtml(adZoneId)
            webView.loadDataWithBaseURL(AdConfig.AADS_BASE_URL, htmlContent, "text/html", "UTF-8", null)
        }

        adDialog?.setOnDismissListener {
            adLoadListener?.onAdClosed()
            destroyAdWebView()
        }

        adDialog?.show()
    }

    fun dismissAd() {
        adDialog?.dismiss()
        adDialog = null
    }

    private fun destroyAdWebView() {
        adWebView?.let {
            it.clearHistory()
            it.clearCache(true)
            it.loadUrl("about:blank")
            it.removeJavascriptInterface("AndroidInterface")
            it.webViewClient = null
            it.webChromeClient = null
            it.destroy()
        }
        adWebView = null
    }

    fun isAdShowing(): Boolean {
        return adDialog?.isShowing == true
    }

    inner class AdJavaScriptInterface {
        @android.webkit.JavascriptInterface
        fun onAdLoaded() {
            adLoadListener?.onAdLoaded()
        }

        @android.webkit.JavascriptInterface
        fun onAdClicked() {
            adLoadListener?.onAdClicked()
        }

        @android.webkit.JavascriptInterface
        fun onError(error: String) {
            adLoadListener?.onAdFailed(error)
        }
    }
}