package cl.tsnchile.app;
import android.app.*;import android.os.*;import android.webkit.*;import android.content.*;import android.net.*;
public class MainActivity extends Activity {
 WebView web;
 @Override public void onCreate(Bundle b){super.onCreate(b);web=new WebView(this);setContentView(web);WebSettings s=web.getSettings();s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);s.setAllowFileAccess(false);s.setAllowContentAccess(false);web.setWebViewClient(new WebViewClient(){@Override public boolean shouldOverrideUrlLoading(WebView v,WebResourceRequest r){Uri u=r.getUrl();String h=u.getHost();if(h!=null&&(h.equals("torresalejandroaltamirano.github.io")||h.equals("osognidheouthkubxbev.supabase.co")))return false;startActivity(new Intent(Intent.ACTION_VIEW,u));return true;}});web.loadUrl("https://torresalejandroaltamirano.github.io/TSN-Chile/");}
 @Override public void onBackPressed(){if(web!=null&&web.canGoBack())web.goBack();else super.onBackPressed();}
}