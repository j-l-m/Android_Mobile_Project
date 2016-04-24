package edu.uwi.sta.comp3275;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.uwi.sta.comp3275.models.Constants;
import edu.uwi.sta.comp3275.models.Sender;
import edu.uwi.sta.comp3275.models.VoiceEncryptor;
import edu.uwi.sta.comp3275.models.VoicePlayer;
import edu.uwi.sta.comp3275.models.VisualizerView;
import edu.uwi.sta.comp3275.models.VoiceRecorder;


public class SendVoiceMessage extends AppCompatActivity {

    //Media buttons
    ImageButton play_btn,stop_btn,record_btn,send_btn;
    //Output file path for recording
    private String outputFile = null;
    //Encryptor
    private VoiceEncryptor voiceEncryptor;
    //Voice Recorder and player
    private VoiceRecorder vr;
    private VoicePlayer vp;
    //Indicate if player is playing
    private  boolean playing = false;
    //for checking permissions
    private boolean hasPermission;
    //Visualizer object and view
    private Visualizer mVisualizer;
    private VisualizerView mVisualizerView;

    //Bluetooth
    private static final int REQUEST_BLU = 1;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_voice_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        checkPermission();

        play_btn = (ImageButton)findViewById(R.id.btn_play);
        stop_btn = (ImageButton)findViewById(R.id.btn_stop);
        record_btn = (ImageButton)findViewById(R.id.btn_record);
        send_btn = (ImageButton)findViewById(R.id.btn_send);

        disableBtn(stop_btn);
        disableBtn(play_btn);
        disableBtn(send_btn);

        //Create recordings directory if it doesn't Exist
        File recordings = new File(Constants.RECORD_PATH);
        if (!recordings.exists()) {
            if (recordings.mkdirs()) {
                Log.d("FILE", "Successfully created folder");
            } else {
                Log.d("FILE", "Failed to create folder");
            }
        }
        //Get encryption key from shared preferences
        SharedPreferences pref = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
        String key = pref.getString(Constants.KEY, Constants.DEFAULT_KEY);

        //Determine if user can access feature by ensuring the default key was changed
        if(!key.equals(Constants.DEFAULT_KEY))
            voiceEncryptor = new VoiceEncryptor(key);
        else{
            Toast.makeText(this, "please change the default key in settings to use this feature", Toast.LENGTH_SHORT).show();
            finish();
        }

        //initialize bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        vr = new VoiceRecorder(voiceEncryptor);


    }

    //Check for permissions to determine if user can access this activities features
    protected void checkPermission(){
        hasPermission = (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        Log.d("MAIN", "Permission " + hasPermission);

        if(!hasPermission){
            Toast.makeText(this, "Please enable all Permissions in settings to use this feature", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    /*
    http://android-er.blogspot.com/2015/02/create-audio-visualizer-for-mediaplayer.html
    */
    private void setupVisualizerFxAndUI() {

        // Create the Visualizer object and attach it to our media player.
        mVisualizer = new Visualizer(vp.getAudioSessionID());
        mVisualizerView = (VisualizerView)findViewById(R.id.myvisualizerview);
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer,
                                                      byte[] bytes, int samplingRate) {
                        mVisualizerView.updateVisualizer(bytes);
                    }

                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] bytes, int samplingRate) {
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }


    /*
        Sets the output path and filename for the recording
        e.g. filename =  encryption-20160418040145.mp4
        yyyy-mm-dd-HH-mm-ss
     */
    public String getOutputPath(){
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStr = format.format(date);
        return Constants.RECORD_PATH + "/"+ Constants.PREFIX + dateStr + Constants.EXT;
    }

    /*
     Disables buttons and changes button appearance
    */
    public void disableBtn(ImageButton btn){
        btn.setEnabled(false);
        btn.setColorFilter(Color.argb(215, 255, 255, 255));
    }

    /*
      Enables buttons and changes button appearance
     */
    public void enableBtn(ImageButton btn){
        btn.setEnabled(true);
        btn.setColorFilter(Color.argb(0, 0, 0, 0));
    }


    /*
     onClick method of the send button (bluetooth icon)
     starts bluetooth file transfer by starting an activity for result
     */
    public void send(View v){
        if( bluetoothAdapter!=null && outputFile!=null){
            startActivityForResult(Sender.createIntent(), REQUEST_BLU);
        }
    }

    /*
      If user enables allows the bluetooth
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Sender.DISCOVER_DURATION  && requestCode == REQUEST_BLU) {
            Intent i = Sender.processResult(outputFile, this);
            if (i != null) {
                startActivity(i);
            }
        }
        else Toast.makeText(this, "Bluetooth Transfer Denied", Toast.LENGTH_LONG).show();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /*
      onClick method of the stop button
      Stops recording and playback
      resets buttons accordingly
     */
    public void stop (View view){
        //stop recording
        if(!playing) {
            vr.stop(outputFile);
            enableBtn(send_btn);
            Toast.makeText(this, "Audio recorded successfully", Toast.LENGTH_LONG).show();
        }
        //stop playback
        else if(vp!=null) {
                vp.stop();
                playing = false;
                mVisualizer.setEnabled(false);
        }
        disableBtn(stop_btn);
        enableBtn(play_btn);
        enableBtn(record_btn);
    }


    /*
      onClick method of record button
      starts recording
        if outputFile is not set, it sets the filepath and record to this path
        else if outputFile was previously set, it deletes this file and then resets
            the outputFile path for recording
     */
    public void record (View view){

        if(outputFile!=null){
            File lastRecording = new File(outputFile);
            if(lastRecording.delete()) Log.d("File", "Delete Successful");
            else Log.d("File", "Delete Failed");
        }

        outputFile = getOutputPath();
        vr.record(outputFile);
        disableBtn(record_btn);
        record_btn.setColorFilter(Color.argb(215, 255, 0, 0)); //color while recording
        disableBtn(play_btn);
        enableBtn(stop_btn);

        Toast.makeText(this, "Recording started", Toast.LENGTH_LONG).show();
    }

    /*
      onClick method of the play button
      starts playback of the current recording
      resets buttons accordingly
     */
    public void play(View view){

        vp = new VoicePlayer(voiceEncryptor, this);
        setupVisualizerFxAndUI();
        vp.setOnComplete(new VoicePlayer.OnVoicePlayerComplete() {
            //performs this actions when playback is completed
            @Override
            public void onComplete() {
                disableBtn(stop_btn);
                enableBtn(record_btn);
                enableBtn(play_btn);
                mVisualizer.setEnabled(false);
                playing=false;
            }
        });
        disableBtn(play_btn);
        disableBtn(record_btn);
        enableBtn(stop_btn);
        play_btn.setColorFilter(Color.argb(215, 0, 200, 150)); //color while playing
        playing = true;
        mVisualizer.setEnabled(true);
        vp.play(outputFile); //play the file located at outputFile path
    }

}
