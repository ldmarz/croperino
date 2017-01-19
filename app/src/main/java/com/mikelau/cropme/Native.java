package com.mikelau.cropme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

/**
 * Created by lmartinez on 19/1/17.
 */

public class Native extends UnityPlayerActivity {
    public byte[] pluginData;
    public Context unityActivity;

    public static Native instance = new Native();


    public void launchGallery(Context context){
        launchAndroidActivity(context, GalleryActivity.class);
    }

    public void launchCamera(Context context){
        launchAndroidActivity(context, CameraActivity.class);
    }

    public static void sendMessage(String method, String message){
        UnityPlayer.UnitySendMessage("MenuController", method, message);

    }

    public byte[] getPluginData(){
        return pluginData;
    }

    public void setPluginData(byte[] data){
        pluginData = data;
    }

    public void launchAndroidActivity(Context context, Class<?> activityClass){
        this.unityActivity = context;
        Intent myIntent = new Intent(context, activityClass);
        context.startActivity(myIntent);
    }

    public void backToUnity(Activity androiActivity){
        Intent myIntent = new Intent(androiActivity, unityActivity.getClass());
        unityActivity.startActivity(myIntent);
    }
}
