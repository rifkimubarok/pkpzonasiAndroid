package id.go.kemdikbud.pkpberbasiszonasi.Helper;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import id.go.kemdikbud.pkpberbasiszonasi.RequestAdapter;

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;
import com.kosalgeek.android.caching.FileCacher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ProfileHelper {
    Context context;
    Activity activity;

    private FormatHelper formatHelper = new FormatHelper();
    private String endPointApi = "";
    private ApiService apiService;
    private String token = "";
    TokenManager tokenManager;

    private String TAG = ProfileHelper.class.getSimpleName();

    public ProfileHelper(Context context, Activity activity, String token){
        apiService = new ApiService(context);
        this.context = context;
        this.activity = activity;
        this.endPointApi += apiService.getEndPointAPI()+"webservice/rest/server.php?wstoken="+token;
        this.endPointApi += "&wsfunction=core_user_get_users_by_field&moodlewsrestformat=json";
        this.token = token;
        tokenManager = new TokenManager(context,activity);
    }

    public boolean loadProfile(final String filename, boolean isRefresh, final ServerCallback serverCallback){
        String username = "";
        String field = "";
        FileCacher fileCacher = new FileCacher(context,"username");
        if (fileCacher.hasCache()){
            try {
                username = fileCacher.readCache().toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileCacher hascache = new FileCacher(context,filename);
        if (hascache.hasCache() && !isRefresh){
            return true;
        }
        if (formatHelper.isEmail(username))field = "email";else field="username";
        endPointApi += "&field="+field+"&values[0]="+username;
        final ViewDialog viewDialog = new ViewDialog(activity);
        viewDialog.showDialog();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,endPointApi,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        FileCacher<String> profile = new FileCacher<>(context,filename+".txt");
                        JSONObject profileObj = new JSONObject();
                        try {
                            profileObj.put("data",response);
                            serverCallback.onSuccess(profileObj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            profile.writeCache(response.toString());
                            Log.v(TAG,"Filename Cache = "+filename+" datanya "+response.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        viewDialog.hideDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, "Error: " + error.getMessage());
                viewDialog.hideDialog();
            }
        });

        RequestAdapter.getInstance().addToRequestQueue(request);
        return false;
    }

    public String get_username(){
        SessionManager sessionManager = new SessionManager(context);
        String email = sessionManager.get_session(sessionManager.USERNAME);
        String[] email_arr = email.split("@");
        return email_arr[0];
    }

    public void load_profile_simpkb(final String filename, String simpbtoken, final ServerCallback serverCallback){
        String username = get_username();
        String url = "https://gtk.belajar.kemdikbud.go.id/api/ptk/info/"
                +username+"?access_token="+simpbtoken;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        FileCacher<String> profile = new FileCacher<>(context,filename);
                        try {
                            profile.writeCache(response.toString());
                            serverCallback.onSuccess(response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        serverCallback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, "Error: " + error.getMessage());
            }
        });
        RequestAdapter.getInstance().addToRequestQueue(request);
    }
}
