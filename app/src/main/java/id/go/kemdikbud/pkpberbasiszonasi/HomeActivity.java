package id.go.kemdikbud.pkpberbasiszonasi;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import id.go.kemdikbud.pkpberbasiszonasi.Fragment.EventFragment;
import id.go.kemdikbud.pkpberbasiszonasi.Fragment.HomeFragment;
import id.go.kemdikbud.pkpberbasiszonasi.Fragment.NotificationFragment;
import id.go.kemdikbud.pkpberbasiszonasi.Fragment.ProfileFragment;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.ApiService;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.DatabaseHelper;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.FormatHelper;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.ProfileHelper;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.ServerCallback;
import id.go.kemdikbud.pkpberbasiszonasi.Master.TokenMaster;

import id.go.kemdikbud.pkpberbasiszonasi.R;

import org.json.JSONObject;


public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private String fragname = "";
    private static final int WRITE_REQUEST_CODE =1;

    private String TAG = HomeActivity.class.getSimpleName();
    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    String endPointApi = "";
    ApiService apiService;
    JSONObject tokenObject;
    String token;
    FormatHelper formatHelper = new FormatHelper();

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        apiService = new ApiService(this);

        db = new DatabaseHelper(getApplicationContext());
        TokenMaster tokenMaster = db.getTokenMaster();
        token = tokenMaster.getToken();

        bottomNavigationView = (BottomNavigationView)findViewById(R.id.navigation);
        loadFragment(new HomeFragment(),"home");
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemReselectedListener);

        ProfileHelper profileHelper = new ProfileHelper(getApplicationContext(),this,token);
        String profileChaceName = "profile"+token;
        profileHelper.loadProfile(profileChaceName, true, new ServerCallback() {
            @Override
            public void onSuccess(JSONObject result) {

            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemReselectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            ActionBar myActionbar = getSupportActionBar();
            /*if (item.getItemId() == R.id.profile_nav){
                myActionbar.hide();
            }else{
                myActionbar.show();
            }*/

            switch (item.getItemId()) {
                case R.id.home_nav:
                    fragment = new HomeFragment();
                    loadFragment(fragment,"home");
                    return true;
                case R.id.event_nav:
                    fragment = new EventFragment();
                    loadFragment(fragment,"event");
                    return true;
                case R.id.notification_nav:
                    fragment = new NotificationFragment();
                    loadFragment(fragment,"notification");
                    return true;
                case R.id.profile_nav:
                    fragment = new ProfileFragment();
                    loadFragment(fragment,"profile");
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment,String name) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(name);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
//        for (int i=0;i<count;i++){
//            System.out.println("Fragment "+i+" "+getSupportFragmentManager().getBackStackEntryAt(i));
//        }
        if (count == 0) {
            super.onBackPressed();
        } else {
            try{
                String nav_name = getSupportFragmentManager().getBackStackEntryAt(count -2).getName();
                switch (nav_name){
                    case "home" :
                        bottomNavigationView.getMenu().getItem(0).setChecked(true);
                        break;
                    case "event" :
                        bottomNavigationView.getMenu().getItem(1).setChecked(true);
                        break;
                    case "notification" :
                        bottomNavigationView.getMenu().getItem(2).setChecked(true);
                        break;
                    case "profile" :
                        bottomNavigationView.getMenu().findItem(R.id.profile_nav).setChecked(true);
                        break;
                }
            }catch (Exception e){
                System.out.println("Error Set Selected" + count);
                finish();
            }
            getSupportFragmentManager().popBackStack();
        }
    }

}
