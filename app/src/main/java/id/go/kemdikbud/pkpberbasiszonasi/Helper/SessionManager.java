package id.go.kemdikbud.pkpberbasiszonasi.Helper;

import android.content.Context;

import com.kosalgeek.android.caching.FileCacher;

import java.io.IOException;

public class SessionManager {

    public String USERNAME = "username";
    public String PASSWORD = "password";

    Context context;
    public SessionManager(Context context){
        this.context = context;
    }

    public void set_session(String data,String session_name){
        FileCacher<String> session = new FileCacher<>(context,session_name);
        try {
            session.writeCache(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String get_session(String session_name){
        FileCacher<String> session = new FileCacher<>(context,session_name);
        if (session.hasCache()){
            try {
                return session.readCache();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return "";
    }
}
