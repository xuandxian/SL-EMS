package com.overtech.ems;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Tony
 * @description 登录界面
 * @date 2015-10-05
 */
public class LoginActivity extends BaseActivity {
	private TextView mHeadContent;
	private ImageView mHeadBack;
	private TextView mLostPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		findViewById();
		init();
		mLostPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(LoginActivity.this,
						LostPasswordActivity.class);
				startActivity(intent);
			}
		});
	}

	private void findViewById() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
		mLostPassword = (TextView) findViewById(R.id.tv_lost_password);
	}

	private void init() {
		mHeadContent.setText("登 录");
		mHeadBack.setVisibility(View.VISIBLE);
	}
}
