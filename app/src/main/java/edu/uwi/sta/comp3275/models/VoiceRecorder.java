package edu.uwi.sta.comp3275.models;

import android.media.MediaRecorder;

import java.io.IOException;

/**
 *
 */
public class VoiceRecorder {

    private  MediaRecorder recorder;
    private VoiceEncryptor voiceEncryptor;

    public VoiceRecorder(VoiceEncryptor ve){
        recorder = new MediaRecorder();
        voiceEncryptor = ve;
    }

    private void setUpRecorder(){
        recorder=new MediaRecorder();
        recorder.reset();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
    }


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

    public void stop(String filePath){
        recorder.stop();
        recorder.release();
        recorder = null;
        voiceEncryptor.encrypt(filePath);
    }




}
