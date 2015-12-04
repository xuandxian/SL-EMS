package com.overtech.ems.activity.parttime.personal;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;

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
		init();
	}
	private void init() {
		mHeadContent.setText("公司主页");
		mDoBack.setVisibility(View.VISIBLE);
		mWebView.setWebChromeClient(new WebChromeClient(){
			
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				super.onProgressChanged(view, newProgress);
				if(newProgress==100){
					mProgressBar.setVisibility(View.GONE);
				}else{
					if(View.VISIBLE==mProgressBar.getVisibility()){
						mProgressBar.setVisibility(View.VISIBLE);
					}
					mProgressBar.setProgress(newProgress);
				}
			}
			
		});
		mWebView.loadUrl("http://www.relevator.cc");
		mSetting.setUseWideViewPort(true);
		mSetting.setBuiltInZoomControls(true);
		mSetting.setSupportZoom(true);
		mWebView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
	}
	private void initView() {
		mHeadContent=(TextView) findViewById(R.id.tv_headTitle);
		mDoBack=(ImageView) findViewById(R.id.iv_headBack);
		mWebView=(WebView) findViewById(R.id.wv_announcement);
		mProgressBar=(ProgressBar) findViewById(R.id.progressBar);
		mSetting=mWebView.getSettings();
	}
}
