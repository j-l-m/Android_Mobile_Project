package edu.uwi.sta.comp3275.models;

import android.media.MediaRecorder;

import java.io.IOException;

/**
*
*
* Taken from:
* http://www.tutorialspoint.com/android/android_audio_capture.htm
*
*This class uses the MediaRecorder to record audio and encrypt it
*
* */
public class VoiceRecorder {

    //MediaRecorder object
    private  MediaRecorder recorder;
    //Encryptor
    private VoiceEncryptor voiceEncryptor;

    /*
      Constructor
     */
    public VoiceRecorder(VoiceEncryptor ve){
        recorder = new MediaRecorder();
        voiceEncryptor = ve;
    }

    /*
      Re-initializes the MediaRecorder and configures
      attributes for recording
     */
    private void setUpRecorder(){
        recorder=new MediaRecorder();
        recorder.reset();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
    }

    /*
      Starts recording by invoking the MediaRecorder.start() method
     */
    public void record(String filePath){
        setUpRecorder();
        recorder.setOutputFile(filePath);
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
    }

    /*
      Stops recording by invoking the MediaRecorder.stop() method
      File is encrypted after recording
     */
    public void stop(String filePath){
        recorder.stop();
        recorder.release();
        recorder = null;
        voiceEncryptor.encrypt(filePath);
    }




}
