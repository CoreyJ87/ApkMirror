package info.coreyjones.apkmirror;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;


public class MainMenu extends ActionBarActivity {

    protected final String siteURL = "http://apkmirror.com";
    protected WebView theWebView;
    protected boolean actionBarState = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        theWebView = setupWebView(siteURL, R.id.webView, savedInstanceState);
        theWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));
                request.allowScanningByMediaScanner();
                request.setDescription("Downloading"+theWebView.getTitle());
                request.setTitle(theWebView.getTitle());
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, theWebView.getTitle()+".apk");
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the state of the WebView
        theWebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore the state of the WebView
        theWebView.restoreState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
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

    //WebView Creator
    protected WebView setupWebView(String theURL, int TheViewID, Bundle savedInstanceState) {
        WebView theWebView = (WebView) findViewById(TheViewID);
        theWebView.setWebViewClient(new WebViewClient());

        WebSettings webSettings = theWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setSupportZoom(true);
        if (savedInstanceState == null) {
            // Load a page
            theWebView.loadUrl(theURL);
        }

        return theWebView;
    }

    //Hide Action Bar
    /* public void toggleActionBar(View view) {
        if (getSupportActionBar() != null && actionBarState) {
            hideActionBar();
        } else if (getSupportActionBar() != null && actionBarState == false) {
            showActionBar();
        }

    }

   public void hideActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
            actionBarState = !actionBarState;
            Button hideButton = (Button) findViewById(R.id.hideBar);
            hideButton.setText(getString(R.string.showBar));
        }
    }

    public void showActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
            actionBarState = !actionBarState;
            Button hideButton = (Button) findViewById(R.id.hideBar);
            hideButton.setText(getString(R.string.hideBar));
        }
    }*/

    //Refresh WebView - Pass in R.id.controlID
    protected void refreshPage(int TheID) {
        WebView theWebView = (WebView) findViewById(TheID);
        String currentURL = theWebView.getUrl();
        theWebView.loadUrl(currentURL);
        toastMessage(getString(R.string.refreshString));
    }


    //Toast Message Wrapper for easier use.
    protected void toastMessage(CharSequence text) {
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

    //Immersion Mode Function from Google
    protected void toggleHideyBar() {

        int uiOptions = this.getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;

        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }
        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
        // Immersive mode: Backward compatible to KitKat.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        this.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)
    }

}
