package id.go.kemdikbud.pkpberbasiszonasi.Helper;

import android.content.Context;

import com.kosalgeek.android.caching.FileCacher;

import java.io.IOException;

public class ApiService {
    private Context context;

    private String EndPointAPI = "https://pkp.belajar.kemdikbud.go.id/";

    public ApiService(Context context){
        this.context = context;
    }
    private String ServiceAPI = "moodle_mobile_app";

    private String serviceTemp = "serviceAPIChacer";
    private String EndPointTemp = "endAPIChacer";
    private String default_token = "5ecb2df16c42f4618d75f1d3f580c2a3";



    public String getEndPointAPI() {
        FileCacher fileCacher = new FileCacher(context,EndPointTemp);
        try{
            if (fileCacher.hasCache()){
                String endpoint = fileCacher.readCache().toString();
                if (!endpoint.isEmpty()){

                    return endpoint;
                }else{
                    return EndPointAPI;
                }
            }else{
                return EndPointAPI;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return EndPointAPI;
    }

    public void setEndPointAPI(String endPointAPI) {
        EndPointAPI = endPointAPI;
        FileCacher<String> fileCacher = new FileCacher<>(context,EndPointTemp);
        try {
            if (fileCacher.hasCache()){
                fileCacher.clearCache();
                fileCacher.writeCache(endPointAPI);
            }else{
                fileCacher.writeCache(endPointAPI);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getServiceAPI() {
        FileCacher fileCacher = new FileCacher(context,serviceTemp);
        try{
            if (fileCacher.hasCache()){
                String endpoint = fileCacher.readCache().toString();
                if (!endpoint.isEmpty()){
                    return endpoint;
                }else{
                    return ServiceAPI;
                }
            }else{
                return ServiceAPI;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return ServiceAPI;
    }

    public void setServiceAPI(String serviceAPI) {
        ServiceAPI = serviceAPI;
        FileCacher<String> fileCacher = new FileCacher<>(context,serviceTemp);
            try {
                if (fileCacher.hasCache()){
                    fileCacher.clearCache();
                    fileCacher.writeCache(serviceAPI);
                }else{
                    fileCacher.writeCache(serviceAPI);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }


    public String getDefault_token() {
        return default_token;
    }
}
