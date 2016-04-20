package edu.uwi.sta.comp3275.models;

import java.io.File;


/**
 * Created by JM on 4/20/2016.
 *
 * Used to get desired string output from file
 */
public class CustomFile {

    private File f;

    public CustomFile(String path){
        f = new File(path);
    }


    public File getFile(){
        return f;
    }


    @Override
    public String toString() {
        return f.getName();
    }
    
    
}
