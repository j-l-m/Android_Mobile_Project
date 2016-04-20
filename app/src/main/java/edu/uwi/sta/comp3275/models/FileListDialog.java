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

/**
 * Created by JM on 4/19/2016.
 */
public class FileListDialog extends DialogFragment {

    private File parent_dir, current_dir;
    private List<CustomFile> fileList = new ArrayList<>();
    private ArrayAdapter<CustomFile> fileAdapter;
    private ListView lv;
    private Button moveUp;
    private DialogResult dialogResult;


    public FileListDialog() {
        // Empty constructor required for DialogFragment
        parent_dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        current_dir = parent_dir;
    }

    public static FileListDialog newInstance(String title) {
        FileListDialog frag = new FileListDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialog = inflater.inflate(R.layout.file_list, null);
        builder.setView(dialog);

        moveUp = (Button) dialog.findViewById(R.id.move_up);
        moveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildFileList(current_dir);
            }
        });

        lv = (ListView)dialog.findViewById(R.id.dialog_file_list);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File selected = fileList.get(position).getFile();
                if (selected.isDirectory()) {
                    buildFileList(selected);
                } else if (selected.isFile()) {
                    //setSelected(selected);
                    if (dialogResult != null)
                        dialogResult.finish(selected);
                    dismiss();
                }
            }
        });
        builder.setTitle("File selector");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        buildFileList(current_dir);
        return builder.create();
    }


    public void buildFileList(File file){

        File[] files = file.listFiles();
        System.out.print(Arrays.toString(files));
        fileList.clear();

        for(File f : files){

            if((!f.isDirectory() && isEncryptedRecording(f)) || f.isDirectory())
            fileList.add(new CustomFile(f.getPath()));
        }
        fileAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1, fileList);

        lv.setAdapter(fileAdapter);
    }


    public boolean isEncryptedRecording(File file){
        String filename = file.getName();
        return filename.startsWith(Constants.PREFIX) && filename.endsWith(Constants.EXT);
    }



    public void setDialogResult(DialogResult result){
        this.dialogResult = result;
    }



    public interface DialogResult{
        void finish(File result);
    }

}
