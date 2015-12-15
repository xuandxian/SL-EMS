package com.overtech.ems.activity.parttime.personal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.R;
import com.overtech.ems.widget.webview.ProgressWebView;

public class PersonalAboutCompanyActivity extends BaseActivity {
	private TextView mHeadContent;
	private ImageView mDoBack;
	private WebView mWebView;
	private WebSettings mSetting;
	private ProgressBar mProgressBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_aboutcompany);
		initView();
		initData();
	}

	private void initView() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mDoBack = (ImageView) findViewById(R.id.iv_headBack);
		mWebView = (ProgressWebView) findViewById(R.id.webview_company);
	}

	private void initData() {
		mHeadContent.setText("公司信息");
		mDoBack.setVisibility(View.VISIBLE);
		mDoBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		String url="http://www.relevator.cc";
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
				if (url != null && url.startsWith("http://"))
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			}
		});
		mWebView.loadUrl(url);
	}
}