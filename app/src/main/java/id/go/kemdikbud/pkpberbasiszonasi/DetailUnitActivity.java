package id.go.kemdikbud.pkpberbasiszonasi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import id.go.kemdikbud.pkpberbasiszonasi.Helper.ApiService;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.CheckPermissionSD;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.FileHelper;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.IntentMod;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.SvgSoftwareLayerSetter;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.ViewDialog;
import id.go.kemdikbud.pkpberbasiszonasi.Master.Courses;
import id.go.kemdikbud.pkpberbasiszonasi.Master.ModuleContent;
import id.go.kemdikbud.pkpberbasiszonasi.Master.UnitCourses;
import id.go.kemdikbud.pkpberbasiszonasi.Master.UnitModule;

import id.go.kemdikbud.pkpberbasiszonasi.R;
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class DetailUnitActivity extends AppCompatActivity {
    private Courses courses;
    public String courses_string;

    private WebView contentSummary;

    private RecyclerView recyclerView;
    private DetailUnitActivity.UnitAdapter mAdapter;
    private List<UnitCourses> itemsList;

    private RecyclerView recyclerViewModule;
    private List<UnitModule> moduleList ;
    private DetailUnitActivity.ModuleAdapter moduleAdapter;

    public String TAG = DetailUnitActivity.class.getSimpleName();
    public SwipeRefreshLayout swipeRefreshLayout;

    public String cacheName= "";
    private int CourseId;
    private String CourseTitle;
    private String token ="";
    private JSONObject tokenObject;
    private ApiService apiService ;
    private String endPointApi = "";
    private FileHelper fileHelper;
    private int[] fileimage;



    private static final int WRITE_REQUEST_CODE =1;
    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_unit);

        apiService = new ApiService(this);

        fileimage = new int[]{R.drawable.in_1,R.drawable.in_2,R.drawable.on_1,R.drawable.in_3,
                R.drawable.on_2,R.drawable.in_4,R.drawable.on_3,R.drawable.in_5,R.drawable.laporan};

        fileHelper = new FileHelper();
        if (getIntent().getExtras() != null){
            try {
                Bundle bundle = getIntent().getExtras();
                CourseTitle = bundle.getString("title");
                CourseId = bundle.getInt("id");
                courses = new Gson().fromJson(bundle.getString("courses"),Courses.class);
                courses_string = bundle.getString("courses");
            }catch (Exception e){
                CourseId = 0;
                CourseTitle = "";
                courses_string = "";
                courses = new Courses();
                System.out.println(e);
            }
        }else{
            CourseId = 0;
            CourseTitle = "";
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData(true);
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerViewModule = findViewById(R.id.recycler_viewModule);

        itemsList = new ArrayList<>();
        mAdapter = new DetailUnitActivity.UnitAdapter(getApplicationContext(), itemsList,courses);


        moduleList = new ArrayList<>();
        moduleAdapter = new DetailUnitActivity.ModuleAdapter(getApplicationContext(),moduleList,courses);


        RecyclerView.LayoutManager mLayoutManager ;
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLayoutManager = new GridLayoutManager(this, 5);
        } else {
            mLayoutManager = new GridLayoutManager(this, 3);
        }
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(8), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        RecyclerView.LayoutManager moduleLayoutManager = new GridLayoutManager(this, 1);
        recyclerViewModule.setLayoutManager(moduleLayoutManager);
        recyclerViewModule.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(8), true));
        recyclerViewModule.setItemAnimator(new DefaultItemAnimator());
        recyclerViewModule.setAdapter(moduleAdapter);
        recyclerViewModule.setNestedScrollingEnabled(false);

        contentSummary = (WebView) findViewById(R.id.contentSummary);
        contentSummary.setWebChromeClient(new WebChromeClient());
        contentSummary.getSettings().setAllowFileAccess(true);
        contentSummary.getSettings().setAllowContentAccess(true);
        contentSummary.getSettings().setAllowFileAccessFromFileURLs(true);
        contentSummary.getSettings().setAllowUniversalAccessFromFileURLs(true);

        contentSummary.getSettings().setDomStorageEnabled(true);

        if (Build.VERSION.SDK_INT >= 23){
            requestPermissions(permissions, WRITE_REQUEST_CODE);
        }

        CheckPermissionSD checkPermissionSD = new CheckPermissionSD();
        checkPermissionSD.checkPermission(getApplicationContext(),this);

        FileCacher tokenString = new FileCacher(getApplicationContext(),"token.txt");
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

        Log.d(TAG,"Parameternya adalah idnya == "+CourseId);

        //Initial EndPoint API
        endPointApi += apiService.getEndPointAPI()+"webservice/rest/server.php?";
        endPointApi += "wstoken="+token+"&moodlewsrestformat=json&wsfunction=core_course_get_contents&courseid="+this.CourseId;
        cacheName = "course"+this.CourseId+token;

        refreshData(false);

        try{
            Toolbar toolbar = findViewById(R.id.toolbar);
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
            setTitle(CourseTitle);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public class UnitAdapter extends RecyclerView.Adapter<DetailUnitActivity.UnitAdapter.MyViewHolder> {
        private Context context;
        private List<UnitCourses> moduleList;
        private RequestBuilder<PictureDrawable> requestBuilder;
        private Courses currentCourse;


        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView name;
            public ImageView thumbnail,overflow;
            public View view;
            public CardView cardView;

            public MyViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.title);
                thumbnail = view.findViewById(R.id.thumbnail);
                cardView = view.findViewById(R.id.card_view);
                this.view = view;
            }
        }


        public UnitAdapter(Context context, List<UnitCourses> moduleList,Courses courses) {
            this.context = context;
            this.moduleList = moduleList;
            this.currentCourse = courses;
        }

        @Override
        public DetailUnitActivity.UnitAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_unit_row, parent, false);

            return new DetailUnitActivity.UnitAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final DetailUnitActivity.UnitAdapter.MyViewHolder holder, final int position) {
            final UnitCourses unitCourse = moduleList.get(position);
            holder.name.setText(unitCourse.getName());
            int banyakdata = moduleList.size();
            int startindex = banyakdata - fileimage.length;
            if (banyakdata>=11){
                if (position >= startindex){
                    int image = fileimage[position-startindex];
                    Glide.with(getApplicationContext()).load(image).into(holder.thumbnail);
                }else{
                    Glide.with(getApplicationContext()).load(R.drawable.unit).into(holder.thumbnail);
                }
            }else{
                Glide.with(getApplicationContext()).load(R.drawable.unit).into(holder.thumbnail);
            }
//            final String urlgambar = unitCourse.getModicon();
//            loadSVG(urlgambar,holder.thumbnail);
//            if (!unitCourse.getDescription().isEmpty()){
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    holder.description.setText(Html.fromHtml(unitCourse.getDescription(), Html.FROM_HTML_MODE_COMPACT));
//                } else {
//                    holder.description.setText(Html.fromHtml(unitCourse.getDescription()));
//                }
//
//                holder.description.setVisibility(View.VISIBLE);
//            }
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startModActivity(position);
                }
            });

            holder.thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startModActivity(position);
                }
            });
        }

        private void loadSVG(final String urlgambar, final ImageView thumbnail){
            requestBuilder =
                    Glide.with(context)
                            .as(PictureDrawable.class)
                            .apply(new RequestOptions()
                                    .centerCrop()
                                    .fitCenter()
                                    .error(R.drawable.loading1))
                            .transition(withCrossFade())
                            .listener(new SvgSoftwareLayerSetter());
            Glide.with(context).load(urlgambar).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    requestBuilder.load(urlgambar).into(thumbnail);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(thumbnail);
        }

        private void startModActivity(int position){
            Bundle bundle = new Bundle();
            bundle.putString("title",courses.getFullname());
            bundle.putInt("id",courses.getId());
            bundle.putInt("position",position+1);
            bundle.putString("courses",courses_string);
            Intent detail = new Intent(getApplicationContext(), DetailCoursesActivity.class);
            detail.putExtras(bundle);
            startActivity(detail);
        }

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

    private void fetchDataFromAPI(String uri, final String filename) {
        final ViewDialog viewDialog = new ViewDialog(this);
        viewDialog.showDialog();
        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,uri,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            FileCacher<String> fileCacher = new FileCacher<>(getApplicationContext(),filename+".txt");
                            if (fileCacher.hasCache()){
                                fileCacher.clearCache();
                                fileCacher.writeCache(response.toString());
                            }else {
                                fileCacher.writeCache(response.toString());
                            }

                            fetchDataToRecycleMenu(response);


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        viewDialog.hideDialog();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplication(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
                viewDialog.hideDialog();
            }
        });

        RequestAdapter.getInstance().addToRequestQueue(request);

    }

    public void fetchDataToRecycleMenu(JSONArray data){
        itemsList.clear();
        moduleList.clear();
        if (data.length() > 0){
            try {
                JSONObject objUnit = data.getJSONObject(0);
                UnitCourses unitCourses = new UnitCourses();
                unitCourses.setId(objUnit.getInt("id"));
                unitCourses.setName(objUnit.getString("name"));
                if (objUnit.has("visible")) unitCourses.setVisible(objUnit.getInt("visible"));
                else unitCourses.setVisible(-1);
                unitCourses.setSummary(objUnit.getString("summary"));
                unitCourses.setSummaryformat(objUnit.getInt("summaryformat"));
                if (objUnit.has("section")) unitCourses.setSection(objUnit.getInt("section"));
                else unitCourses.setSection(-1);
                if (objUnit.has("hiddenbynumsections")) unitCourses.setHiddenbynumsections(objUnit.getInt("hiddenbynumsections"));
                if (objUnit.has("uservisible")) unitCourses.setAvailabilityinfo(objUnit.getString("uservisible"));
                if (objUnit.has("modules")){
                    JSONArray modules = objUnit.getJSONArray("modules");
                    for (int i =0;i<modules.length();i++){
                        JSONObject objModule = modules.getJSONObject(i);
                        UnitModule unitModule = new UnitModule();
                        unitModule.setId(objModule.getInt("id"));
                        if (objModule.has("url")){
                            unitModule.setUrl(objModule.getString("url"));
                        }
                        if (objModule.has("description")){
                            unitModule.setDescription(objModule.getString("description"));
                        }
                        unitModule.setName(objModule.getString("name"));
                        unitModule.setInstance(objModule.getInt("instance"));
                        if (objModule.has("description")){
                            unitModule.setDescription(objModule.getString("description"));
                        }
                        unitModule.setVisible(objModule.getInt("visible"));
                        unitModule.setUservisible(objModule.getBoolean("uservisible"));
                        if (objModule.has("availabilityinfo")){
                            unitModule.setAvailabilityinfo(objModule.getString("availabilityinfo"));
                        }
                        unitModule.setVisibleoncoursepage(objModule.getInt("visibleoncoursepage"));
                        unitModule.setModicon(objModule.getString("modicon"));
                        unitModule.setModname(objModule.getString("modname"));
                        unitModule.setModplural(objModule.getString("modplural"));
                        if(objModule.has("availability")){
                            unitModule.setAvailability(objModule.getString("availability"));
                        }
                        unitModule.setIndent(objModule.getInt("indent"));
                        unitModule.setOnclick(objModule.getString("onclick"));
                        unitModule.setAfterlink(objModule.getString("afterlink"));
                        unitModule.setCustomdata(objModule.getString("customdata"));
                        unitModule.setCompletion(objModule.getInt("completion"));
                        if (objModule.has("contents")){
                            JSONArray content = objModule.getJSONArray("contents");
                            List<ModuleContent> mContent = new ArrayList<>();
                            for (int j=0;j<content.length();j++){
                                JSONObject objContent = content.getJSONObject(j);
                                ModuleContent moduleContent = new ModuleContent();
                                moduleContent.setType(objContent.getString("type"));
                                moduleContent.setFilename(objContent.getString("filename"));
                                moduleContent.setFilepath(objContent.getString("filepath"));
                                moduleContent.setFileurl(objContent.getString("fileurl"));
                                String timecreated = objContent.get("timecreated").toString();
                                if (timecreated.equals("null")){
                                    timecreated = "0";
                                }
                                moduleContent.setTimecreated(Integer.parseInt(timecreated));
                                moduleContent.setTimemodified(objContent.getInt("timemodified"));
                                moduleContent.setSortorder(objContent.getInt("sortorder"));
                                if (objContent.has("mimetype")){
                                    moduleContent.setMimetype(objContent.getString("mimetype"));
                                }
                                if (objContent.has("isexternalfile")){
                                    moduleContent.setIsexternalfile(objContent.getBoolean("isexternalfile"));
                                }
                                if (!objContent.isNull("userid")){
                                    moduleContent.setUserid(objContent.getInt("userid"));
                                }
                                moduleContent.setAuthor(objContent.getString("author"));
                                moduleContent.setLicense(objContent.getString("license"));
                                mContent.add(moduleContent);
                            }
                            unitModule.setContents(mContent);
                        }
                        moduleList.add(unitModule);
                    }
                }
                setDataToSummary(objUnit);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int i=1;i<data.length();i++){
                try {
                    JSONObject objUnit = data.getJSONObject(i);
                    UnitCourses unitCourses = new UnitCourses();
                    unitCourses.setId(objUnit.getInt("id"));
                    unitCourses.setName(objUnit.getString("name"));
                    if (objUnit.has("visible")) unitCourses.setVisible(objUnit.getInt("visible"));
                    else unitCourses.setVisible(-1);
                    unitCourses.setSummary(objUnit.getString("summary"));
                    unitCourses.setSummaryformat(objUnit.getInt("summaryformat"));
                    if (objUnit.has("section")) unitCourses.setSection(objUnit.getInt("section"));
                    else unitCourses.setSection(-1);
                    if (objUnit.has("hiddenbynumsections")) unitCourses.setHiddenbynumsections(objUnit.getInt("hiddenbynumsections"));
                    if (objUnit.has("uservisible")) unitCourses.setAvailabilityinfo(objUnit.getString("uservisible"));
                    itemsList.add(unitCourses);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        notifyDataChange();
        notifySummaryDataChange();
    }

    public void notifySummaryDataChange(){
        moduleAdapter.notifyDataSetChanged();
    }

    public void notifyDataChange(){
        mAdapter.notifyDataSetChanged();
    }

    private void refreshData(boolean isRefresh){
        FileCacher fileCacher = new FileCacher(getApplicationContext(),cacheName+".txt");
        if (fileCacher.hasCache() && !isRefresh){
            try {
                JSONArray jsonArray = new JSONArray(fileCacher.readCache().toString());
                fetchDataToRecycleMenu(jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            fetchDataFromAPI(endPointApi,cacheName);
        }
    }

    class ModuleAdapter extends RecyclerView.Adapter<DetailUnitActivity.ModuleAdapter.MyViewHolder> {
        private Context context;
        private List<UnitModule> moduleList;
        private RequestBuilder<PictureDrawable> requestBuilder;
        private Courses currentCourse;


        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView name;
            public ImageView thumbnail,thumbnailDownload,overflow;
            public CardView coursesItem;
            public TextView description;
            public View view;

            public MyViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.title);
                thumbnail = view.findViewById(R.id.thumbnail);
                thumbnailDownload = view.findViewById(R.id.thumbnailDownload);
                coursesItem = view.findViewById(R.id.card_view);
                description = view.findViewById(R.id.description);
                this.view = view;
            }
        }


        public ModuleAdapter(Context context, List<UnitModule> moduleList,Courses courses) {
            this.context = context;
            this.moduleList = moduleList;
            this.currentCourse = courses;
        }

        @Override
        public DetailUnitActivity.ModuleAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_module_row, parent, false);

            return new DetailUnitActivity.ModuleAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final DetailUnitActivity.ModuleAdapter.MyViewHolder holder, final int position) {
            final UnitModule unitModule = moduleList.get(position);
            holder.name.setText(unitModule.getName());
            final String urlgambar = unitModule.getModicon();
            loadSVG(urlgambar,holder.thumbnail);
            final IntentMod intentMod = new IntentMod(getApplicationContext());

            if (!unitModule.getDescription().isEmpty()){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    holder.description.setText(Html.fromHtml(unitModule.getDescription(), Html.FROM_HTML_MODE_COMPACT));
                } else {
                    holder.description.setText(Html.fromHtml(unitModule.getDescription()));
                }

                holder.description.setVisibility(View.VISIBLE);
            }
            if (unitModule.getModname().equals("label")){
                holder.description.setVisibility(View.GONE);
                holder.thumbnail.setVisibility(View.GONE);
                holder.thumbnailDownload.setVisibility(View.GONE);
            }
            holder.coursesItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intentMod.startModActivity(unitModule,currentCourse);
                }
            });

            holder.thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intentMod.startModActivity(unitModule,currentCourse);
                }
            });
        }

        private void loadSVG(final String urlgambar, final ImageView thumbnail){
            requestBuilder =
                    Glide.with(context)
                            .as(PictureDrawable.class)
                            .apply(new RequestOptions()
                                    .centerCrop()
                                    .fitCenter()
                                    .error(R.drawable.loading1))
                            .transition(withCrossFade())
                            .listener(new SvgSoftwareLayerSetter());
            Glide.with(context).load(urlgambar).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    requestBuilder.load(urlgambar).into(thumbnail);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(thumbnail);
        }

        private void showPopupMenu(View view) {
            // inflate menu
            PopupMenu popup = new PopupMenu(context, view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.option_course, popup.getMenu());
            popup.setOnMenuItemClickListener(new DetailUnitActivity.ModuleAdapter.MyMenuItemClickListener());
            popup.show();
        }



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

    public void setDataToSummary(JSONObject unitCourses){
        try {
            if (unitCourses.getInt("summaryformat") == 1){
                String Foldername = "";
                if (courses.getShortname().isEmpty()){
                    Foldername = courses.getFullname();
                }else{
                    Foldername = courses.getShortname();
                }
                String test = unitCourses.getString("summary").toString();
                String regex = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
                ArrayList link = pullLinks(test);
                String newLink = "";
                String fitImaget = "<style>img{display: inline;height: auto;max-width: 100%;}</style>";
                for (int i=0;i<link.size();i++){
                    String fname = "";
                    String[] tempname = link.get(i).toString().split("[/]");
                    if (link.get(i).toString().split("[?]").length >1){
                        String[] tempname2 = tempname[tempname.length-1].split("[?]");
                        fname = tempname2[0];
                        newLink = link.get(i).toString()+"&token="+token;
                    }else{
                        fname = tempname[tempname.length-1];
                        newLink = link.get(i).toString()+"?token="+token;
                    }

                    File file = new File(fileHelper.getBasePath()+"/"+Foldername+"/Unit"+unitCourses.getInt("id"), URLDecoder.decode(fname, StandardCharsets.UTF_8.name()));
                    if (file.exists()){
                        test = test.replace(link.get(i).toString(), "file://"+file.getAbsolutePath());
                    }else{
                        test = test.replace(link.get(i).toString(), newLink);
                    }
                }
                Log.v(TAG,"data = "+ test);
                contentSummary.loadDataWithBaseURL("",fitImaget+test,"text/html","utf-8","");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private ArrayList pullLinks(String text) {
        ArrayList links = new ArrayList();

        String regex = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        while(m.find()) {
            String urlStr = m.group();
            if (urlStr.startsWith("(") && urlStr.endsWith(")"))
            {
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }
            links.add(urlStr);
        }
        return links;
    }
}
