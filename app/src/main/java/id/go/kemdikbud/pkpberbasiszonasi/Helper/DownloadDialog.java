package id.go.kemdikbud.pkpberbasiszonasi.Helper;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import id.go.kemdikbud.pkpberbasiszonasi.R;

public class DownloadDialog {
    Activity activity;
    Dialog dialog;

    TextView filename;
    ProgressBar progressBar;
    TextView percentDownload;
    TextView sizeDownload;
    TextView currentDownload;
    TextView title;

    public DownloadDialog(Activity activity){
        this.activity = activity;
    }
    public void showDialog() {
        dialog  = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_download_layout);
        filename = (TextView) dialog.findViewById(R.id.filename);
        title = (TextView) dialog.findViewById(R.id.title);
        progressBar = (ProgressBar) dialog.findViewById(R.id.progressBar);
        percentDownload = (TextView) dialog.findViewById(R.id.percentDownload);
        sizeDownload = (TextView) dialog.findViewById(R.id.sizeDownload);
        currentDownload = (TextView) dialog.findViewById(R.id.currentDownload);
        dialog.show();
    }

    public void setTitle(String judul){
        title.setText(judul);
    }

    public void setFilename(String text){
        filename.setText(text);
    }

    public void setProgressBar(int progress){
        progressBar.setProgress(progress);
    }

    public void setCurrentDownload(String current){
        currentDownload.setText(current);
    }
    public void setPercentDownload(String current){
        String cur = current + "%";
        percentDownload.setText(cur);
    }

    public void setSizeDownload(String size){
        sizeDownload.setText(size);
    }

    //..also create a method which will hide the dialog when some work is done
    public void hideDialog(){
        dialog.dismiss();
    }
}
