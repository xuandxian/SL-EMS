package com.overtech.ems.activity.parttime.personal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.widget.webview.ProgressWebView;
import android.net.Uri;

public class PersonalAboutDeveloperActivity extends BaseActivity {
    private TextView mHead;
    private ImageView mDoBack;
    private ProgressWebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_app_developers);
        initView();
        initData();
    }

    private void initView() {
        mHead = (TextView) findViewById(R.id.tv_headTitle);
        mDoBack = (ImageView) findViewById(R.id.iv_headBack);
        webview = (ProgressWebView) findViewById(R.id.webview_developer);
    }

    private void initData() {
        mHead.setText("APP开发团队");
        mDoBack.setVisibility(View.VISIBLE);
        mDoBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        String url="http://m.ly.com/?RefId=4140683";
//        String url="http://www.overtech.com.cn";
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                if (url != null && url.startsWith("http://"))
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });

        webview.loadUrl(url);
    }
}
