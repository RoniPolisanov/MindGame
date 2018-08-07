package elbazpolisanovleib.com.mindgame;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.google.firebase.analytics.FirebaseAnalytics;

public class WebViewActivity extends AppCompatActivity {

    //Firebase Analytics object
    private FirebaseAnalytics mFirebaseAnalytics;

    // Webview Objects
    WebView myWebView;

    //ImageView that will be shown when internet won't work
    ImageView myImage;

    //Constraint Layout to show the snackbar
    ConstraintLayout myLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        // Binding of webview with xml webview
        myWebView = findViewById(R.id.myWebView);

        // binding of imageview with xml imageview
        myImage = findViewById(R.id.myImage);

        // constraint layout binding with xml view
        myLayout = findViewById(R.id.myLayout);

        // initialization of firebaseanalytics object
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Making the bundle object to track firebase analytics
        Bundle bundle = new Bundle();
        // Loging the firebase analytics event
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Tutorials Activity Opened!");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);

        // Setting the webview properties
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setSaveFormData(true);
        myWebView.getSettings().setSavePassword(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.getSettings().setUseWideViewPort(true);
        myWebView.getSettings().setAppCacheEnabled(true);
        myWebView.getSettings().setLoadsImagesAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            myWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }



        // Webview will be only shown when internet is working, otherwise it won't be shown.
        // when internet is working, show the webview, otherwise show the image and snackbar.
        if (!haveNetworkConnection())
        {
            myWebView.setVisibility(View.GONE);
            myImage.setVisibility(View.VISIBLE);
            Snackbar.make(myLayout, "Can't Reach to elbazpolisanovleib network.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("CLOSE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    })
                    .setActionTextColor(getResources().getColor(R.color.pink ))
                    .show();
        }
        else {
            myWebView.loadUrl("https://yyyroni.000webhostapp.com#tutorial");
        }
    }

    // When the app will be in background the webview will be paused
    @Override
    protected void onPause() {
        myWebView.onPause();
        myWebView.pauseTimers();
        super.onPause();

    }

    // when the app is brought to foreground, the webview is resumed again
    @Override
    protected void onResume() {
        myWebView.resumeTimers();
        myWebView.onResume();
        super.onResume();

    }

    // when the activity is destroyed destroy the webview as well.
    @Override
    protected void onDestroy() {
        myWebView.destroy();
        myWebView = null;
        super.onDestroy();
    }

    //Function to check if the internet is working or not
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
