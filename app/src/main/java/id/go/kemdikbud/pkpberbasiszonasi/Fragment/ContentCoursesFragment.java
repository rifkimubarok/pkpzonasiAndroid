package id.go.kemdikbud.pkpberbasiszonasi.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import id.go.kemdikbud.pkpberbasiszonasi.GridSpacingItemDecoration;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.ApiService;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.CheckPermissionSD;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.CustomAdapterSpinner;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.FileHelper;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.IntentMod;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.SvgSoftwareLayerSetter;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.ViewDialog;
import id.go.kemdikbud.pkpberbasiszonasi.Master.Courses;
import id.go.kemdikbud.pkpberbasiszonasi.Master.ModuleContent;
import id.go.kemdikbud.pkpberbasiszonasi.Master.UnitModule;
import id.go.kemdikbud.pkpberbasiszonasi.Modules.ExoPlayerActivity;

import id.go.kemdikbud.pkpberbasiszonasi.R;
import id.go.kemdikbud.pkpberbasiszonasi.RequestAdapter;
import com.google.gson.Gson;
import com.kosalgeek.android.caching.FileCacher;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ContentCoursesFragment extends Fragment {
    public Courses courses;
    private RecyclerView recyclerView;
    private List<UnitModule> itemsList;
    private List<String> itemSpinner;
    private CustomAdapterSpinner sAdapter;
    private ContentCoursesFragment.UnitAdapter mAdapter;
    private ApiService apiService ;
    private String endPointApi = "";
    private int CourseId = 0;
    private int clickPosition = 0;
    private String CourseTitle = "";
    private Spinner spinnerItem;
    private WebView contentSummary;
    public String cacheName= "";
    private String token ="";
    private JSONObject tokenObject;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout layoutWebView;
    private FileHelper fileHelper = new FileHelper();
    private ImageView videoView;
    private RelativeLayout playerViewLayout;
//    private NestedScrollView layoutNestedScrollView;
    ProgressDialog pDialog;
    MediaController mediaController;
    String urlVideo="";
    IntentMod intentMod;

    private final String TAG = ContentCoursesFragment.class.getSimpleName();

    private static final int WRITE_REQUEST_CODE =1;
    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private View mCustomView;

    public ContentCoursesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.CourseId = getArguments().getInt("id");
            this.clickPosition = getArguments().getInt("position");
            this.CourseTitle = getArguments().getString("title");
            this.courses = new Gson().fromJson(getArguments().getString("courses"),Courses.class);
            Log.d(TAG,"Courses nya adalha "+courses.getShortname());
        }else{
            this.CourseId = 0;
            this.clickPosition = -1;
            this.CourseTitle = "";
            this.courses = new Courses();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_content_courses, container, false);

        apiService = new ApiService(getContext());

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeContent);
        layoutWebView = (LinearLayout) view.findViewById(R.id.layoutWebView);
        Button imageClick = new Button(getContext());
        // Video view
        videoView = (ImageView) view.findViewById(R.id.playerView);
        playerViewLayout = (RelativeLayout) view.findViewById(R.id.playerViewLayout);
//        layoutNestedScrollView = (NestedScrollView) view.findViewById(R.id.layoutNestedScrollView);

        contentSummary = (WebView) view.findViewById(R.id.contentSummary);
        contentSummary.setWebChromeClient(new MyWebChromeClient());
        contentSummary.getSettings().setAllowFileAccess(true);
        contentSummary.getSettings().setAllowContentAccess(true);
        contentSummary.getSettings().setAllowFileAccessFromFileURLs(true);
        contentSummary.getSettings().setAllowUniversalAccessFromFileURLs(true);
        contentSummary.getSettings().setBuiltInZoomControls(false);
        contentSummary.getSettings().setDisplayZoomControls(false);
        contentSummary.getSettings().setDomStorageEnabled(true);
        contentSummary.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        contentSummary.setScrollbarFadingEnabled(false);
        contentSummary.setVerticalScrollBarEnabled(true);
        contentSummary.getSettings().setJavaScriptEnabled(true);
        contentSummary.setHorizontalScrollBarEnabled(true);
        contentSummary.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (contentSummary.getScrollY() == 0){
                    swipeRefreshLayout.setEnabled(true);
                }else{
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });

        intentMod = new IntentMod(getContext());
        contentSummary.addJavascriptInterface(new Object(){
            @JavascriptInterface           // For API 17+
            public void performClick(String strl)
            {
                intentMod.zoomImage(strl);
            }
        },"imgClick");

        if (Build.VERSION.SDK_INT >= 23){
            requestPermissions(permissions, WRITE_REQUEST_CODE);
        }

        CheckPermissionSD checkPermissionSD = new CheckPermissionSD();
        checkPermissionSD.checkPermission(getContext(),getActivity());

//        enableHTML5AppCache();

        // SET TOKEN
        // Get Token From cache file
        FileCacher tokenString = new FileCacher(getContext(),"token.txt");
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

        // Initial Content

        recyclerView = view.findViewById(R.id.recycler_view);
        itemsList = new ArrayList<>();
        mAdapter = new ContentCoursesFragment.UnitAdapter(getActivity(), itemsList,courses);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(8), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        // Initial spinner
        spinnerItem = (Spinner) view.findViewById(R.id.opt_unit_course);
        itemSpinner = new ArrayList<>();
        sAdapter = new CustomAdapterSpinner(getActivity(),itemSpinner);

        spinnerItem.setAdapter(sAdapter);
        spinnerItem.setOnItemSelectedListener(spinnerOnSelectedItem);
        refreshData(false);

        // Initial SwipeLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData(true);
            }
        });

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("videouri",urlVideo);
                Intent intent = new Intent(getContext(), ExoPlayerActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return view;
    }

    private AdapterView.OnItemSelectedListener spinnerOnSelectedItem = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            setDataToSummary(position,cacheName);
            fetchDataToModule(position,cacheName);
            clickPosition = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void refreshData(boolean isRefresh){
        itemSpinner.clear();
        FileCacher fileCacher = new FileCacher(getContext(),cacheName+".txt");
        if (fileCacher.hasCache() && !isRefresh){
            try {
                JSONArray jsonArray = new JSONArray(fileCacher.readCache().toString());
                fetchDataToSpinner(jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            fetchDataFromAPI(endPointApi,cacheName);
        }
    }

    private void fetchDataFromAPI(String uri, final String filename) {
        final ViewDialog viewDialog = new ViewDialog(getActivity());
        viewDialog.showDialog();
        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,uri,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            FileCacher<String> fileCacher = new FileCacher<>(getContext(),filename+".txt");
                            if (fileCacher.hasCache()){
                                fileCacher.clearCache();
                                fileCacher.writeCache(response.toString());
                            }else {
                                fileCacher.writeCache(response.toString());
                            }

                            fetchDataToSpinner(response);


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
                Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
                viewDialog.hideDialog();
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

    public void fetchDataToSpinner(JSONArray data){
        for (int i = 0;i<data.length();i++){
            JSONObject obj = null;
            try {
                obj = data.getJSONObject(i);
                itemSpinner.add(obj.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        notifyDataChangeSpinner();
        if (this.clickPosition >=0){
            spinnerItem.setSelection(this.clickPosition);
        }
    }

    public void notifyDataChangeSpinner(){
        sAdapter.notifyDataSetChanged();
    }

    public void setDataToSummary(int position,String filename){
        String script = "<script>" +
                "        window.onload = function () {" +
                "            var test = document.getElementsByTagName('img');" +
                "            for (let i = 0; i < test.length; i++) {" +
                "                test[i].addEventListener('click', function (e) {" +
                "                    imgClick.performClick(this.src)" +
                "                })" +
                "            }" +
                "        }" +
                "    </script>";
        FileCacher fileCacher = new FileCacher(getContext(),filename+".txt");
        if (fileCacher.hasCache()){
            try {
                JSONArray data = new JSONArray(fileCacher.readCache().toString());
                JSONObject unitCourses = data.getJSONObject(position);
                if (unitCourses.getInt("summaryformat") == 1){
                    String Foldername = "";
                    if (courses.getShortname().isEmpty()){
                        Foldername = courses.getFullname();
                    }else{
                        Foldername = courses.getShortname();
                    }
                    String test = unitCourses.getString("summary");
                    Log.v(TAG,test);
                    String regex = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
                    String regex_video = "<(video).*?>[\\s\\S]*?<\\/\\1>";
                    ArrayList link = pullLinks(test);
                    String newLink = "";
                    String fitImaget = "<style>img{display: inline;height: auto;max-width: 100%;}</style>";
                    boolean containVideo = false;
                    ArrayList<String> url_video = new ArrayList<>();
                    for (int i=0;i<link.size();i++){
                        String fname = "";

                        String[] tempname = link.get(i).toString().split("[/]");
                        int jmlQ = link.get(i).toString().split("[?]").length;
                        if ( jmlQ >1){
                            String[] tempname2 = tempname[tempname.length-1].split("[?]");
                            fname = tempname2[0];
                            newLink = link.get(i).toString()+"&token="+token;
                            String urlVideo = link.get(i).toString().split("[?]")[0];
                            if (fileHelper.isVideoFile(urlVideo)){
                                containVideo = true;
                                url_video.add(newLink);
                            }
                        }else{
                            fname = tempname[tempname.length-1];
                            newLink = link.get(i).toString()+"?token="+token;
                            if (fileHelper.isVideoFile(link.get(i).toString())){
                                containVideo = true;
                                url_video.add(newLink);
                            }
                        }

                        String ext = FilenameUtils.getExtension(link.get(i).toString());

                        File file = new File(fileHelper.getBasePath()+"/"+Foldername+"/Unit"+unitCourses.getInt("id"), URLDecoder.decode(fname, StandardCharsets.UTF_8.name()));
                        if (file.exists()){
                            test = test.replace(link.get(i).toString(), "file://"+file.getAbsolutePath());
                        }else{
                            test = test.replace(link.get(i).toString(), newLink);
                        }
                    }
                    if (containVideo){
                        String url = url_video.get(0);
                        urlVideo = url;
                        playerViewLayout.setVisibility(View.VISIBLE);
                        test = test.replaceAll(regex_video,"");
                    }else{
                        playerViewLayout.setVisibility(View.GONE);
                    }
                    contentSummary.loadDataWithBaseURL("",fitImaget+test+script,"text/html","utf-8","");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    public void fetchDataToModule(int position,String filename) {
        itemsList.clear();
        FileCacher fileCacher = new FileCacher(getContext(), filename + ".txt");
        if (fileCacher.hasCache()) {
            try {
                JSONArray data = new JSONArray(fileCacher.readCache().toString());
                JSONObject unitCourses = data.getJSONObject(position);
                if (unitCourses.has("modules")){
                    JSONArray modules = unitCourses.getJSONArray("modules");
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
                        itemsList.add(unitModule);
                    }
                    notifyDataChangeModule();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyDataChangeModule(){
        mAdapter.notifyDataSetChanged();
    }

    public int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    class UnitAdapter extends RecyclerView.Adapter<ContentCoursesFragment.UnitAdapter.MyViewHolder> {
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


        public UnitAdapter(Context context, List<UnitModule> moduleList,Courses courses) {
            this.context = context;
            this.moduleList = moduleList;
            this.currentCourse = courses;
        }

        @Override
        public ContentCoursesFragment.UnitAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_module_row, parent, false);

            return new ContentCoursesFragment.UnitAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ContentCoursesFragment.UnitAdapter.MyViewHolder holder, final int position) {
            final UnitModule unitModule = moduleList.get(position);
            holder.name.setText(unitModule.getName());
            final String urlgambar = unitModule.getModicon();
            final IntentMod intentMod = new IntentMod(getContext());

            loadSVG(urlgambar,holder.thumbnail);
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
            popup.setOnMenuItemClickListener(new ContentCoursesFragment.UnitAdapter.MyMenuItemClickListener());
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

    private void enableHTML5AppCache() {


        // Set cache size to 8 mb by default. should be more than enough
        contentSummary.getSettings().setAppCacheMaxSize(1024*1024*8);

        // This next one is crazy. It's the DEFAULT location for your app's cache
        // But it didn't work for me without this line
        File dir = getActivity().getCacheDir();

        if (!dir.exists()) {
            dir.mkdirs();
        }

        contentSummary.getSettings().setAllowFileAccess(true);
        contentSummary.getSettings().setAllowContentAccess(true);
        contentSummary.getSettings().setAllowFileAccessFromFileURLs(true);
        contentSummary.getSettings().setAllowUniversalAccessFromFileURLs(true);

        contentSummary.getSettings().setDomStorageEnabled(true);

        contentSummary.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
    }

    private class MyWebChromeClient extends WebChromeClient {
        private int mOriginalOrientation;
        private FullscreenHolder mFullscreenContainer;
        private CustomViewCallback mCustomViewCollback;
        private Activity mActivity = getActivity();

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {

            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }

            mOriginalOrientation = mActivity.getRequestedOrientation();

            FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();

            mFullscreenContainer = new FullscreenHolder(mActivity);
            mFullscreenContainer.addView(view, ViewGroup.LayoutParams.MATCH_PARENT);
            decor.addView(mFullscreenContainer, ViewGroup.LayoutParams.MATCH_PARENT);
            mCustomView = view;
            mCustomViewCollback = callback;
            mActivity.setRequestedOrientation(mOriginalOrientation);

        }

        @Override
        public void onHideCustomView() {
            if (mCustomView == null) {
                return;
            }

            FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
            decor.removeView(mFullscreenContainer);
            mFullscreenContainer = null;
            mCustomView = null;
            mCustomViewCollback.onCustomViewHidden();
            // show the content view.

            mActivity.setRequestedOrientation(mOriginalOrientation);
        }


        class FullscreenHolder extends FrameLayout {

            public FullscreenHolder(Context ctx) {
                super(ctx);
                setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
            }

            @Override
            public boolean onTouchEvent(MotionEvent evt) {
                return true;
            }
        }
    }
}
