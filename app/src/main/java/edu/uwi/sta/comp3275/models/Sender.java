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

/**
 * Created by JM on 4/19/2016.
 */
public class Sender {

    public static final int DISCOVER_DURATION = 300;



    public static Intent createIntent(){
        Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVER_DURATION);
        return discoveryIntent;
    }


    public static Intent processResult(String path, Context context){

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("*/*");
        File file = new File(path);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> appsList = pm.queryIntentActivities( intent, 0);

        if(appsList.size() > 0 ){
            // proceed
            //select bluetooth
            String packageName = null;
            String className = null;
            boolean found = false;

            for(ResolveInfo info: appsList){
                packageName = info.activityInfo.packageName;
                if( packageName.equals("com.android.bluetooth")){
                    className = info.activityInfo.name;
                    found = true;
                    break;// found
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