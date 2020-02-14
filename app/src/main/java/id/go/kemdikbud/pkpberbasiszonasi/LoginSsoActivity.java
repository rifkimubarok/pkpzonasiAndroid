package id.go.kemdikbud.pkpberbasiszonasi;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.kosalgeek.android.caching.FileCacher;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Set;

import id.go.kemdikbud.pkpberbasiszonasi.Helper.DatabaseHelper;
import id.go.kemdikbud.pkpberbasiszonasi.Helper.ViewDialog;
import id.go.kemdikbud.pkpberbasiszonasi.Master.TokenMaster;


public class LoginSsoActivity extends AppCompatActivity {

    DatabaseHelper dbs;
    String TAG = LoginSsoActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sso);

        dbs = new DatabaseHelper(this);
        Intent intent = getIntent();
        String action = intent.getAction();
        String url = intent.getData().toString();

        Uri uri = Uri.parse(url);
        String token = uri.getQueryParameter("param");
        String token_encode = token.substring(9);
        String fix_token = new String(Base64.decodeBase64(token_encode.getBytes()));
        String[] token_split = fix_token.split(":::");
        JSONObject objToken = new JSONObject();
        try {
            objToken.put("privatetoken",token_split[0]);
            objToken.put("token",token_split[1]);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TokenMaster token_master = new TokenMaster();
        token_master.setToken(token_split[1]);
        token_master.setPrivatetoken(token_split[0]);
        dbs.addRecord(token_master);

        Log.d(TAG,objToken.toString());
        FileCacher<String> tokenString = new FileCacher<>(getApplicationContext(),"token.txt");
        try {tokenString.writeCache(objToken.toString());}catch (IOException e){e.printStackTrace();}

        ViewDialog viewDialog = new ViewDialog(this);
//        viewDialog.showDialog();

        Intent login = new Intent(getApplicationContext(),HomeActivity.class);
        startActivity(login);
        login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finishAffinity();
        finish();
    }
}
