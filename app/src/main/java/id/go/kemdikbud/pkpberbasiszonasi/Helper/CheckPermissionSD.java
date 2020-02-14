package id.go.kemdikbud.pkpberbasiszonasi.Helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

public class CheckPermissionSD {
    private int STORAGE_PERMISSION_CODE = 1;
    private int STORAGE_WRITE_PERMISSION_CODE = 1;

    public void checkPermission(Context context,Activity activity){
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        } else {
            requestStoragePermission(context,activity);
        }

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        } else {
            requestStoragePermission(context,activity);
        }
    }

    public void requestStoragePermission(Context context, final Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(context)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }
    public void requestWriteStoragePermission(Context context, final Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(context)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity,
                                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_WRITE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_WRITE_PERMISSION_CODE);
        }
    }

}
