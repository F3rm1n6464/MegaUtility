package com.example.xupermega.model

object AdConfig {
    const val AD_ZONE_ID: String = "YOUR_AD_ZONE_ID_HERE"
    const val AADS_BASE_URL = "https://a-ads.com/"
    const val INTERSTITIAL_HTML_PATH = "ads/aads_interstitial.html"
    
    fun getAdUrl(): String {
        return "${AADS_BASE_URL}$AD_ZONE_ID"
    }
    
    fun getInterstitialHtml(adZoneId: String = AD_ZONE_ID): String {
        return """
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
    <title>A-Ads Interstitial</title>
    <style>
        body { margin: 0; padding: 0; background: #000; overflow: hidden; }
        #ad-container { width: 100%; height: 100vh; display: flex; align-items: center; justify-content: center; }
    </style>
</head>
<body>
    <div id="ad-container"></div>
    <script async src="https://a-ads.com/$adZoneId.js"></script>
    <script>
        window.addEventListener('load', function() {
            if (window.AndroidInterface) {
                window.AndroidInterface.onAdLoaded();
            }
        });
        
        document.addEventListener('click', function() {
            if (window.AndroidInterface) {
                window.AndroidInterface.onAdClicked();
            }
        });
    </script>
</body>
</html>
        """.trimIndent()
    }
}