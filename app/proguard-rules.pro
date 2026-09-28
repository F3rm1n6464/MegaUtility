# ProGuard rules for Mega Utility

# Hilt
-keep class dagger.hilt.** { *; }
-keep class com.example.xupermega.** { *; }

# WebView JavaScript Interface
-keepclassmembers class com.example.xupermega.utils.WebViewManager$QuotaDetectionInterface {
    @android.webkit.JavascriptInterface <methods>;
}
-keepclassmembers class com.example.xupermega.utils.QuotaDetector$QuotaDetectionInterface {
    @android.webkit.JavascriptInterface <methods>;
}
-keepclassmembers class com.example.xupermega.utils.AdManager$AdJavaScriptInterface {
    @android.webkit.JavascriptInterface <methods>;
}

# Kotlin coroutines
-keepclassmembers class kotlinx.coroutines.** { *; }

# Timber
-dontwarn timber.log.Timber

# AndroidX WebView
-keep class android.webkit.** { *; }

# Keep model classes
-keep class com.example.xupermega.model.** { *; }

# Keep repository interfaces
-keep interface com.example.xupermega.repository.** { *; }