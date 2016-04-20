package edu.uwi.sta.comp3275;

import android.content.Context;
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


    private EditText key, confirm_key;
    private TextView confirm_status, prompt;

    private static final String MATCHED = "Confirmed!";
    private static final String NO_MATCH = "Keys entered do not match";
    private static final int POSITIVE = Color.argb(255, 0, 250, 0);
    private static final int NEGATIVE = Color.argb(255, 250, 0, 0);
    private boolean confirmed = false;

    private SharedPreferences pref;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_key);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        key = (EditText)findViewById(R.id.txt_key_input);
        confirm_key = (EditText)findViewById(R.id.txt_confirm_input);
        confirm_status = (TextView)findViewById(R.id.txt_confirmation);
        prompt = (TextView)findViewById(R.id.txt_prompt);


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



    public void setConfirmListener(){
        confirm_key.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                confirm_status.setText("...");
            }

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


    public void submit(View v){
        String str = key.getText().toString();
        if(confirmed && validate(str)){
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(Constants.KEY, str);
            editor.commit();
            prompt.setText("");
            Toast.makeText(SetKey.this, "Key stored", Toast.LENGTH_SHORT).show();
        }
        else Toast.makeText(SetKey.this, "Invalid Key: Fields cannot be blank", Toast.LENGTH_SHORT).show();
    }


    public boolean validate(String str){
        return(str!=null && !str.equals(""));
    }


}
