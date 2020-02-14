package id.go.kemdikbud.pkpberbasiszonasi.Fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
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
import id.go.kemdikbud.pkpberbasiszonasi.DetailUnitActivity;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.CheckConnection;
import id.go.kemdikbud.pkpberbasiszonasi.GridSpacingItemDecoration;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.ApiService;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.CheckPermissionSD;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.CollectingLink;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.CollectingLinkCallback;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.DownloadDialog;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.ViewDialog;
import id.go.kemdikbud.pkpberbasiszonasi.Master.Courses;
import id.go.kemdikbud.pkpberbasiszonasi.R;
import id.go.kemdikbud.pkpberbasiszonasi.RequestAdapter;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.kosalgeek.android.caching.FileCacher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyCoursesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<Courses> itemsList;
    private MyCoursesFragment.CoursesAdapter mAdapter;
    private Spinner spinnerItem;
    private String endPointApi = "";
    ApiService apiService;
    private String[] item_spinner = new String[]{"Semua","Berlangsung","Yang akan datang","Berlalu"};
    private String[] item_action = new String[]{"all","inprogress","future","past"};
    private HashMap<Integer,String> item_spinnerMap = new HashMap<Integer, String>();
    private SwipeRefreshLayout swipeContent;
    private CheckConnection checkConnection = new CheckConnection();
    private JSONObject tokenObject;
    private String token = "";
    private ShimmerFrameLayout shimmerFrameLayout;

    private static final int WRITE_REQUEST_CODE =1;
    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};


    private static final String TAG = MyCoursesFragment.class.getSimpleName();
    public MyCoursesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_course, container, false);

        apiService = new ApiService(getContext());
        shimmerFrameLayout = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();
        recyclerView = view.findViewById(R.id.recycler_view);
        itemsList = new ArrayList<>();
        mAdapter = new MyCoursesFragment.CoursesAdapter(getActivity(), itemsList);
        spinnerItem = (Spinner) view.findViewById(R.id.opt_course_show);
        swipeContent = (SwipeRefreshLayout) view.findViewById(R.id.swapContainer);

        // Get Token From cache file
        FileCacher tokenString = new FileCacher(getContext(),"token.txt");
        try {
            if (tokenString.hasCache()){
                tokenObject = new JSONObject(tokenString.readCache().toString());
                if (tokenObject.has("token")){
                    token = tokenObject.getString("token");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //Initial EndPoint API
        endPointApi += apiService.getEndPointAPI()+"webservice/rest/server.php?";
        endPointApi += "wstoken="+token+"&moodlewsrestformat=json&wsfunction=core_course_get_enrolled_courses_by_timeline_classification&classification=";

        // Add item to spinner
        for (int i=0;i<item_spinner.length;i++){
            item_spinnerMap.put(i,item_action[i]);
        }
        ArrayAdapter<String> adapter_opt = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,item_spinner);
        adapter_opt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerItem.setAdapter(adapter_opt);
        adapter_opt.notifyDataSetChanged();



        // add content
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(1), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        spinnerItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String Action = item_spinnerMap.get(position);
                itemsList.clear();
                refresh_data(Action,false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Set refresh layout
        swipeContent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String Action = item_spinnerMap.get(spinnerItem.getSelectedItemPosition());
                itemsList.clear();
                refresh_data(Action,true);
            }
        });

        if (Build.VERSION.SDK_INT >= 23){
            requestPermissions(permissions, WRITE_REQUEST_CODE);
        }

        PRDownloader.initialize(getContext());
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(getContext(), config);
        CheckPermissionSD checkPermissionSD = new CheckPermissionSD();
        checkPermissionSD.checkPermission(getContext(),getActivity());

        return view;
    }

    private void refresh_data(String Action,Boolean refresh){
        boolean isConnected = checkConnection.isInternetIsConnected(getContext());
        if (!isConnected){
            refresh = false;
        }
        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        switch (Action){
            case "all" :
                for (int i = 1;i<item_action.length;i++){
                    FileCacher itemCache = new FileCacher(getContext(),item_action[i]+token+".txt");
                    try {
                        if (refresh){
                            fetchDataFromAPI(endPointApi+item_action[i],item_action[i]+token);
                        }else if (itemCache.hasCache()){
                            String course = itemCache.readCache().toString();
                            try {
                                JSONArray js = new JSONArray(course);

                                fetchDataToItem(js);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else{
                            fetchDataFromAPI(endPointApi+item_action[i],item_action[i]+token);
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                break;
            default:
                FileCacher itemCache = new FileCacher(getContext(),Action+token+".txt");
                try {
                    if (refresh){
                        fetchDataFromAPI(endPointApi+Action,Action);
                    }else if (itemCache.hasCache()){
                        String course = itemCache.readCache().toString();
                        try {
                            JSONArray js = new JSONArray(course);
                            fetchDataToItem(js);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        fetchDataFromAPI(endPointApi+Action,Action+token);
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
                break;
        }
    }

    private void fetchDataFromAPI(String uri, final String filename) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,uri,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray itemCourse = response.getJSONArray("courses");
                            FileCacher<String> fileCacher = new FileCacher<>(getContext(),filename+".txt");
                            if (fileCacher.hasCache()){
                                fileCacher.clearCache();
                                fileCacher.writeCache(itemCourse.toString());
                            }else{
                                fileCacher.writeCache(itemCourse.toString());
                            }
                            fetchDataToItem(itemCourse);
                            notifyDataSetChange();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(), "Tidak dapat memuat, silahkan cek koneksi anda", Toast.LENGTH_SHORT).show();
                swipeContent.setRefreshing(false);
            }
        });

        RequestAdapter.getInstance().addToRequestQueue(request);
    }

    private void fetchDataToItem(JSONArray data){
        try{
            for (int i =0; i<data.length();i++){
                JSONObject courses_item = data.getJSONObject(i);
                Courses courses = new Courses();
                courses.setId(courses_item.getInt("id"));
                courses.setFullname(courses_item.getString("fullname"));
                courses.setShortname(courses_item.getString("shortname"));
                courses.setIdnumber(courses_item.getString("idnumber"));
                courses.setSummary(courses_item.getString("summary"));
                courses.setSummaryformat(courses_item.getInt("summaryformat"));
                courses.setStartdate(courses_item.getInt("startdate"));
                courses.setEnddate(courses_item.getInt("enddate"));
                courses.setFullnamedisplay(courses_item.getString("fullnamedisplay"));
                courses.setViewurl(courses_item.getString("viewurl"));
                courses.setCourseimage(courses_item.getString("courseimage"));
                courses.setProgres(courses_item.getInt("progress"));
                courses.setHasprogress(courses_item.getBoolean("hasprogress"));
                courses.setIsfavourite(courses_item.getBoolean("isfavourite"));
                courses.setHidden(courses_item.getBoolean("hidden"));
                courses.setShowshortname(courses_item.getBoolean("showshortname"));
                itemsList.add(courses);
            }

            notifyDataSetChange();
        }catch (JSONException e){
            e.printStackTrace();
        }
        swipeContent.setRefreshing(false);
    }

    private void notifyDataSetChange(){
        mAdapter.notifyDataSetChanged();
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    class CoursesAdapter extends RecyclerView.Adapter<MyCoursesFragment.CoursesAdapter.MyViewHolder> {
        private Context context;
        private List<Courses> coursesList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView name;
            public ImageView thumbnail,overflow;
            public CardView coursesItem;

            public MyViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.title);
                thumbnail = view.findViewById(R.id.thumbnail);
                coursesItem = view.findViewById(R.id.card_view);
                overflow = view.findViewById(R.id.overflow);
            }
        }


        public CoursesAdapter(Context context, List<Courses> coursesList) {
            this.context = context;
            this.coursesList = coursesList;
        }

        @Override
        public MyCoursesFragment.CoursesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_course_row, parent, false);

            return new MyCoursesFragment.CoursesAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyCoursesFragment.CoursesAdapter.MyViewHolder holder, final int position) {
            final Courses courses = coursesList.get(position);
            holder.name.setText(courses.getFullname());
            String urlgambar = courses.getCourseimage();
            Glide.with(context)
                    .load(urlgambar)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.loading)
                            .centerCrop())
                    .into(holder.thumbnail);
            holder.overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(holder.overflow,position);
                }
            });
            final Bundle bundle = new Bundle();
            bundle.putString("title",courses.getFullname());
            bundle.putInt("id",courses.getId());
            bundle.putString("courses",new Gson().toJson(courses));
            holder.coursesItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detail = new Intent(getContext(), DetailUnitActivity.class);
                    detail.putExtras(bundle);
                    startActivity(detail);
                }
            });

            holder.thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detail = new Intent(getContext(), DetailUnitActivity.class);
                    detail.putExtras(bundle);
                    startActivity(detail);
                }
            });
        }

        private void showPopupMenu(View view,int posisinya) {
            // inflate menu
            PopupMenu popup = new PopupMenu(context, view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.option_course, popup.getMenu());
            popup.setOnMenuItemClickListener(new MyMenuItemClickListener(posisinya));
            popup.show();
        }

        class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
            private int posisi;
            public MyMenuItemClickListener(int position) {
                this.posisi = position;
            }

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.download_button:
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Konfirmasi");
                        builder.setMessage("Apakah anda yakin akan mengunduh pembelanjaran ini?\n"+
                                "Mungkin memerlukan penyimpanan yang cukup besar.");
                        builder.setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int index = spinnerItem.getSelectedItemPosition();
                                Courses myCourses = itemsList.get(posisi);
                                String Foldername = "";
                                if (myCourses.getShortname().isEmpty()){
                                    Foldername = myCourses.getFullname();
                                }else{
                                    Foldername = myCourses.getShortname();
                                }
                                final String fxFoldername = Foldername;
                                String apiUrl = apiService.getEndPointAPI()+"webservice/rest/server.php?";
                                apiUrl += "wstoken="+token+"&moodlewsrestformat=json&wsfunction=core_course_get_contents&courseid="+myCourses.getId();
                                String cacheName = "course"+myCourses.getId()+token;
                                ViewDialog dialogDownload = new ViewDialog(getActivity());
                                CollectingLink collectingLink = new CollectingLink(getContext(),dialogDownload);
                                collectingLink.fetchLink(apiUrl, cacheName, new CollectingLinkCallback() {
                                    @Override
                                    public void onSuccess(List<ArrayList> result) {
                                        for (int a=0;a<result.size();a++){
                                            ArrayList<HashMap> hasil = result.get(a);
                                            for (int i=0;i<hasil.size();i++){
                                                HashMap<String,String> file = hasil.get(i);
                                                try{
                                                    Log.d(TAG,"Tipe "+file.get("type"));
                                                    if (file.get("type").equals("file") || file.get("type").equals("image")){
                                                        String url = file.get("url");
                                                        if (url.split("[?]").length>1){
                                                            url += "&token="+token;
                                                        }else{
                                                            url += "?token="+token;
                                                        }

                                                        try {
                                                            if (!fileExist(fxFoldername+"/"+file.get("folder"), URLDecoder.decode(file.get("filename"), StandardCharsets.UTF_8.name()))){
                                                                int downloadID = DownloadManager(fxFoldername+"/"+file.get("folder"),url,URLDecoder.decode(file.get("filename"), StandardCharsets.UTF_8.name()));
                                                            }
                                                        } catch (UnsupportedEncodingException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }catch (NullPointerException e){
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                });
//                        fetchDataForDownload(myCourses);
                            }
                        });
                        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        AlertDialog dialogconfirm = builder.create();
                        dialogconfirm.show();
                        return true;
                    default:
                }
                return false;
            }
        }

        public void fetchDataForDownload(Courses data){
            String Foldername = "";
            if (data.getShortname().isEmpty()){
                Foldername = data.getFullname();
            }else{
                Foldername = data.getShortname();
            }
            DownloadManager(Foldername,data.getCourseimage(),"banner.jpg");
        }

        @Override
        public int getItemCount() {
            return coursesList.size();
        }
    }

    public int DownloadManager(String Foldername, String Url, final String Filename){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)){

            File dirExternal = Environment.getExternalStorageDirectory();
            File createDir = new File(dirExternal.getAbsolutePath()+"/PKPZonasi/");

            if (!createDir.exists()){
                if (!createDir.mkdirs()){
                    Log.d(TAG,"Access Denied To Create"+createDir.getPath());
                }
            }

            final File destFile = new File(createDir.getAbsolutePath(),Foldername);
            if (!destFile.exists()){
                if (!destFile.mkdirs()){
                    Log.d(TAG,"Access Denied To Create"+destFile.getPath());
                }
            }
            final DownloadDialog downloadDialog = new DownloadDialog(getActivity());
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
                            renameFile(destFile.getAbsolutePath(),Filename);
                        }

                        @Override
                        public void onError(Error error) {
                            Log.d(TAG,"Download "+Filename+ " Was Error");
                        }
                    });
            return downloadId;
        }else {

        }
        return 0;
    }

    public void renameFile(String currentname,String foldername){
        String[] nameSplit = currentname.split("[.]");
        String newName = nameSplit[nameSplit.length-1]+".zip";
        File file = new File(foldername,currentname);
        File file2 = new File(foldername,newName);
        boolean success = file.renameTo(file2);
    }

    public boolean fileExist(String Foldername,String Filename){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {

            File dirExternal = Environment.getExternalStorageDirectory();
            File createDir = new File(dirExternal.getAbsolutePath() + "/PKPZonasi/"+Foldername,Filename);
            if (createDir.exists()){
                return true;
            }else{
                return false;
            }

        }
        return  false;
    }
/*
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
    }*/
}
