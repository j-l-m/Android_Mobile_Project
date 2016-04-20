package edu.uwi.sta.comp3275;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.uwi.sta.comp3275.models.VoiceEncryptor;
import edu.uwi.sta.comp3275.models.VoicePlayer;
import edu.uwi.sta.comp3275.models.VoiceRecorder;

public class SendVoiceMessage extends AppCompatActivity {

    ImageButton play_btn,stop_btn,record_btn,send_btn;
    private MediaRecorder myAudioRecorder;
    private String outputFile = null;
    private VoiceEncryptor voiceEncryptor = new VoiceEncryptor("dontdillydally");
    private SimpleDateFormat format;
    private Date date;
    private VoiceRecorder vr;

    private static final String RECORD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/EncryptedRecordings";

    private static final String PREFIX = "encrypted-";
    private static final String EXT = ".mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_voice_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        play_btn = (ImageButton)findViewById(R.id.btn_play);
        stop_btn = (ImageButton)findViewById(R.id.btn_stop);
        record_btn = (ImageButton)findViewById(R.id.btn_record);
        send_btn = (ImageButton)findViewById(R.id.btn_send);

        disableBtn(stop_btn);
        disableBtn(play_btn);

        //outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.mp4";

     /*   myAudioRecorder=new MediaRecorder();
        myAudioRecorder.reset();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
       // myAudioRecorder.setOutputFile(outputFile);
    */
        File recordings = new File(RECORD_PATH);
        if (!recordings.exists()) {
            if (recordings.mkdirs()) {
                Log.d("FILE", "Successfully created folder");
            } else {
                Log.d("FILE", "Failed to create folder");
            }
        }
       // setUpRecorder();
        vr = new VoiceRecorder(voiceEncryptor);


    }

    public String getOutputPath(){
        date = new Date();
        format = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStr = format.format(date);
        return RECORD_PATH + "/"+PREFIX + dateStr + EXT;
    }

    public void disableBtn(ImageButton btn){
        btn.setEnabled(false);
        btn.setColorFilter(Color.argb(215, 255, 255, 255));
    }

    public void enableBtn(ImageButton btn){
        btn.setEnabled(true);
        btn.setColorFilter(Color.argb(0, 0, 0, 0));
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

        vr.stop(outputFile);

       /* myAudioRecorder.stop();
        myAudioRecorder.release();
        myAudioRecorder = null;
        voiceEncryptor.encrypt(outputFile);
        */
        disableBtn(stop_btn);
        enableBtn(play_btn);

        Toast.makeText(this, "Audio recorded successfully", Toast.LENGTH_LONG).show();
    }

    public void record (View view){

      /*  try {
            outputFile = getOutputPath();
            myAudioRecorder.setOutputFile(outputFile);
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
        }*/
        outputFile = getOutputPath();
        vr.record(outputFile);
        disableBtn(record_btn);
        enableBtn(stop_btn);

        Toast.makeText(this, "Recording started", Toast.LENGTH_LONG).show();
    }

    public void play(View view){

       // voiceEncryptor.decrypt();

        VoicePlayer vp = new VoicePlayer(voiceEncryptor, this);
        vp.setOnComplete(new VoicePlayer.OnVoicePlayerComplete() {
            @Override
            public void onComplete() {
                disableBtn(play_btn);
                disableBtn(stop_btn);
                enableBtn(record_btn);
                //setUpRecorder();
            }
        });
        vp.play(outputFile);

       /* MediaPlayer m = new MediaPlayer();

        m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                mp = null;
                setUpRecorder();

                disableBtn(play_btn);
                disableBtn(stop_btn);
                enableBtn(record_btn);
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
        Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();*/
    }

    public void setUpRecorder() {
        //outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.mp4";

        myAudioRecorder=new MediaRecorder();
        myAudioRecorder.reset();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //myAudioRecorder.setOutputFile(outputFile);
    }



}
