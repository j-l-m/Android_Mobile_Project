package edu.uwi.sta.comp3275;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Arrays;

import edu.uwi.sta.comp3275.models.Constants;




public class Main extends AppCompatActivity {

    //Main listView
    private ListView main_list;
    //ArrayAdapter
    private ArrayAdapter<String> adapter;
    //Check permission
    private boolean hasPermission;

    private static final int REQUESTS = 100;//permissions reques code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        checkPermission();

        //initialize UI Elements
        main_list = (ListView)findViewById(R.id.list_main);
        //Uses Constants.ACTIVITIES Array
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Constants.ACTIVITIES);
        main_list.setAdapter(adapter);
        setListener();


    }


    //Check for permissions
    protected void checkPermission(){
        hasPermission = (ActivityCompat.checkSelfPermission(Main.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(Main.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        Log.d("MAIN", "Permission " + hasPermission);

        if(!hasPermission)
            ActivityCompat.requestPermissions(Main.this, new String[]{Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUESTS);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        System.out.println(Arrays.toString(grantResults));

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case  REQUESTS: {
                if(grantResults.length == 2 && grantResults[0]+grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    //do nothing
                }
                else{
                    Toast.makeText(this, "Permissions not granted. You will not be able to use all features", Toast.LENGTH_LONG).show();
                }
            }
        }


    }

    /*
      Sets the onItemClick Listener of the main_list ListView
      starts the activity selected from the list
     */
    protected void setListener(){
        main_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                activityStart(Constants.ACTIVITIES[position]);
            }
        });
    }

    /*
      Accepts a string selected from the main list
      String is passed to the Constants.getClass() function
      which returns the class used to start the selected activity
     */
    protected void activityStart(String act){
        Class c = Constants.getClass(act);
        if(c!=null){
            startActivity(new Intent(Main.this, c ));
        }
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



}
