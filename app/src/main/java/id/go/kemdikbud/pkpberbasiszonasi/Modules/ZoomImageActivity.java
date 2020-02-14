package id.go.kemdikbud.pkpberbasiszonasi.Modules;

import android.Manifest;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;

import id.go.kemdikbud.pkpberbasiszonasi.Helper.CheckPermissionSD;
import id.go.kemdikbud.pkpberbasiszonasi.R;

public class ZoomImageActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private String url;
    private WebView webContent;
    private static final int WRITE_REQUEST_CODE =1;
    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);
        if (getIntent().getExtras() != null){
            try {
                Bundle bundle = getIntent().getExtras();
                url = bundle.getString("url");
            }catch (Exception e){
                url = "";
                e.printStackTrace();
            }
        }else{
            url = "";
        }

        webContent = (WebView) findViewById(R.id.webContent);

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
        setTitle(getResources().getString(R.string.app_name));

        webContent.getSettings().setAllowFileAccess(true);
        webContent.getSettings().setAllowContentAccess(true);
        webContent.getSettings().setJavaScriptEnabled(true);
        webContent.getSettings().setAllowFileAccessFromFileURLs(true);
        webContent.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webContent.getSettings().setDomStorageEnabled(true);
        webContent.getSettings().setBuiltInZoomControls(true);
        webContent.getSettings().setDisplayZoomControls(false);
        webContent.getSettings().setLoadWithOverviewMode(true);
        webContent.getSettings().setUseWideViewPort(true);


        if (Build.VERSION.SDK_INT >= 23){
            requestPermissions(permissions, WRITE_REQUEST_CODE);
        }

        CheckPermissionSD checkPermissionSD = new CheckPermissionSD();
        checkPermissionSD.checkPermission(this,this);

        LoadImage();
    }

    private void LoadImage(){
        webContent.loadUrl(this.url);
    }

}
