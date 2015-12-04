package com.overtech.ems.activity.parttime.personal;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;

public class PersonalAppDeveloperActivity extends BaseActivity {
	private TextView mHead;
	private ImageView mDoBack;
	private WebView mDevelopers;
	private WebSettings mSetting;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_app_developers);
		initView();
		initData();
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
		mDevelopers.loadUrl("http://www.overtech.com.cn");
		mSetting.setJavaScriptEnabled(true);
		mDevelopers.setWebViewClient(new MyWebViewClient());
	}

	private void initView() {
		mHead=(TextView) findViewById(R.id.tv_headTitle);
		mDoBack=(ImageView) findViewById(R.id.iv_headBack);
		mDevelopers=(WebView) findViewById(R.id.wv_developers);
		mSetting=mDevelopers.getSettings();
	}
	private class MyWebViewClient extends WebViewClient{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			view.loadUrl(url);
			return true;
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK&&mDevelopers.canGoBack()){
			mDevelopers.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
