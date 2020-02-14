package id.go.kemdikbud.pkpberbasiszonasi;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import id.go.kemdikbud.pkpberbasiszonasi.Fragment.ContentCoursesFragment;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.ApiService;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.CheckConnection;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.ViewDialog;
import id.go.kemdikbud.pkpberbasiszonasi.Master.NavigationOption;

import com.h6ah4i.android.tablayouthelper.TabLayoutHelper;
import com.kosalgeek.android.caching.FileCacher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DetailCoursesActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private int CourseId;
    private int clickPosition;
    private String CourseTitle;
    private String token="";
    private JSONObject tokenObject;
    private String endPointApi = "";
    private ApiService apiService;
    private String cacheName="";
    private List<NavigationOption> itemList;
    private ViewDialog dialog = new ViewDialog(this);

    public String courses;

    private final String TAG = DetailCoursesActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_courses);
        apiService = new ApiService(this);
        // PASS Parameter from Activity
        if (getIntent().getExtras() != null){
            try {
                Bundle bundle = getIntent().getExtras();
                CourseTitle = bundle.getString("title");
                CourseId = bundle.getInt("id");
                clickPosition = bundle.getInt("position");
                courses = bundle.getString("courses");
            }catch (Exception e){
                CourseId = 0;
                CourseTitle = "";
                courses = "";
                clickPosition = -1;
                System.out.println(e);
            }
        }else{
            CourseId = 0;
            CourseTitle = "";
        }
        itemList = new ArrayList<>();

        // GET TOKEN LOGIN
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

        // Initial endPoint
        //Initial EndPoint API
        endPointApi += apiService.getEndPointAPI()+"webservice/rest/server.php?";
        endPointApi += "wstoken="+token+"&moodlewsrestformat=json&wsfunction=core_course_get_user_navigation_options&courseids[0]="+this.CourseId;
        cacheName = "courseNavigation"+this.CourseId+token;

        viewPager = (ViewPager)findViewById(R.id.viewpager);
//        setupViewPager(viewPager);
        tabLayout = (TabLayout)findViewById(R.id.tabs);

        notifyDataChange();

//        refreshData();

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

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.clearList();
        Bundle bundle = new Bundle();
        bundle.putInt("id",CourseId);
        bundle.putString("title",CourseTitle);
        bundle.putString("courses",courses);
        bundle.putInt("position",clickPosition);

        ContentCoursesFragment contentCoursesFragment = new ContentCoursesFragment();
        contentCoursesFragment.setArguments(bundle);
        adapter.addFragment(contentCoursesFragment, "Konten");
        if (itemList.size()>4){
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }else{
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        }
        for (int i=0;i<itemList.size();i++){
            NavigationOption opt = itemList.get(i);
            if (opt.isAvailable()){
                addFragmentToAdapter(adapter,bundle,opt.getName());
            }
        }

        if (itemList.size() == 0){
            viewPager.setAdapter(adapter);
        }

        tabLayout.setupWithViewPager(viewPager);

        TabLayoutHelper mTabLayoutHelper = new TabLayoutHelper(tabLayout,viewPager);
        mTabLayoutHelper.setAutoAdjustTabModeEnabled(true);


//        adapter.addFragment(new CoursesFragment(), "Kursus Tersedia");
    }

    private void addFragmentToAdapter(ViewPagerAdapter adapter,Bundle bundle,String optName){
        switch (optName){
            /*case "badges":
                ContentCoursesFragment badge = new ContentCoursesFragment();
                badge.setArguments(bundle);
                adapter.addFragment(badge,"Penghargaan");
                break;
            case "blogs":
                ContentCoursesFragment blogs = new ContentCoursesFragment();
                blogs.setArguments(bundle);
                adapter.addFragment(blogs,"Blog");
                break;*/
            case "calendar":
                ContentCoursesFragment calender = new ContentCoursesFragment();
                calender.setArguments(bundle);
                adapter.addFragment(calender,"Kalender");

                break;
            case "competencies":
                ContentCoursesFragment competencies = new ContentCoursesFragment();
                competencies.setArguments(bundle);
                adapter.addFragment(competencies,"Kompetensi");
                break;
            case "grades":
                ContentCoursesFragment grades = new ContentCoursesFragment();
                grades.setArguments(bundle);
                adapter.addFragment(grades,"Nilai-nilai");
                break;
            case "notes":
                ContentCoursesFragment notes = new ContentCoursesFragment();
                notes.setArguments(bundle);
                adapter.addFragment(notes,"Catatan");
                break;
            case "participants":
                ContentCoursesFragment participants = new ContentCoursesFragment();
                participants.setArguments(bundle);
                adapter.addFragment(participants,"peserta");
                break;
            case "search":
                ContentCoursesFragment search = new ContentCoursesFragment();
                search.setArguments(bundle);
                adapter.addFragment(search,"Pencarian");
                break;
            /*case "tags":
                ContentCoursesFragment tags = new ContentCoursesFragment();
                tags.setArguments(bundle);
                adapter.addFragment(tags,"Tag");
                break;*/

        }
        viewPager.setAdapter(adapter);
    }

    private void fetchDataFromAPI(String uri, final String filename) {
        dialog.showDialog();
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,uri,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray coursesNav = response.getJSONArray("courses");
                            for (int i=0; i<coursesNav.length();i++){
                                JSONObject objNav = coursesNav.getJSONObject(i);
                                if (objNav.getInt("id") == CourseId){
                                    JSONArray option = objNav.getJSONArray("options");
                                    fetchDataToMenu(option);
                                    FileCacher<String> fileCacher = new FileCacher<>(getApplicationContext(),filename);
                                    if (fileCacher.hasCache()){
                                        fileCacher.clearCache();
                                        fileCacher.writeCache(option.toString());
                                    }else{
                                        fileCacher.writeCache(option.toString());
                                    }
                                }
                            }
                            Log.d(TAG,response.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialog.hideDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Cek Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
                dialog.hideDialog();
            }
        });

        RequestAdapter.getInstance().addToRequestQueue(request);
    }

    private void fetchDataToMenu(JSONArray data){
        try{
            for (int j=0;j<data.length();j++){
                JSONObject nav = data.getJSONObject(j);
                NavigationOption opt = new NavigationOption();
                opt.setName(nav.getString("name"));
                opt.setAvailable(nav.getBoolean("available"));
                itemList.add(opt);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        notifyDataChange();
    }

    private void notifyDataChange(){
        setupViewPager(viewPager);
    }

    private void refreshData(){
        CheckConnection network = new CheckConnection();

        if (network.isInternetIsConnected(this)){
            fetchDataFromAPI(endPointApi,cacheName);
        }else{
            FileCacher fileCacher = new FileCacher(this,cacheName);
            if (fileCacher.hasCache()){
                try{
                    String data = fileCacher.readCache().toString();
                    fetchDataToMenu(new JSONArray(data));
                }catch (IOException e){
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                fetchDataFromAPI(endPointApi,cacheName);
            }
        }
    }
}
