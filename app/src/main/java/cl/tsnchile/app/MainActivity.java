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

public class MainActivity extends Activity {
    private static final String HOME = "https://torresalejandroaltamirano.github.io/TSN-Chile/";
    private WebView web;
    private TextView status;

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
        setContentView(root);
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
    private void showStatus(String message) { status.setText(message); status.setVisibility(TextView.VISIBLE); }
    @Override public void onBackPressed() {
        if (web != null && web.canGoBack()) web.goBack(); else super.onBackPressed();
    }
}
