package edu.uwi.sta.comp3275.models;

import android.os.Environment;

import java.util.HashMap;

import edu.uwi.sta.comp3275.PlayEncryptedMessages;
import edu.uwi.sta.comp3275.SendVoiceMessage;
import edu.uwi.sta.comp3275.SetKey;

/**
 *
 */
public class Constants {


    //shared pref
    public static final String SHARED_PREF = "CryptoKey";

    //default secret key - not to be used.
    public static final String DEFAULT_KEY = "";

    //location of key in sharedpreference
    public  static final String KEY = "key";

    //file path for recording voice messages
    public static final String RECORD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/EncryptedRecordings";

    //file prefix and extension
    public static final String PREFIX = "encrypted-";
    public static final String EXT = ".mp4";

    //Map Activities
    public static Class getClass(String str){
        HashMap h = activities();
        return (Class)h.get(str);
    }

    //list of activities
    public static final String[] ACTIVITIES = activityList();







    private static HashMap activities(){
        HashMap hashMap = new HashMap();
        hashMap.put("Set Key", SetKey.class);
        hashMap.put("Play Voice Message", PlayEncryptedMessages.class);
        hashMap.put("Send Voice Message", SendVoiceMessage.class);
        return hashMap;
    }



    private static String[] activityList() {
        String[] strings = new String[]{"Send Voice Message","Play Voice Message","Set Key"};
        return strings;
    }


}
