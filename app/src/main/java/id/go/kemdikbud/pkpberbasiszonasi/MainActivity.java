package id.go.kemdikbud.pkpberbasiszonasi;

import android.Manifest;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import id.go.kemdikbud.pkpberbasiszonasi.Helper.DatabaseHelper;
import id.go.kemdikbud.pkpberbasiszonasi.Master.TokenMaster;

import id.go.kemdikbud.pkpberbasiszonasi.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper dbs = new DatabaseHelper(this);

    private ArrayList<String> folder = new ArrayList<>();
    private ArrayList<String> file = new ArrayList<>();

    private String TAG = MainActivity.class.getSimpleName();

    private static final int WRITE_REQUEST_CODE =1;
    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new CountDownTimer(2000,1000){

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                TokenMaster tokenMaster = dbs.getTokenMaster();
                if (tokenMaster.getToken() == null){
                    Intent login = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(login);
                    login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    finishAffinity();
                    finish();
                }else{
                    Intent home = new Intent(getApplicationContext(),HomeActivity.class);
                    startActivity(home);
                    home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    finishAffinity();
                    finish();
                }
            }
        }.start();

    }
}
