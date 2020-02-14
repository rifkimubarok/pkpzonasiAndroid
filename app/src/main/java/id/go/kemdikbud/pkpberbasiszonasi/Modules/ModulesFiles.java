package id.go.kemdikbud.pkpberbasiszonasi.Modules;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;

import id.go.kemdikbud.pkpberbasiszonasi.GridSpacingItemDecoration;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.ApiService;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.CheckPermissionSD;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.DownloadDialog;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.SvgSoftwareLayerSetter;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.ViewDialog;
import id.go.kemdikbud.pkpberbasiszonasi.Master.Courses;
import id.go.kemdikbud.pkpberbasiszonasi.Master.ModuleContent;
import id.go.kemdikbud.pkpberbasiszonasi.Master.UnitModule;
import id.go.kemdikbud.pkpberbasiszonasi.R;
import com.google.gson.Gson;
import com.kosalgeek.android.caching.FileCacher;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ModulesFiles extends AppCompatActivity {
    public String modules="";
    public Courses courses;
    public UnitModule unitModule;
    public Toolbar toolbar;
    private String hvpScript ="";

    private List<ModuleContent> itemsList;
    private ModulesFiles.UnitAdapter mAdapter;
    private RecyclerView recyclerView;


    private ApiService apiService;
    private String endPointApi = "";
    private String cacheName= "";
    private String token ="";
    private JSONObject tokenObject;
    final ViewDialog viewDialog = new ViewDialog(this);
    final DownloadDialog downloadDialog = new DownloadDialog(this);


    private static final int WRITE_REQUEST_CODE =1;
    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private String TAG = ModulesFiles.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modules_files);

        apiService = new ApiService(this);
        if (getIntent().getExtras() != null){
            try {
                Bundle bundle = getIntent().getExtras();
                modules = bundle.getString("modules");
                this.courses = new Gson().fromJson(bundle.getString("courses"),Courses.class);
            }catch (Exception e){
                modules = "";
                e.printStackTrace();
            }
        }else{
            modules = "";
        }
        if (!modules.equals("")){
            unitModule = new Gson().fromJson(modules, UnitModule.class);
        }else{
            unitModule = new UnitModule();
        }

        //GET TOKEN
        FileCacher tokenString = new FileCacher(this,"token.txt");
        String token ="";
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

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        itemsList = new ArrayList<>();
        mAdapter = new ModulesFiles.UnitAdapter(getBaseContext(), itemsList,courses);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(8), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        setDataToItem(unitModule.getContents());

        if (Build.VERSION.SDK_INT >= 23){
            requestPermissions(permissions, WRITE_REQUEST_CODE);
        }

        PRDownloader.initialize(this);
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(this, config);
        CheckPermissionSD checkPermissionSD = new CheckPermissionSD();
        checkPermissionSD.checkPermission(this,this);

    }


    private void setDataToItem(List<ModuleContent> moduleContents){
        itemsList.clear();
        for (int i=0;i<moduleContents.size();i++){
            ModuleContent mContent = moduleContents.get(i);
            itemsList.add(mContent);
        }

        notifyChangeData();
    }

    private void notifyChangeData(){
        mAdapter.notifyDataSetChanged();
    }


    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    class UnitAdapter extends RecyclerView.Adapter<ModulesFiles.UnitAdapter.MyViewHolder> {
        private Context context;
        private List<ModuleContent> moduleList;
        private RequestBuilder<PictureDrawable> requestBuilder;
        public Courses currentCourse;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView name;
            public ImageView thumbnail,overflow;
            public CardView coursesItem;

            public MyViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.title);
                thumbnail = view.findViewById(R.id.thumbnail);
                coursesItem = view.findViewById(R.id.card_view);
            }
        }


        public UnitAdapter(Context context, List<ModuleContent> moduleList,Courses courses) {
            this.context = context;
            this.moduleList = moduleList;
            this.currentCourse = courses;
        }

        @Override
        public ModulesFiles.UnitAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_module_row, parent, false);

            return new ModulesFiles.UnitAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ModulesFiles.UnitAdapter.MyViewHolder holder, final int position) {
            final ModuleContent moduleContent = moduleList.get(position);
            holder.name.setText(moduleContent.getFilename());
            String mime = moduleContent.getFilename().substring(moduleContent.getFilename().lastIndexOf("."));
            Log.v("Tipe File ",mime);
            if (mime.equals(".docx") || mime.equals(".doc")){
                Glide.with(context).load(R.drawable.doc).into(holder.thumbnail);
            }else if (mime.equals(".ppt") || mime.equals(".pptx")){
                Glide.with(context).load(R.drawable.ppt).into(holder.thumbnail);
            }else if (mime.equals(".xls") || mime.equals(".xlsx")){
                Glide.with(context).load(R.drawable.xls).into(holder.thumbnail);
            }else if (mime.equals(".pdf")){
                Glide.with(context).load(R.drawable.pdf).into(holder.thumbnail);
            }else{
                Glide.with(context).load(R.drawable.file).into(holder.thumbnail);
            }
//            String urlgambar = unitModule.get();
            requestBuilder =
                    Glide.with(context)
                            .as(PictureDrawable.class)
                            .apply(new RequestOptions()
                                    .centerCrop()
                                    .fitCenter()
                                    .error(R.drawable.loading1))
                            .transition(withCrossFade())
                            .listener(new SvgSoftwareLayerSetter());
//            requestBuilder.load(urlgambar).into(holder.thumbnail);

            holder.coursesItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadDialog.showDialog();
                    FileCacher tokenString = new FileCacher(context,"token.txt");
                    String token1 ="";
                    try {
                        if (tokenString.hasCache()){
                            tokenObject = new JSONObject(tokenString.readCache().toString());
                            if (tokenObject.has("token")){
                                token1 = tokenObject.getString("token");
                            }
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String urlFile = "";
                    if (moduleContent.getFileurl().split("[?]").length>1){
                        urlFile = moduleContent.getFileurl()+"&token="+token1;
                    }else{
                        urlFile = moduleContent.getFileurl()+"?token="+token1;
                    }
                    String Filename = moduleContent.getFilename();
                    String foldername = "";
                    if (currentCourse.getShortname().isEmpty()){
                        foldername = currentCourse.getFullname();
                    }else{
                        foldername = currentCourse.getShortname();
                    }
                    foldername += "/Modules"+unitModule.getId()+"/"+unitModule.getName();
                    if (!fileExist(foldername,Filename)){
                        DownloadManager(foldername,urlFile,Filename);
                    }else{
                        downloadDialog.hideDialog();
                    }
                }
            });

            holder.thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        /*private void showPopupMenu(View view) {
            // inflate menu
            PopupMenu popup = new PopupMenu(context, view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.option_course, popup.getMenu());
            popup.setOnMenuItemClickListener(new ContentCoursesFragment.UnitAdapter.MyMenuItemClickListener());
            popup.show();
        }*/


        class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

            public MyMenuItemClickListener() {
            }

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.download_button:
                        Toast.makeText(context, "Downloading", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                }
                return false;
            }
        }

        @Override
        public int getItemCount() {
            return moduleList.size();
        }
    }

    public int DownloadManager(final String Foldername, String Url, final String Filename){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)){

            File dirExternal = Environment.getExternalStorageDirectory();
            File createDir = new File(dirExternal.getAbsolutePath()+"/PKPZonasi/");

            if (!createDir.exists()){
                if (!createDir.mkdir()){
                    Log.d(TAG,"Access Denied To Create"+createDir.getPath());
                }
            }

            File destFile = new File(createDir.getAbsolutePath(),Foldername);
            if (!destFile.exists()){
                if (!destFile.mkdirs()){
                    Log.d(TAG,"Access Denied To Create"+destFile.getPath());
                }
            }

            int downloadId = PRDownloader.download(Url, destFile.getAbsolutePath(), Filename)
                    .build()
                    .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                        @Override
                        public void onStartOrResume() {
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
                            fileExist(Foldername,Filename);
                        }

                        @Override
                        public void onError(Error error) {
                            Toast.makeText(getApplicationContext(),"Tidak Dapat membuka File "+Filename,Toast.LENGTH_SHORT).show();
                            downloadDialog.hideDialog();
                        }
                    });
            return downloadId;
        }else {

        }
        return 0;
    }

    public boolean fileExist(String Foldername,String Filename){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {

            File dirExternal = Environment.getExternalStorageDirectory();
            File createDir = new File(dirExternal.getAbsolutePath() + "/PKPZonasi/");

            if (!createDir.exists()) {
                if (!createDir.mkdir()) {
                    Log.d(TAG, "Access Denied To Create" + createDir.getPath());
                }
            }

            File destFile = new File(createDir.getAbsolutePath(), Foldername);
            if (!destFile.exists()) {
                if (!destFile.mkdirs()) {
                    Log.d(TAG, "Access Denied To Create" + destFile.getPath());
                }
            }

            File file = new File(destFile.getAbsolutePath(),Filename);
            if (file.exists()){
                try{
                    // Get URI and MIME type of file
                    Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file);
                    String mime = getContentResolver().getType(uri);

                    // Open file with user selected app
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, mime);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                    return true;
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Tidak ada aplikasi untuk membuka file dokumen.",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            return  false;
        }
        return  false;
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
}
