package info.coreyjones.apkmirror;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;


public class InstallMenu extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install_menu);
        initFileList();
    }


    protected void initFileList() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.theLinear);
        File sdCardRoot = Environment.getExternalStorageDirectory();
        File yourDir = new File(sdCardRoot, "Download");
        for (File f : yourDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.endsWith(".apk")) {
                    return true;
                } else {
                    return false;
                }
            }
        })) {
            if (f.isFile()) {
                String name = f.getName();


                Button installButton = new Button(this);
                installButton.setText(name);
                installButton.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                installButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleInstall(v);
                    }
                });
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    installButton.setId(Utils.generateViewId());

                } else {
                    installButton.setId(View.generateViewId());
                }

                layout.addView(installButton);

            }
        }
    }

    protected void handleInstall(View v) {
        Button button = (Button) v.findViewById(v.getId());
        String text = button.getText().toString();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/Download/" + text)), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
