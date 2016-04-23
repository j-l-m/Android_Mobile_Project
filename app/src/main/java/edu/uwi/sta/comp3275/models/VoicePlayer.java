package edu.uwi.sta.comp3275.models;


import android.content.Context;
import android.media.MediaPlayer;
import android.widget.Toast;

import java.io.IOException;

/**
 * Taken from:
 * http://www.tutorialspoint.com/android/android_audio_capture.htm
 *
 *  Voice player class that uses the MediaPlayer class to enable playback of voice files
 *  This class decrypts encrypted audio files, play the audio, and then encrypts the file again
 */
public class VoicePlayer {
    //mediaplayer
    public static MediaPlayer mediaPlayer;
    //Encryptor
    private VoiceEncryptor voiceEncryptor;
    private Context context;
    //on complete listener interface
    private OnVoicePlayerComplete vpc;
    //path of file for playback
    private String filePath;

    /*
    Constructor
     */
    public VoicePlayer(VoiceEncryptor v, Context context){
        voiceEncryptor = v;
        this.context = context;
        mediaPlayer = new MediaPlayer();
        setListener();
    }

    /*
      Decrypts and plays file
     */
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
            mediaPlayer.start();
            Toast.makeText(context, "Playing audio", Toast.LENGTH_LONG).show();
        }

        catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Could not decrypt, File is now unusable", Toast.LENGTH_LONG).show();
        }

    }

    /*
      Set onCompletionListener
      Allows for file to be encrypted automatically after playback has finished
     */
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


    /*
      Stops playback and encrypts the file
     */
    public void stop(){
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
            voiceEncryptor.encrypt(filePath);
            filePath = null;
        }
    }

    public int getAudioSessionID(){
        return mediaPlayer.getAudioSessionId();
    }

    /*
      Allows the application to implement the OnVoicePlayerComplete interface methods
     */
    public void setOnComplete(OnVoicePlayerComplete onComplete){
        this.vpc = onComplete;
    }

    /*
      Interface that allows the activity using the VoicePlayer class to listen for completion
      and perform actions on completion of playback
     */
    public interface OnVoicePlayerComplete{

        public void onComplete();
    }


}
