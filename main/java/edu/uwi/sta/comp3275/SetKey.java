package edu.uwi.sta.comp3275;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import edu.uwi.sta.comp3275.models.Constants;

public class SetKey extends AppCompatActivity {

    //input fields for key and key confirm
    private EditText key, confirm_key;
    //status of matching fields key and confirm key. prompt text view is default key is not changed
    private TextView confirm_status, prompt;

    //Messages to user
    private static final String MATCHED = "Confirmed!";
    private static final String NO_MATCH = "Keys entered do not match";
    //Color for prompts and statuses
    private static final int POSITIVE = Color.argb(255, 0, 250, 0);
    private static final int NEGATIVE = Color.argb(255, 250, 0, 0);
    //both key entries match
    private boolean confirmed = false;
    //Shared preference for key storage
    private SharedPreferences pref;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_key);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialize Views
        key = (EditText)findViewById(R.id.txt_key_input);
        confirm_key = (EditText)findViewById(R.id.txt_confirm_input);
        confirm_status = (TextView)findViewById(R.id.txt_confirmation);
        prompt = (TextView)findViewById(R.id.txt_prompt);

        //set text changed listener
        setConfirmListener();
        pref = getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);

        checkDefault();
    }


    public void checkDefault(){
        String key = pref.getString(Constants.KEY, Constants.DEFAULT_KEY);
        if (key.equals(Constants.DEFAULT_KEY)){
            prompt.setText("Key is currently set to default. Please set a new key below");
        }
    }


    /*
      adds a text changed listener to the confirm_key editText field
      dynamically checks if key and confirm_key field match
     */
    public void setConfirmListener(){
        confirm_key.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                confirm_status.setText("...");
            }

            /*
              onTextChanged check if confirm_key field matches the key field
              set the confirm status accordingly
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals(key.getText().toString())){
                    confirm_status.setText(MATCHED);
                    confirm_status.setTextColor(POSITIVE);
                    confirmed = true;
                }
                else{
                    confirm_status.setText(NO_MATCH);
                    confirm_status.setTextColor(NEGATIVE);
                    confirmed = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /*
      Ensures that the key is valid and
      stores the key in SharedPreferences
     */
    public void submit(View v){
        String str = key.getText().toString();
        if(confirmed && validate(str)){
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(Constants.KEY, str);
            editor.commit();
            prompt.setText("");
            Toast.makeText(SetKey.this, "Key stored", Toast.LENGTH_LONG).show();
            Intent i = new Intent(SetKey.this, Main.class); // go back to main screen if set success
            startActivity(i);
        }
        else Toast.makeText(SetKey.this, "Invalid Key: Fields cannot be blank", Toast.LENGTH_SHORT).show();
    }

    /*
      Validates the key that was entered by the user
     */
    public boolean validate(String str){
        return(str!=null && !str.equals(""));
    }


}
