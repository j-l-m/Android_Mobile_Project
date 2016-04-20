package edu.uwi.sta.comp3275.models;


import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;
import java.util.List;

/*
 *https://tsicilian.wordpress.com/2012/11/06/bluetooth-data-transfer-with-android/
 */
public class Sender {

    public static final int DISCOVER_DURATION = 300;


    /*
      Create intent to start device bluetooth discovery activity
     */
    public static Intent createIntent(){
        Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVER_DURATION);
        return discoveryIntent;
    }

    /*
      Creates the implicit intent to start bluetooth file transfer
      by calling the device's built in bluetooth transfer activity
     */
    public static Intent processResult(String path, Context context){

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("*/*"); //MIME type for unspecified file type
        File file = new File(path);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));


        PackageManager pm = context.getPackageManager();
        //List apps that respond to an implicit intent with SEND Action
        List<ResolveInfo> appsList = pm.queryIntentActivities( intent, 0);

        if(appsList.size() > 0 ){
            //Find the bluetooth activity
            String packageName = null;
            String className = null;
            boolean found = false;

            for(ResolveInfo info: appsList){
                packageName = info.activityInfo.packageName;
                if( packageName.equals("com.android.bluetooth")){
                    className = info.activityInfo.name;
                    found = true;
                    break;// found bluetooth activity
                }
            }
            if(!found){
                Toast.makeText(context, "Bluetooth Activity not found", Toast.LENGTH_SHORT).show();
                // exit
            }
            else{
                intent.setClassName(packageName, className);
                return intent;
            }
        }
        return null;
    }


}