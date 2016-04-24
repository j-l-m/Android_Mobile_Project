package edu.uwi.sta.comp3275.models;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.uwi.sta.comp3275.R;

/*
 *  A custom file selector dialog
 */

public class FileListDialog extends DialogFragment {

    //base directory and current directory
    private File base_dir, current_dir;

    //Array list used to display files on device storage
    private List<CustomFile> fileList;

    //Adapter used with file list
    private ArrayAdapter<CustomFile> fileAdapter;

    //file list ListView
    private ListView lv;

    //Button used to move up one directory level
    private Button moveUp;

    //used to move data from this diaglog class to the Activity using it
    private DialogResult dialogResult;


    /*
     Constructor for FileListDialog
     */
    public FileListDialog() {
        base_dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        current_dir = base_dir;
    }

    /*
     Returns an instance of the FileListDialog Fragment
     */
    public static FileListDialog newInstance(String title) {
        FileListDialog frag = new FileListDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    /*
     Initializes UI elements of the Dialog
     see layout/file_list.xml
     Sets click listeners and enables directory navigation
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialog = inflater.inflate(R.layout.file_list, null);
        builder.setView(dialog);

        moveUp = (Button) dialog.findViewById(R.id.move_up);
        moveUp.setOnClickListener(new View.OnClickListener() {
            /*
                Update List to show contents of parent directory of current_dir
             */
            @Override
            public void onClick(View v) {
                if(!base_dir.equals(current_dir))
                    buildFileList(current_dir.getParentFile());
            }
        });

        //initialize list view, fileList and array adapter
        lv = (ListView)dialog.findViewById(R.id.dialog_file_list);
        fileList = new ArrayList<>();
        fileAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1, fileList);
        lv.setAdapter(fileAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            /*
               If the selected item on the list is a directory, then update list with
                buildFileList();
                Else if selected file is a file run the finish() method of the DialogResult interface
                    this enables the selected file to be passed from the dialog to the activity
                    then dismiss dialog
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File selected = fileAdapter.getItem(position).getFile();//fileList.get(position).getFile();
                if (selected.isDirectory()) {
                    buildFileList(selected);
                } else if (selected.isFile()) {
                    if (dialogResult != null)
                        dialogResult.finish(selected);
                    dismiss();
                }
            }
        });
        builder.setTitle("File selector");  //set title of dialog
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing
            }
        });
        buildFileList(current_dir);
        return builder.create();
    }


    /*
        Builds the list of files displayed in the dialog
        updates the fileList ArrayList and notifies the ArrayAdapter
     */
    public void buildFileList(File file){

        File[] files = file.listFiles();
        System.out.print(Arrays.toString(files));
        fileList.clear();
        current_dir=file;

        for(File f : files){
            //ensures only files that can be used by the app are displayed
            if((!f.isDirectory() && isEncryptedRecording(f)) || f.isDirectory())
            fileList.add(new CustomFile(f.getPath()));
        }

        fileAdapter.notifyDataSetChanged();
    }


    /*
     Verifies that file name matches the naming convention for the application's
     encrypted files
     see model/Constants.java for values
     */
    public boolean isEncryptedRecording(File file){
        String filename = file.getName();
        return filename.startsWith(Constants.PREFIX) && filename.endsWith(Constants.EXT);
    }



    /*
      Used to pass data from the dialog to the activity
     */
    public void setDialogResult(DialogResult result){
        this.dialogResult = result;
    }


    /*
     Interface used to pass data from the dialog to the activity
     The activity using the dialog implements this to get data selected from the list
     */
    public interface DialogResult{
        void finish(File result);
    }

}
