package info.coreyjones.apkmirror;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

public class InstallMenu extends ActionBarActivity {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
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
                theNamesOfFiles[installIndex] = "Install";
            fileIndex = fileIndex+2;
            installIndex=installIndex+2;
        }
        ArrayAdapter<String> fileAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,theNamesOfFiles);

        final GridView theGrid = (GridView) findViewById(R.id.gridView);
        theGrid.setAdapter(fileAdapter);
        theGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                int itemNum = position-1;
                TextView textView = (TextView)theGrid.getChildAt(itemNum);
                handleInstall(textView);
            }
        });
    }

    protected void handleInstall(TextView v) {
        String text = v.getText().toString();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/Download/" + text)), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
