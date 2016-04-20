package edu.uwi.sta.comp3275;

import android.Manifest;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import edu.uwi.sta.comp3275.models.Constants;
import edu.uwi.sta.comp3275.models.FileListDialog;
import edu.uwi.sta.comp3275.models.VoiceEncryptor;
import edu.uwi.sta.comp3275.models.VoicePlayer;

public class PlayEncryptedMessages extends AppCompatActivity {

    private TextView fileLocation;
    private VoicePlayer vp;
    private  boolean playing = false;
    private ImageButton stop_btn, play_btn;
    private VoiceEncryptor voiceEncryptor;
    private String file;
    private  boolean hasPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_encrypted_messages);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        checkPermission();

        SharedPreferences pref = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
        String key = pref.getString(Constants.KEY, Constants.DEFAULT_KEY);

        if(!key.equals(Constants.DEFAULT_KEY))
            voiceEncryptor = new VoiceEncryptor(key);
        else{
            Toast.makeText(this, "please change the default key in settings to use this feature", Toast.LENGTH_SHORT).show();
            finish();
        }

        fileLocation = (TextView)findViewById(R.id.txt_file_location);

        stop_btn = (ImageButton)findViewById(R.id.btn_stop);
        play_btn = (ImageButton)findViewById(R.id.btn_play);

        disableBtn(stop_btn);
        disableBtn(play_btn);


    }


    protected void checkPermission(){
        hasPermission = (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        Log.d("MAIN", "Permission " + hasPermission);

        if(!hasPermission){
            Toast.makeText(this, "Please enable all Permissions in settings to use this feature", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    public void disableBtn(ImageButton btn){
        btn.setEnabled(false);
        btn.setColorFilter(Color.argb(215, 255, 255, 255));
    }

    public void enableBtn(ImageButton btn){
        btn.setEnabled(true);
        btn.setColorFilter(Color.argb(0, 0, 0, 0));
    }

    public void stop (View view){

        if(vp!=null) {
            vp.stop();
            playing = false;
        }
        disableBtn(stop_btn);
        enableBtn(play_btn);
    }


    public void play(View view){
        vp = new VoicePlayer(voiceEncryptor, this);
        vp.setOnComplete(new VoicePlayer.OnVoicePlayerComplete() {
            @Override
            public void onComplete() {
                disableBtn(stop_btn);
                enableBtn(play_btn);
                playing = false;
            }
        });
        disableBtn(play_btn);
        enableBtn(stop_btn);
        play_btn.setColorFilter(Color.argb(215, 0, 200, 150)); //color while playing
        playing = true;
        vp.play(file);
    }



    public void displayFiles(View v){
        showAlertDialog();
    }

    private void showAlertDialog() {
        FragmentManager fm = getFragmentManager();
        FileListDialog alertDialog = FileListDialog.newInstance("Some title");
        alertDialog.setDialogResult(new FileListDialog.DialogResult() {
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
