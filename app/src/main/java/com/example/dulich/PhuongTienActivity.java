package com.example.dulich;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class PhuongTienActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phuong_tien);
        WebView webView = findViewById(R.id.webview);
        Intent i = getIntent();
        String phuongtien = i.getStringExtra("phuongtien");
        switch (phuongtien){
            case "bus":
                webView.loadUrl("https://vexere.com");
                break;
            case "plane":
                webView.loadUrl("https://www.bestprice.vn/ve-may-bay/");
                break;
            case "train":
                webView.loadUrl("http://vetautructuyen.vn/");
                break;
        }
    }
}
