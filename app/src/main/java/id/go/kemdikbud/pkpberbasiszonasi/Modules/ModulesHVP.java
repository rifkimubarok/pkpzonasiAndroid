package id.go.kemdikbud.pkpberbasiszonasi.Modules;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;

import id.go.kemdikbud.pkpberbasiszonasi.Helper.ApiService;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.CheckConnection;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.CheckPermissionSD;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.DownloadDialog;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.FileHelper;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.ViewDialog;
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
import java.text.DecimalFormat;

import static id.go.kemdikbud.pkpberbasiszonasi.Helper.ZipManager.unzip;

public class ModulesHVP extends AppCompatActivity {
    private String modules="";
    private UnitModule unitModule;
    private Toolbar toolbar;
    private String hvpScript ="";
    private MenuItem menuItem;

    private String ExportUrl = "";
    private String ExportDir ="";
    private String ExportFile = "";


    private ApiService apiService;
    private String endPointApi = "";
    private String endPointApiExport = "";
    private String cacheName= "";
    private String cacheNameExport= "";
    private String token ="";
    private JSONObject tokenObject;
    private static final int ItemThreeDotMenu = 1;

    private WebView webView;
    private SwipeRefreshLayout swipeContent;

    private int DonwloadID=0;

    private final String TAG = ModulesHVP.class.getSimpleName();

    private Context context;
    private static final int WRITE_REQUEST_CODE =1;
    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modules_hvp);
        apiService = new ApiService(this);
        context = getApplicationContext();
        String jsresizer = apiService.getEndPointAPI()+"/mod/hvp/library/js/h5p-resizer.js";
        hvpScript = "<script src='"+jsresizer+"' charset=\"UTF-8\"></script>";
        webView = (WebView) findViewById(R.id.webContent);
        webView.setWebChromeClient(new CustomChrome());
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setDomStorageEnabled(true);

        swipeContent = (SwipeRefreshLayout) findViewById(R.id.swipeContent);
        swipeContent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData(true);
            }
        });

        // PASS Parameter from Activity
        if (getIntent().getExtras() != null){
            try {
                Bundle bundle = getIntent().getExtras();
                modules = bundle.getString("modules");
            }catch (Exception e){
                modules = "";
                e.printStackTrace();
            }
        }else{
            modules = "";
        }
        if (!modules.equals("")){
            unitModule = new Gson().fromJson(modules,UnitModule.class);
        }else{
            unitModule = new UnitModule();
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

        // GET TOKEN
        FileCacher tokenString = new FileCacher(this,"token.txt");
        try {
            if (tokenString.hasCache()){
                tokenObject = new JSONObject(tokenString.readCache().toString());
                if (tokenObject.has("token")){
                    token = tokenObject.getString("token");
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Initial EndPoint API
        endPointApi += apiService.getEndPointAPI()+"webservice/rest/server.php?";
        endPointApi += "wstoken="+token+"&moodlewsrestformat=json&wsfunction=tool_mobile_get_content&component=mod_hvp&method=mobile_course_view&args[0][name]=cmid&args[0][value]="+unitModule.getId();
        cacheName = "modules"+unitModule.getModname()+unitModule.getId()+token;

        endPointApiExport = apiService.getEndPointAPI()+"mod/hvp/export.php?wstoken="+token+"&id="+unitModule.getId();
        cacheNameExport = "url"+unitModule.getModname()+unitModule.getId()+token;
        Log.d(TAG,endPointApi);
        refreshData(false);

        if (Build.VERSION.SDK_INT >= 23){
            requestPermissions(permissions, WRITE_REQUEST_CODE);
        }

        CheckPermissionSD checkPermissionSD = new CheckPermissionSD();
        checkPermissionSD.checkPermission(this,this);

        PRDownloader.initialize(getApplicationContext());
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);
    }

    public  boolean onCreateOptionsMenu(Menu menu){
        menuItem = (MenuItem) menu.add(0,ItemThreeDotMenu,0,null);
        menuItem.setIcon(R.drawable.ic_menu_dots_white).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//        View v = MenuItemCompat.getActionView(menuItem);
//        showPopupMenu(v);
        return true;
    }

    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(this,view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.option_module_h5p, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.download_button:
                        openDialog();
                        break;
                    case R.id.refresh:
                        refreshData(true);
                        break;
                }
                return false;
            }
        });
        popup.show();
    }

    public void openDialog(){
        if (ExportUrl.isEmpty()){
            fetchDataExport(endPointApiExport,cacheNameExport);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Apakah anda yakin akan mengunduh pembelanjaran ini?\n"+
                "Mungkin memerlukan penyimpanan yang cukup besar.");
        builder.setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DonwloadID = DownloadManager(ExportDir,ExportUrl,ExportFile);
            }
        });
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialogconfirm = builder.create();
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Log.v(TAG,"item menunya "+item.getItemId());
        switch (item.getItemId()){
            case ItemThreeDotMenu:
                View view = findViewById(item.getItemId());
                showPopupMenu(view);
                /*if (ExportUrl.isEmpty()){
                    fetchDataExport(endPointApiExport,cacheNameExport);
                }
                DonwloadID = DownloadManager(ExportDir,ExportUrl,ExportFile);*/
                break;
            case 2:
                File file = new File(Environment.getExternalStorageDirectory()+"/PKPZonasi/web/workspace/",ExportDir);
                FileHelper fileHelper = new FileHelper();
                fileHelper.deleteRecursive(file);
                break;
        }
        return true;
    }

    private void fetchDataFromAPI(String uri, final String filename) {
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,uri,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("templates")){
                                JSONArray template = response.getJSONArray("templates");
                                if (template.length()>0){
                                    pushDataToWebView(template);
                                }
                                FileCacher<String> fileCacher = new FileCacher<>(getApplicationContext(),filename+".txt");
                                if (fileCacher.hasCache()){
                                    fileCacher.clearCache();
                                    fileCacher.writeCache(response.toString());
                                }else {
                                    fileCacher.writeCache(response.toString());
                                }
                            }else {
                                Log.d(TAG,"error asdalsdkaskl");
                            }

//                            fetchDataToSpinner(response);
//                            fetchDataExport(endPointApiExport,cacheNameExport);

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, "Error FETCH API: " + error.getMessage());
                Toast.makeText(getApplication(), "Error FETCH API: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                fetchDataExport(endPointApiExport,cacheNameExport);
                loadOffline();
                swipeContent.setRefreshing(false);
            }
        });

        RequestAdapter.getInstance().addToRequestQueue(request);
        /*for (int i = 0 ; i<10;i++){
            UnitCourses courses = new UnitCourses();
            courses.setId(""+(i+1));
            courses.setTitle("Contoh Unit Ke-"+(i+1));
            courses.setImage("https://training.p4tkipa.net/theme/moove/pix/default_course.jpg");
            itemsList.add(courses);
        }
        mAdapter.notifyDataSetChanged();*/
    }

    private void fetchDataExport(String uri, final String FileCache) {
        final ViewDialog viewDialog = new ViewDialog(this);
        viewDialog.showDialog();
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,uri,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("url")){
                                ExportUrl = response.getString("url");
                                String[] url = ExportUrl.split("[/]");
                                String filename = url[url.length-1];
                                String[] fname = filename.split("[.]");
                                String fxName = "";
                                if (fname.length>0){
                                    fxName = fname[0]+".zip";
                                }else {
                                    fxName = "default.zip";
                                }
                                String foldername = "hvp"+unitModule.getId();
                                ExportDir = foldername;
                                ExportFile = fxName;
                                FileCacher<String> fileCacher = new FileCacher<>(getApplicationContext(),FileCache+".txt");
                                if (fileCacher.hasCache()){
                                    fileCacher.clearCache();
                                    fileCacher.writeCache(ExportUrl);
                                }else {
                                    fileCacher.writeCache(ExportUrl);
                                }
                            }else {
                                Log.d(TAG,"error asdalsdkaskl");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        viewDialog.hideDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                viewDialog.hideDialog();
                Log.e(TAG, "Error Export: " + error.getMessage());
//                Toast.makeText(getApplication(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestAdapter.getInstance().addToRequestQueue(request);
        /*for (int i = 0 ; i<10;i++){
            UnitCourses courses = new UnitCourses();
            courses.setId(""+(i+1));
            courses.setTitle("Contoh Unit Ke-"+(i+1));
            courses.setImage("https://training.p4tkipa.net/theme/moove/pix/default_course.jpg");
            itemsList.add(courses);
        }
        mAdapter.notifyDataSetChanged();*/
    }

    public int DownloadManager(String Foldername, String Url, final String Filename){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)){

            File dirExternal = Environment.getExternalStorageDirectory();
            File createDir = new File(dirExternal.getAbsolutePath()+"/PKPZonasi/web/workspace/");

            if (!createDir.exists()){
                if (!createDir.mkdir()){
                    Log.d(TAG,"Access Denied To Create"+createDir.getPath());
                }
            }

            final File destFile = new File(createDir.getAbsolutePath(),Foldername);
            if (!destFile.exists()){
                if (!destFile.mkdir()){
                    Log.d(TAG,"Access Denied To Create"+destFile.getPath());
                }
            }
            if (new File(destFile.getAbsolutePath(),Filename).exists()){
                Log.d(TAG,"path "+new File(destFile.getAbsolutePath(),Filename).getAbsolutePath());
                Toast.makeText(getApplicationContext(),"File Sudah Didownload",Toast.LENGTH_SHORT).show();
                return 0;
            }
            final DownloadDialog downloadDialog = new DownloadDialog(this);
//            ExportDir = destFile.getAbsolutePath();
//            ExportFile = Filename;
            final DownloadDialog dldialog = new DownloadDialog(this);
            int downloadId = PRDownloader.download(Url, destFile.getAbsolutePath(), Filename)
                    .build()
                    .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                        @Override
                        public void onStartOrResume() {
                            downloadDialog.showDialog();
                            downloadDialog.setFilename(Filename);
                        }
                    })
                    .setOnPauseListener(new OnPauseListener() {
                        @Override
                        public void onPause() {

                        }
                    })
                    .setOnCancelListener(new OnCancelListener() {
                        @Override
                        public void onCancel() {

                        }
                    })
                    .setOnProgressListener(new OnProgressListener() {
                        @Override
                        public void onProgress(Progress progress) {
                            DecimalFormat df2 = new DecimalFormat("#.##");

                            double current = (double) progress.currentBytes;
                            double total = (double) progress.totalBytes;
                            double percent = (current/total)*100;
                            String spercent = (String) df2.format(percent);
                            double totalMb = total/1024/1024;
                            double currentMb = 0;
                            String fixcurrentMb = "";
                            if ((int) current/1024/1024 >= 1){
                                currentMb = current/1024/1024;
                                fixcurrentMb = (String) df2.format(currentMb) + "MB";
                            }else{
                                currentMb = current/1024;
                                fixcurrentMb = (String) df2.format(currentMb) + "KB";
                            }

                            downloadDialog.setProgressBar((int)(progress.currentBytes*100/progress.totalBytes));
                            downloadDialog.setPercentDownload(spercent);
                            downloadDialog.setCurrentDownload(fixcurrentMb);
                            downloadDialog.setSizeDownload(new DecimalFormat("##.##").format(totalMb) + "MB");
                        }
                    })
                    .start(new OnDownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            downloadDialog.hideDialog();
                            File file = new File(Environment.getExternalStorageDirectory()+"/PKPZonasi/web/workspace",ExportDir);
                            File filename = new File(file.getAbsolutePath(),ExportFile);
                            Log.v(TAG,"file "+file.getAbsolutePath());
                            Log.v(TAG,"filename "+filename.getAbsolutePath());
                            try {
                                unzip(filename,file,dldialog);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
//                            extrackZip(destFile.getAbsolutePath(),Filename);
                        }

                        @Override
                        public void onError(Error error) {
                            Log.d(TAG,"Download "+Filename+ " Was Error");
                            Toast.makeText(getApplicationContext(),"Tidak Dapat Mengunduh File ini.",Toast.LENGTH_SHORT);
                        }
                    });
            return downloadId;
        }else {

        }
        return 0;
    }

    private boolean refreshData(boolean isRefresh){
        CheckConnection ck = new CheckConnection();
        if (!ck.isInternetIsConnected(this)){
            Log.v(TAG,"No internet Connection");
            return loadOffline();
        }else{
            FileCacher fileCacher = new FileCacher(getApplicationContext(),cacheName);
            if (fileCacher.hasCache() && !isRefresh){
                Log.d(TAG,"Load Cache");
                try {
                    JSONArray data = new JSONArray(fileCacher.readCache().toString());
//                pushDataToWebView(data);
                    fetchDataFromAPI(endPointApi,cacheName);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                Log.d(TAG,"Load From APi");

                fetchDataFromAPI(endPointApi,cacheName);
                fetchDataExport(endPointApiExport,cacheNameExport);
            }
        }
        return true;
    }

    private void pushDataToWebView(JSONArray data){
        if (data.length() >0){
            try {
                JSONObject dObj = data.getJSONObject(0);
                String html = dObj.getString("html");
                html += hvpScript;
                Log.v(TAG,html);
                webView.loadData(html,"text/html","utf-8");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void fecthDataWhenDownloaded(String dir){
        webView.loadUrl("file:///"+Environment.getExternalStorageDirectory().getAbsolutePath()+"/PKPZonasi/web/www/index.html#"+dir);
    }

    public boolean loadOffline(){
        FileCacher fileCacher = new FileCacher(getApplicationContext(),cacheNameExport+".txt");
        if (fileCacher.hasCache()){
            try {
                String uri = fileCacher.readCache().toString();
                String[] url = uri.split("[/]");
                String filename = url[url.length-1];
                String[] fname = filename.split("[.]");
                String fxName = "";
                if (fname.length>0){
                    fxName = fname[0]+".zip";
                }else {
                    fxName = "default.zip";
                }
                String foldername = "hvp"+unitModule.getId();
                File file = new File(Environment.getExternalStorageDirectory()+"/PKPZonasi/web/workspace/"+foldername,fxName);
                Log.v(TAG,file.getAbsolutePath());
                if (file.exists()){
                    fecthDataWhenDownloaded(foldername);
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            Log.v(TAG,"No cahcefile "+cacheNameExport);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case WRITE_REQUEST_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG,"granted");
                }
                else{
                    Log.d(TAG,"Denied");
                }
                break;
        }
    }

    class CustomChrome extends WebChromeClient {

        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        CustomChrome() {}

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
    }

    class MyWebViewClient extends WebViewClient{

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            swipeContent.setRefreshing(false);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.v(TAG,url);
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }
}
