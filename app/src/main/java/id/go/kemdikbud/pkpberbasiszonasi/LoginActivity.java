package id.go.kemdikbud.pkpberbasiszonasi;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.ApiService;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.CheckPermissionSD;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.DatabaseHelper;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.ServerCallback;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.TokenManager;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.ViewDialog;
import id.go.kemdikbud.pkpberbasiszonasi.Master.TokenMaster;

import id.go.kemdikbud.pkpberbasiszonasi.R;
import com.kosalgeek.android.caching.FileCacher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private Button btn_login,btn_login_simpkb;
    private EditText txt_username,txt_password;
    private String endPointApi;
    private ArrayList<String> folder = new ArrayList<>();
    private ArrayList<String> file = new ArrayList<>();
    ApiService apiService ;
    ViewDialog viewDialog;
    ImageView imageView;
    DatabaseHelper dbs = new DatabaseHelper(this);
    TokenManager tokenManager;

    private static final int WRITE_REQUEST_CODE =1;
    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static final String TAG = LoginActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        imageView = (ImageView) findViewById(R.id.logo_app);
        apiService = new ApiService(this);
        viewDialog = new ViewDialog(this);
        btn_login = (Button)findViewById(R.id.btn_login);
        txt_username = (EditText) findViewById(R.id.txt_username);
        txt_password = (EditText) findViewById(R.id.txt_password);
        btn_login_simpkb = (Button) findViewById(R.id.btn_login_simpkb);

        /*btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });*/
        if (Build.VERSION.SDK_INT >= 23){
            requestPermissions(permissions, WRITE_REQUEST_CODE);
        }
        btn_login_simpkb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_user();
            }
        });

        /*imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SetApiActivity.class);
                startActivity(intent);
                return false;
            }
        });*/

        CheckPermissionSD cp = new CheckPermissionSD();
        cp.checkPermission(this,this);

        viewDialog.showDialog();
        if (configurationFiles()){
            viewDialog.hideDialog();
        }
        tokenManager = new TokenManager(this,this);
    }

    private void doLogin() {
        if (validation()){
            viewDialog.showDialog();
            final String username = txt_username.getText().toString();
            final String password = txt_password.getText().toString();
            endPointApi = apiService.getEndPointAPI()+"login/token.php";
            Log.v(TAG,"urlnya "+endPointApi);
            endPointApi += "?username="+username+"&password="+password+"&service="+apiService.getServiceAPI();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,endPointApi,null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.has("error")){
                                    Toast.makeText(getApplicationContext(), "Error: " + response.getString("error"), Toast.LENGTH_SHORT).show();
                                }else{
                                    TokenMaster token = new TokenMaster();
                                    token.setToken(response.getString("token"));
                                    token.setPrivatetoken(response.getString("privatetoken"));
                                    dbs.addRecord(token);

                                    FileCacher<String> tokenString = new FileCacher<>(getApplicationContext(),"token.txt");
                                    try {tokenString.writeCache(response.toString());}catch (IOException e){e.printStackTrace();}
                                    FileCacher<String> fileUsername = new FileCacher<>(getApplicationContext(),"username");
                                    try {fileUsername.writeCache(username);} catch (IOException e) {e.printStackTrace();}
                                    FileCacher<String> filePassword = new FileCacher<>(getApplicationContext(),"password");
                                    try {filePassword.writeCache(password);} catch (IOException e) {e.printStackTrace();}

                                    if (configurationFiles()){
                                        Intent login = new Intent(getApplicationContext(),HomeActivity.class);
                                        startActivity(login);
                                        login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        finishAffinity();
                                        finish();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            viewDialog.hideDialog();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // error in getting json
                    Log.e(TAG, "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), "Cek Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
                    viewDialog.hideDialog();
                }
            });
            request.setShouldCache(false);
            RequestAdapter.getInstance().addToRequestQueue(request);
        }
    }

    private void check_user(){
        if (validation()){
            viewDialog.showDialog();
            final String username = txt_username.getText().toString();
            final String password = txt_password.getText().toString();
            endPointApi = apiService.getEndPointAPI()+"/webservice/rest/server.php";
            endPointApi += "?wstoken="+apiService.getDefault_token()+"&wsfunction=core_user_get_users_by_field&moodlewsrestformat=json&field=email&values[0]="+username;
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,endPointApi,null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                if (response.length() > 0){
                                    doLogin_simpkb();
                                }else{
                                    Toast.makeText(getApplicationContext(),"Username tidak terdaftar.",Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            viewDialog.hideDialog();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // error in getting json
                    Log.e(TAG, "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), "Cek Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
                    viewDialog.hideDialog();
                }
            });
            request.setShouldCache(false);
            RequestAdapter.getInstance().addToRequestQueue(request);
        }
    }

    private void doLogin_simpkb(){
        if (validation()){
            tokenManager.loadToken(new ServerCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    String username = txt_username.getText().toString();
                    String password = txt_password.getText().toString();
                    FileCacher<String> fileUsername = new FileCacher<>(getApplicationContext(),"username");
                    try {fileUsername.writeCache(username);} catch (IOException e) {e.printStackTrace();}
                    FileCacher<String> filePassword = new FileCacher<>(getApplicationContext(),"password");
                    try {filePassword.writeCache(password);} catch (IOException e) {e.printStackTrace();}
                    Intent intent = new Intent(LoginActivity.this,SsoActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    public boolean validation(){
        boolean txt_user = isEmpty(txt_username);
        boolean txt_pass = isEmpty(txt_password);
        if(txt_user){
            setError(txt_username,"Username Tidak boleh kosong");
            return false;
        }else{
            clearError(txt_username);
        }

        if(txt_pass){
            setError(txt_password,"Password tidak boleh kosong");
            return false;
        }else{
            clearError(txt_password);
        }
        return !txt_user && !txt_pass;
    }

    public boolean isEmpty(EditText txt_editor){
        String value = txt_editor.getText().toString().trim();
        return value.length() == 0;
    }

    public void setError(EditText txt_editor,String msg_error){
        txt_editor.setError(msg_error);
        txt_editor.requestFocus();
        txt_editor.setSelection(txt_editor.getText().length());
    }

    public  void clearError(EditText txt_editor){
        txt_editor.setError(null);
    }

    private boolean listAssetFiles(String path) {

        String [] list;
        try{
            String[] f = this.getAssets().list(path);
            for(String f1 : f){
                String pathAsset = path+"/"+f1;
                if (pathAsset.split("[.]").length > 1){
                    file.add(pathAsset);
                }else{
                    folder.add(pathAsset);
                }
                listAssetFiles(path+"/"+f1);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    public boolean configurationFiles(){

        if (listAssetFiles("web")){
            for (String fol:folder){
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PKPZonasi/",fol);
                if (!file.exists()){
                    if(!file.mkdirs()){
                        Log.v(TAG,"Tidak dapat membuat asset folder");
                    }
                }
            }
            InputStream in = null;
            OutputStream out = null;
            for (String fil:file){
                try {
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PKPZonasi/",fil);
                    if (file.exists()){
                        continue;
                    }
                    in = this.getAssets().open(fil);
                    out = new FileOutputStream(file);
                    copyFile(in,out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }


}
