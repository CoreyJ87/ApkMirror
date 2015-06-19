package info.coreyjones.apkmirror;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

public class InstallMenu extends ActionBarActivity implements deleteFile.deleteFileListener {
    final Context context = this;
    protected TextView selectedItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install_menu);
        initFileList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initFileList();
    }

    protected void initFileList() {
        File sdCardRoot = Environment.getExternalStorageDirectory();
        File downloadDir = new File(sdCardRoot, "Download");
        File[] fileList = downloadDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.endsWith(".apk")) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        Arrays.sort(fileList);

        String[] theNamesOfFiles = new String[fileList.length*2];
        int fileIndex = 0;
        int installIndex = 1;
        for (int i = 0; i < fileList.length; i++) {
                theNamesOfFiles[fileIndex] = fileList[i].getName();
                theNamesOfFiles[installIndex] = "Manage";
            fileIndex = fileIndex+2;
            installIndex=installIndex+2;
        }
        ArrayAdapter<String> fileAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,theNamesOfFiles);

        final GridView theGrid = (GridView) findViewById(R.id.gridView);
        theGrid.setAdapter(fileAdapter);
        theGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if(position % 2 != 0) {
                    int itemNum = position - 1;
                    selectedItem = (TextView) theGrid.getChildAt(itemNum);
                    handleManageButton();
                }
            }
        });
    }

    protected void handleManageButton() {
        final Dialog optionDialog = new Dialog(context);
        optionDialog.setContentView(R.layout.dialog);
        optionDialog.setTitle("Options");

        Button cancelButton = (Button) optionDialog.findViewById(R.id.dialogButtonCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionDialog.dismiss();
            }
        });

        Button deleteButton = (Button) optionDialog.findViewById(R.id.dialogButtonDelete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionDialog.dismiss();
                DialogFragment deleteDialog = new deleteFile();
                deleteDialog.show(getFragmentManager(), "deleteConfirm");
            }
        });

        Button installButton = (Button) optionDialog.findViewById(R.id.dialogButtonInstall);
        installButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionDialog.dismiss();
                String text = selectedItem.getText().toString();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/Download/" + text)), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        optionDialog.show();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        String app = selectedItem.getText().toString();
        File sdCardRoot = Environment.getExternalStorageDirectory();
        File downloadDir = new File(sdCardRoot, "Download");
        String downloadDirPath = downloadDir.toString();
        String fullAppPath = downloadDirPath+"/"+app;
        File file = new File(downloadDirPath+"/"+app);
        boolean deleted = file.delete();
        if(deleted) {
            Log.d("Delete", fullAppPath+": deleted");
        }
        else
        {
            Log.d("File Not Deleted","file not deleted");
        }
        initFileList();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        //Cancelled
    }
}