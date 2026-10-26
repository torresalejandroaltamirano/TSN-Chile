package cl.tsnchile.app;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.widget.TextView;
import android.widget.FrameLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Button;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

public class MainActivity extends Activity {
    private static final String HOME = "https://torresalejandroaltamirano.github.io/TSN-Chile/";
    private WebView web;
    private TextView status;
    private LinearLayout bannerSlot;
    private Button privacy;
    private AdView banner;
    private ConsentInformation consent;
    private boolean adsStarted;
    // Change only after device QA and Play disclosures are updated.
    private static final boolean PRODUCTION_ADS = false;
    private static final String LIVE_BANNER = "ca-app-pub-2369519530952827/5145549220";

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        FrameLayout root = new FrameLayout(this);
        web = new WebView(this);
        web.setBackgroundColor(Color.WHITE);
        root.addView(web, new FrameLayout.LayoutParams(-1, -1));
        status = new TextView(this);
        status.setTextColor(Color.WHITE);
        status.setBackgroundColor(Color.rgb(6, 43, 82));
        status.setTextSize(16);
        status.setGravity(Gravity.CENTER);
        status.setPadding(32, 24, 32, 24);
        status.setText("Cargando TSN Chile…");
        root.addView(status, new FrameLayout.LayoutParams(-1, -1));
        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setBackgroundColor(Color.WHITE);
        screen.setPadding(0, 0, 0, 0);
        screen.setOnApplyWindowInsetsListener((v, insets) -> {
            v.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
                insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
            return insets.consumeSystemWindowInsets();
        });
        screen.addView(root, new LinearLayout.LayoutParams(-1, 0, 1));
        privacy = new Button(this);
        privacy.setText("Privacidad de los anuncios");
        privacy.setVisibility(View.GONE);
        screen.addView(privacy, new LinearLayout.LayoutParams(-1, -2));
        bannerSlot = new LinearLayout(this);
        bannerSlot.setGravity(Gravity.CENTER);
        bannerSlot.setVisibility(View.GONE);
        screen.addView(bannerSlot, new LinearLayout.LayoutParams(-1, -2));
        setContentView(screen);
        consent = UserMessagingPlatform.getConsentInformation(this);
        privacy.setOnClickListener(v -> {
            destroyBanner();
            adsStarted = false;
            UserMessagingPlatform.showPrivacyOptionsForm(this, error -> {
                updatePrivacy();
                startAdsIfAllowed();
            });
        });
        ConsentRequestParameters params = new ConsentRequestParameters.Builder().build();
        consent.requestConsentInfoUpdate(this, params, () ->
            UserMessagingPlatform.loadAndShowConsentFormIfRequired(this, error -> {
                updatePrivacy();
                startAdsIfAllowed();
            }), error -> {
                updatePrivacy();
                startAdsIfAllowed();
            });
        startAdsIfAllowed();
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        web.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String host = uri.getHost();
                if (("https".equals(uri.getScheme())) && host != null &&
                    (host.equals("torresalejandroaltamirano.github.io") || host.equals("osognidheouthkubxbev.supabase.co"))) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW, uri)); }
                catch (ActivityNotFoundException ex) { showStatus("No hay una aplicación disponible para abrir este enlace."); }
                return true;
            }
            @Override public void onPageFinished(WebView view, String url) {
                if (status.getText().toString().startsWith("Cargando")) status.setVisibility(TextView.GONE);
            }
            @Override public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (request.isForMainFrame()) showStatus("No se pudo cargar TSN Chile. Comprueba tu conexión a Internet y vuelve a abrir la aplicación.");
            }
            @Override public void onReceivedHttpError(WebView view, WebResourceRequest request, android.webkit.WebResourceResponse response) {
                if (request.isForMainFrame()) showStatus("TSN Chile no está disponible (HTTP " + response.getStatusCode() + "). Revisa la publicación en GitHub Pages.");
            }
        });
        web.loadUrl(HOME);
    }
    private void updatePrivacy() {
        privacy.setVisibility(consent.getPrivacyOptionsRequirementStatus() ==
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED ? View.VISIBLE : View.GONE);
    }
    private void startAdsIfAllowed() {
        if (isFinishing() || isDestroyed() || adsStarted || !consent.canRequestAds()) return;
        adsStarted = true;
        MobileAds.initialize(this, ignored -> runOnUiThread(() -> {
            if (isFinishing() || isDestroyed() || !consent.canRequestAds()) {
                adsStarted = false;
                return;
            }
            bannerSlot.post(() -> {
                if (isFinishing() || isDestroyed() || !consent.canRequestAds()) return;
                int width = (int) (getResources().getDisplayMetrics().widthPixels /
                    getResources().getDisplayMetrics().density);
                banner = new AdView(this);
                banner.setAdUnitId(PRODUCTION_ADS ? LIVE_BANNER :
                    "ca-app-pub-3940256099942544/9214589741");
                banner.setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, width));
                banner.setAdListener(new AdListener() {
                    @Override public void onAdLoaded() {
                        if (!isFinishing() && !isDestroyed()) bannerSlot.setVisibility(View.VISIBLE);
                    }
                    @Override public void onAdFailedToLoad(LoadAdError error) {
                        bannerSlot.setVisibility(View.GONE);
                    }
                });
                bannerSlot.addView(banner, new LinearLayout.LayoutParams(-1, -2));
                banner.loadAd(new AdRequest.Builder().build());
            });
        }));
    }
    private void destroyBanner() {
        if (banner != null) { banner.destroy(); banner = null; }
        if (bannerSlot != null) {
            bannerSlot.removeAllViews();
            bannerSlot.setVisibility(View.GONE);
        }
    }
    @Override protected void onPause() {
        if (banner != null) banner.pause();
        super.onPause();
    }
    @Override protected void onResume() {
        super.onResume();
        if (banner != null) banner.resume();
    }
    @Override protected void onDestroy() {
        destroyBanner();
        if (web != null) { web.stopLoading(); web.destroy(); web = null; }
        super.onDestroy();
    }
    private void showStatus(String message) { status.setText(message); status.setVisibility(TextView.VISIBLE); }
    @Override public void onBackPressed() {
        if (web != null && web.canGoBack()) web.goBack(); else super.onBackPressed();
    }
}
