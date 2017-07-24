package com.example.jwjibilian.runificationandroid.controller;

import android.content.Context;
import android.os.Environment;
import android.text.format.DateFormat;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by jwjibilian on 7/24/17.
 */

public class DataRecord {
    private Context context;
    private String date;
    private String filename;
    public DataRecord(Context context){
        this.context = context;
        String date = (DateFormat.format("dd-MM-yyyyhh:mm:ss", new java.util.Date()).toString());
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
            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            File myFile = new File(path, "mytextfile.txt");
            FileOutputStream fOut = new FileOutputStream(myFile,true);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(date+","+toSave);
            myOutWriter.close();
            fOut.close();

            Toast.makeText(context,"Text file Saved !",Toast.LENGTH_LONG).show();
        }

        catch (java.io.IOException e) {

            //do something if an IOException occurs.

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
}
