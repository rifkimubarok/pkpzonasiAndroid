package id.go.kemdikbud.pkpberbasiszonasi.Helper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import id.go.kemdikbud.pkpberbasiszonasi.RequestAdapter;
import com.kosalgeek.android.caching.FileCacher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollectingLink {
    public ArrayList<HashMap> list_url_summary = new ArrayList<>();
    public ArrayList<HashMap> list_url_content = new ArrayList<>();
    Context context;
    ViewDialog dialog;
    String TAG = CollectingLink.class.getSimpleName();
    FileHelper fileHelper = new FileHelper();
    public CollectingLink(Context context,ViewDialog viewDialog){
        this.context = context;
        dialog = viewDialog;
        FileCacher tokenString = new FileCacher(context,"token.txt");
    }



    public void fetchLink(final String uri, final String filename,final CollectingLinkCallback callback) {
        dialog.showDialog();
        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,uri,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            FileCacher<String> fileCacher = new FileCacher<>(context,filename+".txt");
                            if (fileCacher.hasCache()){
                                fileCacher.clearCache();
                                fileCacher.writeCache(response.toString());
                            }else {
                                fileCacher.writeCache(response.toString());
                            }
                            for (int i=0;i<response.length();i++){
                                try {
                                    JSONObject unitCourse = response.getJSONObject(i);
                                    String unitName = "Unit"+unitCourse.getInt("id");

                                    if (unitCourse.has("summary")){
                                        ArrayList list = pullLinks(unitCourse.getString("summary"));
                                        for (int aa=0;aa<list.size();aa++){
                                            HashMap<String,String> url = new HashMap<>();
                                            if (fileHelper.isVideoFile(list.get(aa).toString()))continue;
                                            if (list.get(aa).toString().contains("youtube"))continue;

                                            url.put("url",list.get(aa).toString());
                                            String[] filen = list.get(aa).toString().split("[/]");
                                            String fxFilename= "";
                                            if (filen[filen.length-1].split("[?]").length > 1){
                                                String[] fname = filen[filen.length-1].split("[?]");
                                                fxFilename = fname[0];
                                            }else{
                                                fxFilename = filen[filen.length-1];
                                            }
                                            url.put("type","image");
                                            url.put("filename",fxFilename);
                                            url.put("folder",unitName);
                                            list_url_summary.add(url);
                                        }
                                    }
                                    if (unitCourse.has("modules")){
                                        JSONArray arrayModules = unitCourse.getJSONArray("modules");
                                        for (int j=0;j<arrayModules.length();j++){
                                            final JSONObject modules = arrayModules.getJSONObject(j);
                                            String folderModules = "Modules"+modules.getInt("id");
                                                if (modules.has("url")){
                                                    if (modules.has("modname")){
                                                        if (modules.getString("modname").equals("hvp")){
                                                            HashMap<String,String> url = new HashMap<>();
                                                            url.put("url",modules.getString("url"));
                                                            url.put("filename","");
                                                            url.put("type","hvp");
                                                            url.put("folder","web/workspace/hvp"+modules.getInt("id"));
                                                            list_url_content.add(url);
                                                        }
                                                    }
                                                }

                                            if (modules.has("contents")){
                                                JSONArray arrayContent = modules.getJSONArray("contents");
                                                String folderContent = folderModules+"/"+modules.getString("name");
                                                for (int k=0;k<arrayContent.length();k++){
                                                    JSONObject content = arrayContent.getJSONObject(k);
//                                                    Log.v(TAG,"URL CONTENT "+content.getString("fileurl"));
                                                    if (content.getString("type").equals("file")){
                                                        HashMap<String,String> url = new HashMap<>();
                                                        url.put("url",content.getString("fileurl"));
                                                        if (content.has("filepath")){
                                                            if (!content.getString("filepath").equals("/")){
                                                                url.put("folder",folderContent+content.getString("filepath"));
                                                            }else{
                                                                url.put("folder",folderContent);
                                                            }
                                                        }else{
                                                            url.put("folder",folderContent);
                                                        }

                                                        url.put("type","file");
                                                        url.put("filename",content.getString("filename"));
                                                        list_url_content.add(url);
                                                    }

                                                }
                                            }
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            List<ArrayList> result = new ArrayList<>();
                            result.add(list_url_content);
                            result.add(list_url_summary);
                            callback.onSuccess(result);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        dialog.hideDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                dialog.hideDialog();
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestAdapter.getInstance().addToRequestQueue(request);
    }

    private ArrayList pullLinks(String text) {
        ArrayList links = new ArrayList();

        String regex = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        while(m.find()) {
            String urlStr = m.group();
            if (urlStr.startsWith("(") && urlStr.endsWith(")"))
            {
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }
            links.add(urlStr);
        }
        return links;
    }
}
