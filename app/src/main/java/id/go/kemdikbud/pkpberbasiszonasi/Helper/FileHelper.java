package id.go.kemdikbud.pkpberbasiszonasi.Helper;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FileHelper {

    private String TAG = FileHelper.class.getSimpleName();
    private static final String VIDEO_PATTERN =
            "([^\\s]+(\\.(?i)(mp4|mkv|flv|avi|vob|ogg|wmv|mpg|mp2|mpeg|mpe|m2v|m4v|3gp|))$)";
    private Pattern pattern;
    private Matcher matcher;


    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public String getBasePath(){
        File file = new File(Environment.getExternalStorageDirectory()+"/PKPZonasi");
        return file.getAbsolutePath();
    }

    public void DownloadManager(String Foldername,String Url,String Filename){
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)){

                File dirExternal = Environment.getExternalStorageDirectory();
                File createDir = new File(dirExternal.getAbsolutePath()+"/PKPZonasi/");

                if (!createDir.exists()){
                    if (!createDir.mkdir()){
                        Log.d(TAG,"Access Denied To Create"+createDir.getPath());
                    }
                }

                File destFile = new File(dirExternal,Foldername);
                if (!destFile.exists()){
                    if (!destFile.mkdir()){
                        Log.d(TAG,"Access Denied To Create"+destFile.getPath());
                    }
                }

            }else {

            }
        }

    public void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }

    public boolean isVideoFile(final String video){
        pattern = Pattern.compile(VIDEO_PATTERN);
        matcher = pattern.matcher(video);
        return matcher.matches();

    }
}
