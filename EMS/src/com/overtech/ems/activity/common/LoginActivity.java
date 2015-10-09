package com.overtech.ems.activity.common;

import com.overtech.ems.R;
import com.overtech.ems.R.id;
import com.overtech.ems.R.layout;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.MainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
	private Button mLogin;
	private TextView mRegister;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		findViewById();
		init();
		mLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				startActivity(intent);
			}
		});
		mLostPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(LoginActivity.this,
						LostPasswordActivity.class);
				startActivity(intent);
			}
		});
		mRegister.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(intent);
			}
		});
		
		mHeadBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				onBackPressed();			
			}
		});
	}

	private void findViewById() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
		mLostPassword = (TextView) findViewById(R.id.tv_lost_password);
		mRegister=(TextView)findViewById(R.id.tv_login_by_message);
		mLogin=(Button)findViewById(R.id.btn_login);
	}

	private void init() {
		mHeadContent.setText("登 录");
		mHeadBack.setVisibility(View.VISIBLE);
	}
}
