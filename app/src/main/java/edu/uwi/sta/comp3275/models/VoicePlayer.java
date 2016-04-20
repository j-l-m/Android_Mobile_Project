package edu.uwi.sta.comp3275.models;


import android.content.Context;
import android.media.MediaPlayer;
import android.widget.Toast;

import java.io.IOException;

/**
 *
 */
public class VoicePlayer {

    public static MediaPlayer mediaPlayer;
    private VoiceEncryptor voiceEncryptor;
    private Context context;
    private OnVoicePlayerComplete vpc;
    private String filePath;

    public VoicePlayer(VoiceEncryptor v, Context context){
        voiceEncryptor = v;
        this.context = context;
        mediaPlayer = new MediaPlayer();
        setListener();
    }


    public void play(String path){

        filePath = path;
        voiceEncryptor.decrypt(filePath);


        try {
            mediaPlayer.setDataSource(path);
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mediaPlayer.prepare();
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.start();
        Toast.makeText(context, "Playing audio", Toast.LENGTH_LONG).show();
    }


    public void setListener(){
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                VoicePlayer.mediaPlayer.release();
                VoicePlayer.mediaPlayer = null;
                voiceEncryptor.encrypt(filePath);
                if (vpc != null)
                    vpc.onComplete();
                filePath = null;
            }
        });
    }


    public void stop(){
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
            voiceEncryptor.encrypt(filePath);
            filePath = null;
        }
    }


    public void setOnComplete(OnVoicePlayerComplete onComplete){
        this.vpc = onComplete;
    }

    public interface OnVoicePlayerComplete{

        public void onComplete();
    }


}
