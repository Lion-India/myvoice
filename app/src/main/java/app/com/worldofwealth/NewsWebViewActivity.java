package app.com.worldofwealth;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import app.com.worldofwealth.models.Post;

public class NewsWebViewActivity extends AppCompatActivity {
    WebView webView;
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(this,MainActivity.class));
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_web_view);
        Post post= (Post) getIntent().getSerializableExtra(DescriptionActivity.KEY);
        WebView webView = (WebView) findViewById(R.id.webView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(post.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(post.getSourceurl());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return true;
            }
        });
    }



}
