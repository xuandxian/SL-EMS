package com.overtech.ems.activity.parttime.personal;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;

public class PersonalAnnouncementDetailActivity extends BaseActivity {
	private TextView mHeadContent;
	private ImageView mDoBack;
	private WebView mWebView;
	private WebSettings mSetting;
	private ProgressBar mProgressBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_announcement_detail);
		initView();
		init();
	}

	private void init() {
		mHeadContent.setText("详情");
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
		
	}

	private void initView() {
		mHeadContent=(TextView) findViewById(R.id.tv_headTitle);
		mDoBack=(ImageView) findViewById(R.id.iv_headBack);
		mWebView=(WebView) findViewById(R.id.wv_announcement);
		mProgressBar=(ProgressBar) findViewById(R.id.progressBar);
		mSetting=mWebView.getSettings();
	}
}
