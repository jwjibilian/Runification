package com.example.jwjibilian.runificationandroid.controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by jwjibilian on 7/24/17.
 */

public class DataRecord {
    private static final int REQUESTCODE_STORAGE_PERMISSION = 1;
    private Context context;
    private String date;
    private String filename;
    public DataRecord(Context context, Activity act){
        this.context = context;
        storagePermitted(act);
        date = (DateFormat.format("dd-MM-yyyyhh:mm:ss", new java.util.Date()).toString());
        filename = "runifi"+date;
        if (isExternalStorageWritable()){

        } else{
            Toast.makeText(context,"Cant save to external memory",Toast.LENGTH_LONG).show();
        }

    }

    /**
     * For CSV file line must be inserted in this format:
     * *input*,*input*,...,*input*\n
     * Should save to download directory.
     * @param toSave What you want to save in CSV format
     */
    public void save(String toSave){
        try {
            date = (DateFormat.format("dd-MM-yyyyhh:mm:ss", new java.util.Date()).toString());


            File myFile = new File("/sdcard/run/", filename);
            Log.d("Dir", myFile.exists() + "");
            Log.d("Filepath", myFile.getPath()+ "" + Environment.MEDIA_MOUNTED);
            if(!myFile.exists()){

                myFile.mkdirs();
            }
            FileOutputStream fOut = new FileOutputStream(myFile,true);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(date+","+toSave );
            myOutWriter.close();
            fOut.close();

            Toast.makeText(context,"Text file Saved !" ,Toast.LENGTH_LONG).show();
        }

        catch (java.io.IOException e) {

            //do something if an IOException occurs.
            Log.d("DataRec", e.getMessage()+""+e.toString());
            Toast.makeText(context,"ERROR - Text could't be"+
                    " added", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
    private static boolean storagePermitted(Activity activity) {

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&

                ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)

            return true;

        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTCODE_STORAGE_PERMISSION);

        return false;

    }
}
