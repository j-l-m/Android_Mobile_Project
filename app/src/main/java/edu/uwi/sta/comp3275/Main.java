package edu.uwi.sta.comp3275;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import edu.uwi.sta.comp3275.models.Decryptor;
import edu.uwi.sta.comp3275.models.Encryptor;
import edu.uwi.sta.comp3275.models.VoiceEncryptor;


/*
*
* Taken from:
* http://www.tutorialspoint.com/android/android_audio_capture.htm
*
* */


public class Main extends AppCompatActivity {

    Button play_btn,stop_btn,record_btn;
    private MediaRecorder myAudioRecorder;
    private String outputFile = null;
    private VoiceEncryptor voiceEncryptor = new VoiceEncryptor("dontdillydally");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        play_btn = (Button)findViewById(R.id.btn_play);
        stop_btn = (Button)findViewById(R.id.btn_stop);
        record_btn = (Button)findViewById(R.id.btn_record);

        play_btn.setEnabled(false);
        stop_btn.setEnabled(false);

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.mp4";

        myAudioRecorder=new MediaRecorder();
        myAudioRecorder.reset();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        myAudioRecorder.setOutputFile(outputFile);


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void stop (View view){
        myAudioRecorder.stop();
        myAudioRecorder.release();
        /*Encryptor encryptor = new Encryptor("dontdillydally");
        encryptor.encrypt();*/
        voiceEncryptor.encrypt();
        myAudioRecorder = null;

        stop_btn.setEnabled(false);
        play_btn.setEnabled(true);

        Toast.makeText(this, "Audio recorded successfully", Toast.LENGTH_LONG).show();
    }

    public void record (View view){
        try {
            myAudioRecorder.prepare();
            myAudioRecorder.start();
        }

        catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        record_btn.setEnabled(false);
        stop_btn.setEnabled(true);


        Toast.makeText(this, "Recording started", Toast.LENGTH_LONG).show();
    }

    public void play(View view){
     /*   Decryptor Decryptor = new Decryptor("dontdillydally");
        Decryptor.decrypt();*/
        voiceEncryptor.decrypt();
        MediaPlayer m = new MediaPlayer();

        m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                mp = null;
                setUpRecorder();
                play_btn.setEnabled(false);
                record_btn.setEnabled(true);
                stop_btn.setEnabled(false);
            }
        });

        try {
            m.setDataSource(outputFile);
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        try {
            m.prepare();
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        m.start();
        Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
    }

    public void setUpRecorder(){
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.mp4";

        myAudioRecorder=new MediaRecorder();
        myAudioRecorder.reset();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        myAudioRecorder.setOutputFile(outputFile);
    }

}
