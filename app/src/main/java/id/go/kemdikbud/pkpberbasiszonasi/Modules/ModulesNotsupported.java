package id.go.kemdikbud.pkpberbasiszonasi.Modules;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.ApiService;
import id.go.kemdikbud.pkpberbasiszonasi.Master.UnitModule;
import id.go.kemdikbud.pkpberbasiszonasi.R;
import id.go.kemdikbud.pkpberbasiszonasi.RequestAdapter;
import com.google.gson.Gson;
import com.kosalgeek.android.caching.FileCacher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ModulesNotsupported extends AppCompatActivity {
    private WebView webView;
    String url = "";
    JSONObject tokenObject;
    String token = "";
    String privateToken = "";
    String endPointApi="";
    ApiService apiService;
    String profileChaceName;
    JSONArray profileArray;
    private UnitModule unitModule;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeContent;

    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int FILECHOOSER_RESULTCODE = 1;
    public int login_page_count = 1;

    String TAG = ModulesNotsupported.class.getSimpleName();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            Uri[] results = null;
            // Check that the response is a good one
            if (resultCode == AppCompatActivity.RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }
            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (null == this.mUploadMessage) {
                    return;
                }
                Uri result = null;
                try {
                    if (resultCode != RESULT_OK) {
                        result = null;
                    } else {
                        // retrieve from the private variable if the intent is null
                        result = data == null ? mCapturedImageURI : data.getData();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "activity :" + e,
                            Toast.LENGTH_LONG).show();
                }
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modules_notsupported);

        apiService = new ApiService(this);

        webView = (WebView) findViewById(R.id.webContent);
        progressBar = (ProgressBar)findViewById(R.id.progress_webview);
        swipeContent = (SwipeRefreshLayout) findViewById(R.id.swipeContent);

        webView.setWebChromeClient(new customChrome(progressBar));
        webView.setWebViewClient(new customView(progressBar));
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setSaveFormData(false);
//        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (webView.getScrollY() == 0){
                    swipeContent.setEnabled(true);
                }else{
                    swipeContent.setEnabled(false);
                }
            }
        });
        swipeContent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });

        if (getIntent().getExtras() != null){
            try {
                Bundle bundle = getIntent().getExtras();
                url = bundle.getString("url");
            }catch (Exception e){
                url = "";
                e.printStackTrace();
            }
        }
        FileCacher fileCacher = new FileCacher(getApplicationContext(),"token.txt");
        try {
            if (fileCacher.hasCache()){
                tokenObject = new JSONObject(fileCacher.readCache().toString());
                if (tokenObject.has("token")){
                    token = tokenObject.getString("token");
                    privateToken = tokenObject.getString("privatetoken");
                    profileChaceName = "profile"+token;
                }
            }
            FileCacher fileCacherProfile = new FileCacher(getApplicationContext(),profileChaceName+".txt");
            if (fileCacherProfile.hasCache()){
                profileArray = new JSONArray(fileCacherProfile.readCache().toString());
            }else{
                profileArray = new JSONArray();
            }
        }catch (IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v(TAG,"chache namenya adalah "+profileChaceName);

        if (getIntent().getExtras() != null){
            try {
                Bundle bundle = getIntent().getExtras();
                unitModule = new Gson().fromJson(bundle.getString("modules"),UnitModule.class);
            }catch (Exception e){
                unitModule = new UnitModule();
                e.printStackTrace();
            }
        }

        if (!unitModule.getName().isEmpty()){
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_white));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            setTitle(unitModule.getName());
        }

        endPointApi += apiService.getEndPointAPI()+"webservice/rest/server.php?";
        endPointApi += "wsfunction=tool_mobile_get_autologin_key&moodlewsrestformat=json&wstoken="+token;
//        LoadWeb();
        getWebContent();
    }

    private void LoadWeb(){
        Log.v(TAG,endPointApi.toString());

        final StringRequest request = new StringRequest(Request.Method.POST,endPointApi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (profileArray.length()>0){
                                JSONObject profileObjt = profileArray.getJSONObject(0);
                                JSONObject object = new JSONObject(response);
                                if (object.has("exception")){
                                    webView.loadUrl(unitModule.getUrl());
                                }else{
                                    object.put("expired",System.currentTimeMillis()+(6*60000));
                                    Log.v(TAG,object.toString());
                                    FileCacher fileCacher = new FileCacher(getApplicationContext(),"access"+token+".txt");

                                    if (fileCacher.hasCache()){
                                        fileCacher.clearCache();
                                        fileCacher.writeCache(object.toString());
                                    }else{
                                        fileCacher.writeCache(object.toString());
                                    }

                                    String url_load = object.getString("autologinurl");
                                    String url_fix = url_load+"?userid="+profileObjt.getInt("id")+"&key="+object.getString("key");
                                    url_fix += "&urltogo="+unitModule.getUrl();
                                    Log.v(TAG,"URL FIX "+url_fix);
                                    webView.loadUrl(url_fix);
                                }

                            }else{
                                System.out.println(profileArray);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, "Error FETCH API: " + error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("privatetoken", privateToken);
                return params;
            }
        };

        RequestAdapter.getInstance().addToRequestQueue(request);
    }

    private void getWebContent(){
        if (!unitModule.getUrl().substring(0,5).equals("https")){
            webView.loadUrl(unitModule.getUrl());
            webView.addJavascriptInterface(new LoaderListener(webView),"HOMELAYOUT");
            return;
        }
        FileCacher fileCacher = new FileCacher(getApplicationContext(),"access"+token+".txt");
        try{
            if (fileCacher.hasCache()){
                JSONObject access = new JSONObject(fileCacher.readCache().toString());
                if (access.has("expired")){
                    Long expired = access.getLong("expired");
                    Date current_date = new Date(System.currentTimeMillis());
                    Date expired_date = new Date(expired);
                    if (expired_date.compareTo(current_date)<0){
                        LoadWeb();
//                        Log.v(TAG,"expired = "+expired_date);
                    }else{
                        if (profileArray.length()>0) {
                            JSONObject profileObjt = profileArray.getJSONObject(0);
                            String url_load = access.getString("autologinurl");
                            String url_fix = url_load + "?userid=" + profileObjt.getInt("id") + "&key=" + access.getString("key");
                            url_fix += "&urltogo=" + unitModule.getUrl();
                            webView.loadUrl(url_fix);
                        }
                    }
                }
            }else{
                LoadWeb();
            }
        }catch (IOException e){
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class customChrome extends WebChromeClient{
        ProgressBar progressbar;
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        public customChrome(ProgressBar progress){
            this.progressbar = progress;
        }

        public Bitmap getDefaultVideoPoster()
        {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView()
        {
            ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
        {
            if (this.mCustomView != null)
            {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846);
        }

        private File createImageFile() throws IOException {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            File imageFile = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            return imageFile;
        }

        public void onProgressChanged(WebView view, int progress) {
            progressbar.setProgress(progress *100);
        }

        // For Android 5.0
        public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, WebChromeClient.FileChooserParams fileChooserParams) {
            // Double check that we don't have any existing callbacks
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePath;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.e(TAG, "Unable to create Image File", ex);
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                } else {
                    takePictureIntent = null;
                }
            }
            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("image/*");
            Intent[] intentArray;
            if (takePictureIntent != null) {
                intentArray = new Intent[]{takePictureIntent};
            } else {
                intentArray = new Intent[0];
            }
            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
//            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
            startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
            return true;
        }

        // openFileChooser for Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            mUploadMessage = uploadMsg;
            // Create AndroidExampleFolder at sdcard
            // Create AndroidExampleFolder at sdcard
            File imageStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES)
                    , "AndroidExampleFolder");
            if (!imageStorageDir.exists()) {
                // Create AndroidExampleFolder at sdcard
                imageStorageDir.mkdirs();
            }
            // Create camera captured image file path and name
            File file = new File(
                    imageStorageDir + File.separator + "IMG_"
                            + String.valueOf(System.currentTimeMillis())
                            + ".jpg");
            mCapturedImageURI = Uri.fromFile(file);
            // Camera capture image intent
            final Intent captureIntent = new Intent(
                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            // Create file chooser intent
            Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
            // Set camera intent to file chooser
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                    , new Parcelable[]{captureIntent});
            // On select image call onActivityResult method of activity
            startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
        }

        // openFileChooser for Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }

        //openFileChooser for other Android versions
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType,
                                    String capture) {
            openFileChooser(uploadMsg, acceptType);
        }
    }

    class customView extends WebViewClient{
        private ProgressBar progressBar;

        public customView(ProgressBar progressBar) {
            this.progressBar=progressBar;
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("intent://")) {
                try {
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
            }else{
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

//            view.setVisibility(View.GONE);

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
            swipeContent.setRefreshing(false);
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
            Log.d("URL LOGIN",url);

            if(url.contains("login")){
                    if(login_page_count <=1){
                        String ur = "javascript:" +
                                "document.getElementById('password').value = '" + password + "';"  +
                                "document.getElementById('username').value = '" + username + "';"  +
                                "document.forms[0].submit()";
                        view.loadUrl(ur);
                        login_page_count++;
                    }
            }
            if (url.startsWith("https://paspor-gtk.belajar.kemdikbud.go.id")){
                String ur = "javascript:" +
                        "document.getElementById('password').value = '" + password + "';"  +
                        "document.getElementById('username').value = '" + username + "';"  +
                        "document.forms1.submit()";
                view.loadUrl(ur);
            }
        }
    }

    String resultHTML;
    Handler handlerForJavascriptInterface = new Handler();
    class LoaderListener{
        WebView view;
        ProgressDialog pd;
        LoaderListener(WebView web){
            view = web;
        }
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processHTML(String html){
            Log.e("result", html);
            resultHTML = html;
            handlerForJavascriptInterface.post(new Runnable() {
                @Override
                public void run() {
                    if (resultHTML.contains("Selamat Datang")) {

                        //post login
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
                        Log.d("AKUN2", username +";"+password);
                        String url = "javascript:" +
                                "document.getElementById('password').value = '" + password + "';"  +
                                "document.getElementById('username').value = '" + username + "';"  +
                                "document.form1.submit()";

                        if (Build.VERSION.SDK_INT >= 19) {
                            view.evaluateJavascript(url, new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String s) {
                                    Log.d("Recieve", s);
                                }
                            });
                        } else {
                            view.loadUrl(url);
                        }
                    } else  {
                        view.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

}
