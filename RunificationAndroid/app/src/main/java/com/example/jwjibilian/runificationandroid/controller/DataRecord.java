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

import com.example.jwjibilian.runificationandroid.model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by jwjibilian on 7/24/17.
 */

public class DataRecord {
    User user;
    private static final int REQUESTCODE_STORAGE_PERMISSION = 1;
    private Context context;
    private String date;
    private String filename;
    private String dir;
    private File  myFile;
    private FileOutputStream fOut;
    private OutputStreamWriter myOutWriter;

    public DataRecord(Context context, Activity act,String type){
        this.context = context;
        storagePermitted(act);
        user = User.getInstance(context);
        date = (DateFormat.format("ddMMyyyyhhmmss", new java.util.Date()).toString());
        dir = Environment.getExternalStorageDirectory() + "";
        filename = type+user.getName()+""+date+".txt";

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


            myFile = new File(dir, filename);
            Log.d("Dir", dir);
            Log.d("Filepath", myFile.getPath()+ " " + Environment.MEDIA_MOUNTED);
            if (!myFile.exists()){

                myFile.createNewFile();
            }

            fOut = new FileOutputStream(myFile,true);
            myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(date+","+toSave );
            myOutWriter.flush();
            myOutWriter.close();
            fOut.close();


            Toast.makeText(context, dir ,Toast.LENGTH_LONG).show();
        }

        catch (java.io.IOException e) {

            //do something if an IOException occurs.
            Log.d("DataRec", e.getMessage()+""+e.toString());
            //Toast.makeText(context,"ERROR - Text could't be"+
            //        " added", Toast.LENGTH_LONG).show();
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
    public void close(){
        try{
            myOutWriter.flush();
            myOutWriter.close();
            fOut.close();

        } catch (Exception e){

           Log.d("Close", e.toString());
        }


    }
}
