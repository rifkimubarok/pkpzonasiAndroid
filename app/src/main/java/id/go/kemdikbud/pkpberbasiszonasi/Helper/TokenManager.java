package id.go.kemdikbud.pkpberbasiszonasi.Helper;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;
import com.kosalgeek.android.caching.FileCacher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import id.go.kemdikbud.pkpberbasiszonasi.RequestAdapter;

public class TokenManager {
    private Context context;
    private Activity activity;

    private String endPointSimpkb = "https://oauth.simpkb.id/oauth/access_token";
    private String client_id = "gpogtk";
    private String client_secret = "gtkgp0";
    private String grant_type = "client_credentials";
    private String scope = "read";
    public String chace_name = "tokenmaster";
    public String new_token;
    ViewDialog viewDialog;

    public TokenManager(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
        viewDialog = new ViewDialog(activity);
    }

    public void loadToken(final ServerCallback serverCallback){
        viewDialog.showDialog();
        String url = endPointSimpkb;
        url += "?client_id="+client_id+"&client_secret="+client_secret
                +"&grant_type="+grant_type+"&scope="+scope;
        Log.d("TokenManager",url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                FileCacher<String> token = new FileCacher<>(context,chace_name);
                try {
                    Log.d("TokenManager", "onResponse: "+response.toString());
                    if (response.has("access_token")){
                        serverCallback.onSuccess(response);
                        response.put("expires",System.currentTimeMillis()+(6*60000));
                        if (token.hasCache()){
                            token.clearCache();
                            token.writeCache(response.toString());
                        }else{
                            token.writeCache(response.toString());
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                viewDialog.hideDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                viewDialog.hideDialog();
            }
        });
        request.setShouldCache(false);
        RequestAdapter.getInstance().addToRequestQueue(request);
    }

    public String getToken(){
        FileCacher<String> tokenmaster = new FileCacher<>(context,chace_name);
        try {
            if (tokenmaster.hasCache()){
                JSONObject token_obj = new JSONObject(tokenmaster.readCache());
                Date current_date = new Date(System.currentTimeMillis());
                Date expired_date = new Date(token_obj.getLong("expires"));
                if (expired_date.compareTo(current_date)<0){
                    loadToken(new ServerCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            try {
                                new_token = result.getString("access_token");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }else{
                    return token_obj.getString(token_obj.getString("access_token"));
                }
            }
        }catch (Exception e){

        }
        return new_token;
    }
}