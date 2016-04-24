package edu.uwi.sta.comp3275.models;

import java.io.File;


/*
 * Used to get desired string output from File object in list view
 */
public class CustomFile {

    private File f;

    public CustomFile(String path){
        f = new File(path);
    }

    //getter for File f
    public File getFile(){
        return f;
    }


    //returns the file name rather than the full path of the file
    @Override
    public String toString() {
        return f.getName();
    }
    
    
}
