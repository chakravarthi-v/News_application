package com.example.newsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class view extends AppCompatActivity {
    WebView love;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        love=findViewById(R.id.web);
        love.getSettings().setJavaScriptEnabled(true);
        love.setWebViewClient(new WebViewClient());
        Intent gost=getIntent();
        love.loadUrl(gost.getStringExtra("content"));

    }
}