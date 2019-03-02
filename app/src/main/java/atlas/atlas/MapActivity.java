package atlas.atlas;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MapActivity extends AppCompatActivity {

    private static final String TAG = "Atlas"+MapActivity.class.getSimpleName();
    WebView mWebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_map);

        Intent intent = getIntent();
        if (intent != null) {
            String TrackerID = intent.getStringExtra("TrackerID");
            Double Latitude =  intent.getDoubleExtra("Latitude", 0);
            Double Longitude = intent.getDoubleExtra("Longitude", 0);
            Double AndroidLatitude = intent.getDoubleExtra("AndroidLatitude", 0);
            Double AndroidLongitude = intent.getDoubleExtra("AndroidLongitude", 0);

            mWebview  = new WebView(this);
            mWebview.getSettings().setJavaScriptEnabled(true); // enable javascript
            final Activity activity = this;
            mWebview.setWebViewClient(new WebViewClient() {
                @SuppressWarnings("deprecation")
                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                }
                @TargetApi(android.os.Build.VERSION_CODES.M)
                @Override
                public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                    // Redirect to deprecated method, so you can use it in all SDK versions
                    onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
                }
            });

            mWebview.loadUrl(String.format("http://maps.google.com/maps?z=15&q=loc:%3.6f,%3.6f", Latitude, Longitude));
            setContentView(mWebview );
        }
    }
}
