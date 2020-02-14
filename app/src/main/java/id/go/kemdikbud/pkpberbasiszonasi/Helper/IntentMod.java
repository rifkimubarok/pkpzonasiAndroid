package id.go.kemdikbud.pkpberbasiszonasi.Helper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import id.go.kemdikbud.pkpberbasiszonasi.Master.Courses;
import id.go.kemdikbud.pkpberbasiszonasi.Master.UnitModule;
import id.go.kemdikbud.pkpberbasiszonasi.Modules.ModulesFiles;
import id.go.kemdikbud.pkpberbasiszonasi.Modules.ModulesHVP;
import id.go.kemdikbud.pkpberbasiszonasi.Modules.ModulesNotsupported;
import id.go.kemdikbud.pkpberbasiszonasi.Modules.ZoomImageActivity;

import com.google.gson.Gson;

public class IntentMod{
    Context context;
    static String TAG = IntentMod.class.getSimpleName();
    public  IntentMod(Context context){
        this.context = context;
    }

    public void startModActivity(UnitModule unit, Courses currentCourse){
        final Bundle bundle = new Bundle();
        final String JSONModules = new Gson().toJson(unit);
        final String JSONCourses = new Gson().toJson(currentCourse);
        bundle.putString("modules",JSONModules);
        bundle.putString("courses",JSONCourses);
        bundle.putString("url",unit.getUrl());
        Log.d(TAG,"Mod name"+unit.getModname());
        switch (unit.getModname()){
            case "hvp":
                Intent detail = new Intent(this.context, ModulesHVP.class);
                detail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                detail.putExtras(bundle);
                this.context.startActivity(detail);
                break;
            case "folder":
                Intent files = new Intent(this.context, ModulesFiles.class);
                files.putExtras(bundle);
                files.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.context.startActivity(files);
                break;
            case "resource":
                Intent resource = new Intent(this.context, ModulesFiles.class);
                resource.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                resource.putExtras(bundle);
                this.context.startActivity(resource);
                break;
            default:
                if (unit.getModname().equals("label")){break;}
                Intent defIntent = new Intent(this.context, ModulesNotsupported.class);
                defIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                defIntent.putExtras(bundle);
                this.context.startActivity(defIntent);
                break;
        }
    }

    public void zoomImage(String url){
        Bundle bundle = new Bundle();
        bundle.putString("url",url);
        Intent intent = new Intent(this.context, ZoomImageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtras(bundle);
        this.context.startActivity(intent);
    }
}
