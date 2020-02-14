package id.go.kemdikbud.pkpberbasiszonasi.Fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.TextView;

import id.go.kemdikbud.pkpberbasiszonasi.Helper.DatabaseHelper;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.ProfileHelper;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.ServerCallback;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.TokenManager;
import id.go.kemdikbud.pkpberbasiszonasi.LoginActivity;
import id.go.kemdikbud.pkpberbasiszonasi.R;
import com.kosalgeek.android.caching.FileCacher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private JSONObject tokenObject;
    private String token = "";
    private Button btn_logout;
    private TextView fullname,username;
    private String fullname2 = "";
    ProfileHelper profileHelper;

    TextView txt_no_peserta;
    TextView txt_nuptk;
    TextView txt_golongan_jabatan;
    TextView txt_tmp_tgl_lahir;
    TextView txt_kelamin;
    TextView txt_tmp_tugas;
    TextView txt_tugas_mengajar;
    TextView txt_alamat;

    private static String TAG = ProfileFragment.class.getSimpleName();
    public ProfileFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_profile, container, false);
        initCollapsingToolbar(v);

        final DatabaseHelper db = new DatabaseHelper(getContext());

        // Get Token From cache file
        FileCacher tokenString = new FileCacher(getContext(),"token.txt");
        try {
            if (tokenString.hasCache()){
                tokenObject = new JSONObject(tokenString.readCache().toString());
                if (tokenObject.has("token")){
                    token = tokenObject.getString("token");
                    Log.d(TAG,"Ini Tokennya "+token);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        fullname = (TextView) v.findViewById(R.id.fullname);
        username = (TextView) v.findViewById(R.id.username);
        txt_no_peserta = (TextView) v.findViewById(R.id.txt_no_ukg);
        txt_nuptk = (TextView) v.findViewById(R.id.txt_nuptk);
        txt_golongan_jabatan = (TextView) v.findViewById(R.id.txt_golongan_jabatan);
        txt_tmp_tgl_lahir = (TextView) v.findViewById(R.id.txt_tmp_tgl_lahir);
        txt_kelamin = (TextView) v.findViewById(R.id.txt_kelamin);
        txt_tmp_tugas = (TextView) v.findViewById(R.id.txt_tmp_tugas);
        txt_tugas_mengajar = (TextView) v.findViewById(R.id.txt_tugas_mengajar);
        txt_alamat = (TextView) v.findViewById(R.id.txt_alamat);
        profileHelper = new ProfileHelper(getContext(),getActivity(),token);

        profileHelper.loadProfile("profile" + token, true, new ServerCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.d(TAG,result.toString());
                setProfile();
            }
        });

        btn_logout = (Button) v.findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteTokenMaster();
                FileCacher fileCacher = new FileCacher(getContext(),"token.txt");
                if (fileCacher.hasCache()){
                    try {
                        fileCacher.clearCache();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                deleteCache(getContext());
                CookieSyncManager.createInstance(getContext());
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeAllCookie();

                Intent login = new Intent(getContext(), LoginActivity.class);
                startActivity(login);
                login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().finishAffinity();
                getActivity().finish();
            }
        });
        return  v;
    }

    private void initCollapsingToolbar(View v) {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) v.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) v.findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(fullname2);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public void setProfile(){
        FileCacher fileCacher = new FileCacher(getContext(),"profile"+token+".txt");
        if (fileCacher.hasCache()){
            try {
                JSONArray profArray = new JSONArray(fileCacher.readCache().toString());
                if (profArray.length()>0){
                    JSONObject profObject = profArray.getJSONObject(0);
                    if (profObject.has("fullname")){
                        fullname.setText(profObject.getString("fullname"));
                        fullname2 = profObject.getString("fullname");
                    }
                    if (profObject.has("username")) username.setText(profObject.getString("username"));
                }
            }catch (IOException e){
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        final TokenManager tokenManager = new TokenManager(getContext(),getActivity());
        tokenManager.loadToken(new ServerCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    String tokensimpkb = result.getString("access_token");
                    profileHelper.load_profile_simpkb(tokenManager.chace_name, tokensimpkb, new ServerCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            if (result.has("data")){
                                try {
                                    JSONObject objectdata = result.getJSONObject("data");
                                    String tmp_lahir = "";
                                    if (objectdata.has("no_ukg")) txt_no_peserta.setText(objectdata.getString("no_ukg"));
                                    if (objectdata.has("nuptk")) txt_nuptk.setText(objectdata.getString("nuptk"));
                                    if (objectdata.has("golongan")) txt_golongan_jabatan.setText(objectdata.getString("golongan"));
                                    if (objectdata.has("tempat_lahir")) tmp_lahir = objectdata.getString("tempat_lahir");
                                    if (objectdata.has("tgl_lahir")){
                                        JSONObject tgllahirobj = objectdata.getJSONObject("tgl_lahir");
                                        if (tgllahirobj.has("date")){
                                            tmp_lahir+=", "+tgllahirobj.getString("date").substring(0,10);
                                            Log.d(TAG,tmp_lahir);
                                            txt_tmp_tgl_lahir.setText(tmp_lahir);
                                        }else{
                                            txt_tmp_tgl_lahir.setText(tmp_lahir);
                                        }
                                    }
                                    if (objectdata.has("kelamin")){
                                        txt_kelamin.setText(objectdata.getString("kelamin").equals("L")?"Laki-laki":"Perempuan");
                                    }
                                    if (objectdata.has("sekolah")){
                                        JSONObject tempatobj = objectdata.getJSONObject("sekolah");
                                        if (tempatobj.has("nama")){
                                            txt_tmp_tugas.setText(tempatobj.getString("nama"));
                                        }
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
