package com.example.xupermega.ui.main

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xupermega.model.LogEntry
import com.example.xupermega.model.LogLevel
import com.example.xupermega.repository.TorRepository
import com.example.xupermega.repository.WebViewRepository
import com.example.xupermega.utils.AdManager
import com.example.xupermega.utils.LogManager
import com.example.xupermega.utils.QuotaDetector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val torRepository: TorRepository,
    private val webViewRepository: WebViewRepository,
    private val adManager: AdManager,
    private val logManager: LogManager
) : ViewModel() {

    private val _urlInput = MutableLiveData<String>()
    val urlInput: LiveData<String> = _urlInput

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRotating = MutableLiveData<Boolean>(false)
    val isRotating: LiveData<Boolean> = _isRotating

    private val _logs = MutableLiveData<List<LogEntry>>()
    val logs: LiveData<List<LogEntry>> = _logs

    private val _isAdLoading = MutableLiveData<Boolean>(false)
    val isAdLoading: LiveData<Boolean> = _isAdLoading

    private val _torConnected = MutableLiveData<Boolean>(false)
    val torConnected: LiveData<Boolean> = _torConnected

    private var currentUrl: String? = null
    private var isProcessing = false
    private var pendingAction: PendingAction? = null

    private enum class PendingAction {
        START_DOWNLOAD,
        ROTATE_IP
    }

    init {
        observeLogs()
        setupAdListener()
        setupQuotaListener()
        checkTorStatus()
    }

    private fun observeLogs() {
        logManager.logsLiveData.observeForever { logs ->
            _logs.value = logs
        }
    }

    private fun setupAdListener() {
        adManager.setAdLoadListener(object : AdManager.AdLoadListener {
            override fun onAdLoaded() {
                _isAdLoading.postValue(false)
                logManager.info("Ad loaded successfully")
                when (pendingAction) {
                    PendingAction.START_DOWNLOAD -> {
                        if (currentUrl != null) {
                            loadUrlInWebView(currentUrl!!)
                        }
                    }
                    PendingAction.ROTATE_IP -> {
                        // Ad loaded for rotate IP, wait for ad to be closed
                    }
                    null -> {}
                }
            }

            override fun onAdFailed(error: String) {
                _isAdLoading.postValue(false)
                logManager.warning("Ad failed to load: $error")
                when (pendingAction) {
                    PendingAction.START_DOWNLOAD -> {
                        _isLoading.postValue(false)
                        isProcessing = false
                    }
                    PendingAction.ROTATE_IP -> {
                        _isRotating.postValue(false)
                    }
                    null -> {}
                }
                pendingAction = null
            }

            override fun onAdClosed() {
                _isAdLoading.postValue(false)
                logManager.info("Ad closed")
                when (pendingAction) {
                    PendingAction.START_DOWNLOAD -> {
                        // Already handled in onAdLoaded for start download
                    }
                    PendingAction.ROTATE_IP -> {
                        rotateIpAddress()
                    }
                    null -> {}
                }
                pendingAction = null
            }

            override fun onAdClicked() {
                logManager.info("Ad clicked")
            }
        })
    }

    private fun setupQuotaListener() {
        webViewRepository.setQuotaDetectionListener(object : QuotaDetector.QuotaDetectionListener {
            override fun onQuotaDetected(detectedString: String) {
                logManager.warning(context.getString(R.string.log_quota_exceeded))
                logManager.warning("Detected quota string: $detectedString")
                _isLoading.postValue(false)
            }

            override fun onError(error: String) {
                logManager.error("Quota detection error: $error")
            }
        })
    }

    private fun checkTorStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            val installed = torRepository.isOrbotInstalled()
            _torConnected.postValue(installed)
            if (installed) {
                logManager.success(context.getString(R.string.log_tor_connected))
            } else {
                logManager.warning(context.getString(R.string.toast_orbot_not_installed))
            }
        }
    }

    fun onUrlChanged(url: String) {
        _urlInput.value = url
    }

    fun onStartDownload() {
        val url = _urlInput.value?.trim()
        if (url.isNullOrEmpty()) {
            logManager.error(context.getString(R.string.toast_enter_url))
            return
        }

        if (!isValidUrl(url)) {
            logManager.error("Invalid URL format")
            return
        }

        if (isProcessing) {
            logManager.warning("Already processing a request")
            return
        }

        isProcessing = true
        currentUrl = url
        pendingAction = PendingAction.START_DOWNLOAD
        _isLoading.value = true
        _isAdLoading.value = true

        logManager.info(context.getString(R.string.log_loading_url, url))

        adManager.loadAndShowInterstitialAd()
    }

    private fun loadUrlInWebView(url: String) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                webViewRepository.clearSessionData()
                logManager.success(context.getString(R.string.log_session_cleared))

                webViewRepository.loadUrl(url)
                webViewRepository.injectQuotaDetectionScript()
                logManager.info("Quota detection script injected")
            } catch (e: Exception) {
                logManager.error("Failed to load URL", e)
                _isLoading.postValue(false)
                isProcessing = false
            }
        }
    }

    fun onRotateIp() {
        if (_isRotating.value == true) {
            logManager.warning("IP rotation already in progress")
            return
        }

        _isRotating.value = true
        _isAdLoading.value = true
        pendingAction = PendingAction.ROTATE_IP

        logManager.info(context.getString(R.string.log_requesting_new_identity))

        adManager.loadAndShowInterstitialAd()
    }

    private fun rotateIpAddress() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                logManager.info(context.getString(R.string.log_clearing_session))
                webViewRepository.clearSessionData()
                logManager.success(context.getString(R.string.log_session_cleared))

                val result = torRepository.requestNewIdentity()
                if (result.isSuccess) {
                    logManager.success(context.getString(R.string.log_new_identity_acquired))
                    _torConnected.postValue(true)
                } else {
                    logManager.error(context.getString(R.string.toast_rotation_failed), result.exceptionOrNull())
                    _torConnected.postValue(false)
                }
            } catch (e: Exception) {
                logManager.error(context.getString(R.string.toast_rotation_failed), e)
                _torConnected.postValue(false)
            } finally {
                _isRotating.postValue(false)
                _isAdLoading.postValue(false)

                if (currentUrl != null) {
                    loadUrlInWebView(currentUrl!!)
                }
            }
        }
    }

    fun onWebViewPageFinished() {
        _isLoading.postValue(false)
        isProcessing = false
        webViewRepository.injectQuotaDetectionScript()
    }

    fun onWebViewError(error: String) {
        logManager.error("WebView error: $error")
        _isLoading.postValue(false)
        isProcessing = false
    }

    fun clearLogs() {
        logManager.clearLogs()
    }

    private fun isValidUrl(url: String): Boolean {
        return try {
            val uri = Uri.parse(url)
            (uri.scheme == "http" || uri.scheme == "https") && uri.host != null
        } catch (e: Exception) {
            false
        }
    }

    private val context: android.content.Context
        get() = logManager.applicationContext

    override fun onCleared() {
        super.onCleared()
        webViewRepository.destroy()
    }
}