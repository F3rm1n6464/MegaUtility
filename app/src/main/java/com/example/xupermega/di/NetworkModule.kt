package com.example.xupermega.di

import android.content.Context
import android.webkit.WebView
import com.example.xupermega.repository.TorRepository
import com.example.xupermega.repository.TorRepositoryImpl
import com.example.xupermega.repository.WebViewRepository
import com.example.xupermega.repository.WebViewRepositoryImpl
import com.example.xupermega.utils.AdManager
import com.example.xupermega.utils.QuotaDetector
import com.example.xupermega.utils.TorManager
import com.example.xupermega.utils.WebViewManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideWebViewManager(@ApplicationContext context: Context): WebViewManager {
        return WebViewManager(context)
    }

    @Provides
    @Singleton
    fun provideTorManager(@ApplicationContext context: Context): TorManager {
        return TorManager(context)
    }

    @Provides
    @Singleton
    fun provideTorRepository(torManager: TorManager): TorRepository {
        return TorRepositoryImpl(torManager)
    }

    @Provides
    @Singleton
    fun provideQuotaDetector(): QuotaDetector {
        return QuotaDetector()
    }

    @Provides
    @Singleton
    fun provideWebViewRepository(
        webViewManager: WebViewManager,
        quotaDetector: QuotaDetector
    ): WebViewRepository {
        return WebViewRepositoryImpl(webViewManager, quotaDetector)
    }

    @Provides
    @Singleton
    fun provideAdManager(@ApplicationContext context: Context): AdManager {
        return AdManager(context)
    }
}