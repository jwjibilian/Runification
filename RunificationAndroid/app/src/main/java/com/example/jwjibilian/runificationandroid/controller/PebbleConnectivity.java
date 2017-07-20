package com.example.jwjibilian.runificationandroid.controller;

import android.content.Context;
import com.getpebble.android.kit.PebbleKit;
import java.util.UUID;


public class PebbleConnectivity {
    private static final UUID APP_UUID = UUID.fromString("3ccbcc65-4bab-47af-ad7b-4a17126fcf1c");

    /**
     * This will create a connection bewtween the app and the watch.
     * @param appContext Context that is open in the app
     */
    public PebbleConnectivity(Context appContext){
        PebbleKit.startAppOnPebble(appContext, APP_UUID);  // starts the app on the watch
    }

}
