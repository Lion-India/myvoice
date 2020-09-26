package app.com.worldofwealth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class PrivacyPolicyActivity extends AppCompatActivity {
    String weburl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.privacy);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if (b != null) {
            weburl = b.getString("weburl");
        }
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(weburl);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return true;
            }
        });
        //privacy = (TextView) findViewById(R.id.privacy);
        //GetDatafromServer();
    }

    public boolean onSupportNavigateUp() {
        startActivity(new Intent(this,LoginActivity.class));
        return true;
    }

}