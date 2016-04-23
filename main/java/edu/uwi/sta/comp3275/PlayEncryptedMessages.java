package edu.uwi.sta.comp3275;

import android.Manifest;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import edu.uwi.sta.comp3275.models.Constants;
import edu.uwi.sta.comp3275.models.FileListDialog;
import edu.uwi.sta.comp3275.models.VisualizerView;
import edu.uwi.sta.comp3275.models.VoiceEncryptor;
import edu.uwi.sta.comp3275.models.VoicePlayer;

public class PlayEncryptedMessages extends AppCompatActivity {

    //TextView that displays the selected file's path
    private TextView fileLocation;
    //VoicePlayer object for playback
    private VoicePlayer vp;
    //Indicates if the voice player is currently playing
    private  boolean playing = false;
    //Media control buttons
    private ImageButton stop_btn, play_btn;
    //Encryptor
    private VoiceEncryptor voiceEncryptor;
    //path of selected file
    private String file;
    //permissions check
    private  boolean hasPermission;

    private Visualizer mVisualizer;
    private VisualizerView mVisualizerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_encrypted_messages);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        checkPermission();

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


        //initialize Views
        fileLocation = (TextView)findViewById(R.id.txt_file_location);

        stop_btn = (ImageButton)findViewById(R.id.btn_stop);
        play_btn = (ImageButton)findViewById(R.id.btn_play);

        disableBtn(stop_btn);
        disableBtn(play_btn);


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
       onClick method of stop button
       Stops voicePlayer playback and
       resets buttons
     */
    public void stop (View view){

        if(vp!=null) {
            mVisualizer.setEnabled(false);
            vp.stop();
            playing = false;
        }
        disableBtn(stop_btn);
        enableBtn(play_btn);
    }

    /*
       onClick method of play button
       Starts voicePlayer playback and
       sets buttons
     */
    public void play(View view){
        vp = new VoicePlayer(voiceEncryptor, this);
        setupVisualizerFxAndUI();
        vp.setOnComplete(new VoicePlayer.OnVoicePlayerComplete() {
            //Actions performed on completion of playback
            @Override
            public void onComplete() {
                disableBtn(stop_btn);
                enableBtn(play_btn);
                playing = false;
                mVisualizer.setEnabled(false);
            }
        });
        disableBtn(play_btn);
        enableBtn(stop_btn);
        play_btn.setColorFilter(Color.argb(215, 0, 200, 150)); //color while playing
        playing = true;
        mVisualizer.setEnabled(true);
        vp.play(file);
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
    onClick method of the Select File button
    invokes showAlertDialog();
     */
    public void displayFiles(View v){
        showAlertDialog();
    }

    /*
     Creates and displays the FileListDialog
     */
    private void showAlertDialog() {
        FragmentManager fm = getFragmentManager();
        FileListDialog alertDialog = FileListDialog.newInstance("Some title");
        alertDialog.setDialogResult(new FileListDialog.DialogResult() {
            //get selected file from list by implementing DialogResult interface methods
            @Override
            public void finish(File result) {
                fileLocation.setText(result.getAbsolutePath());
                file = result.getAbsolutePath();
                enableBtn(play_btn);
            }
        });
        alertDialog.show(fm, "fragment_alert");
    }

}
