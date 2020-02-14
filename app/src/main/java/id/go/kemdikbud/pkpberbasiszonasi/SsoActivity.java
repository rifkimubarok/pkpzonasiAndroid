package id.go.kemdikbud.pkpberbasiszonasi;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kosalgeek.android.caching.FileCacher;

import java.io.IOException;
import java.net.URISyntaxException;

import id.go.kemdikbud.pkpberbasiszonasi.Helper.ApiService;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.ViewDialog;

public class SsoActivity extends AppCompatActivity {

    WebView webView;
    ApiService apiService;
    ViewDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sso);

        apiService = new ApiService(this);
        dialog = new ViewDialog(this);
        webView = (WebView) findViewById(R.id.browser_sso);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");

        double passport = Math.random() * 1000;
        String url = apiService.getEndPointAPI();
        url += "admin/tool/mobile/launch.php?service=moodle_mobile_app&urlscheme=pkpzonasi://login/token?param=&passport=";
        url += passport;

        webView.setWebViewClient(new customView(this));
        load_page(url);
    }

    public void load_page(String url){
        webView.loadUrl(url);
    }

    class customView extends WebViewClient {
        Context context;
        public customView(Context context) {
            this.context = context;
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("pkpzonasi://")) {
                try {
                    Log.d("URL DeepLINK",url);
                    Context context = view.getContext();
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);

                    if (intent != null) {
                        view.stopLoading();

                        PackageManager packageManager = context.getPackageManager();
                        ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
                        if (info != null) {
                            context.startActivity(intent);
                        } else {
                            String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                            view.loadUrl(fallbackUrl);

                            // or call external broswer
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl));
//                    context.startActivity(browserIntent);
                        }

                        return true;
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            else if (url.startsWith("intent")) {
                try {
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);

                    String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                    if (fallbackUrl != null) {
                        view.loadUrl(fallbackUrl);
                        return true;
                    }
                } catch (URISyntaxException e) {
                    //not an intent uri
                }
            }else{
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            if (url.startsWith("pkpzonasi://")) {
                try {
                    Log.d("URL DeepLINK",url);
                    Context context = view.getContext();
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);

                    if (intent != null) {
                        view.stopLoading();

                        PackageManager packageManager = context.getPackageManager();
                        ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
                        if (info != null) {
                            context.startActivity(intent);
                        } else {
                            String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                            view.loadUrl(fallbackUrl);

                            // or call external broswer
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl));
//                    context.startActivity(browserIntent);
                        }

                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (url.startsWith("pkpzonasi://")) {
                try {
                    Log.d("URL DeepLINK",url);
                    Context context = view.getContext();
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);

                    if (intent != null) {
                        view.stopLoading();

                        PackageManager packageManager = context.getPackageManager();
                        ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
                        if (info != null) {
                            context.startActivity(intent);
                        } else {
                            String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                            view.loadUrl(fallbackUrl);

                            // or call external broswer
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl));
//                    context.startActivity(browserIntent);
                        }

                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            if (url.startsWith("https://paspor-gtk.belajar.kemdikbud.go.id")){
                FileCacher username_cache = new FileCacher(getApplicationContext(),"username");
                FileCacher password_cache = new FileCacher(getApplicationContext(),"password");
                String username = "";
                String password = "";
                try {
                    username = username_cache.readCache().toString();
                    password = password_cache.readCache().toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String ur = "javascript:" +
                        "document.getElementById('password').value = '" + password + "';"  +
                        "document.getElementById('username').value = '" + username + "';"  +
                        "document.forms[0].submit()";
                view.loadUrl(ur);
            }

            view.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
        }

    }

    class MyJavaScriptInterface
    {
//        Context context;
//        public MyJavaScriptInterface(Context context){
//            this.context = context;
//        }
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processHTML(String html)
        {
            Log.d("SsoActivity",html);
            if (html.contains("id=\"msg\" class=\"alert alert-danger\"")){
                onBackPressed();
            }
        }
    }
}
