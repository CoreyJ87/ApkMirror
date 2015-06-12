package info.coreyjones.apkmirror;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


public class MainMenu extends ActionBarActivity {

    protected final String siteURL = "http://apkmirror.com";
    protected WebView theWebView;
    //protected boolean actionBarState = true;
    protected boolean fullScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        theWebView = setupWebView(siteURL, R.id.webView, savedInstanceState);
        initDownloadListener();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        theWebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        theWebView.restoreState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            initInstallMenu();
            return true;
        } else if (id == R.id.action_immersive) {
            toggleHideyBar();
            if (fullScreen) {
                toastMessage(getString(R.string.fullScreenOff));
                //showActionBar();
                fullScreen = !fullScreen;
            } else {
                toastMessage(getString(R.string.fullScreenOn));
                //hideActionBar();
                fullScreen = !fullScreen;
            }
        } else if (id == R.id.refresh_window) {
            refreshPage(R.id.webView);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebView myWebView = (WebView) findViewById(R.id.webView);
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //WebView Creator
    protected WebView setupWebView(String theURL, int TheViewID, Bundle savedInstanceState) {
        WebView theWebView = (WebView) findViewById(TheViewID);
        theWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                String title = String.format("%s - Loading...",getString(R.string.app_name));
                setTitle(title);
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                setProgress(progress * 100);

                if (progress == 100) {
                    setTitle(R.string.app_name);
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                }
            }
        });
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

    protected void initDownloadListener(){
        theWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));
                request.allowScanningByMediaScanner();
                request.setDescription(String.format("Downloading: %s",theWebView.getTitle()));
                request.setTitle(theWebView.getTitle());
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, theWebView.getTitle() + ".apk");
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
            }
        });
    }
    //Settings Menu Initialization
    protected void initInstallMenu() {
        Intent intent = new Intent(this, InstallMenu.class);
        startActivity(intent);
    }

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

}
