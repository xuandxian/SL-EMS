package com.overtech.ems;

import android.os.Bundle;
import android.widget.TextView;
/**
 * @author Tony 
 * @description 登录界面
 * @date 2015-10-05 
 */
public class LoginActivity extends BaseActivity {
	private TextView mHeadContent;
	private TextView mRegister;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		findViewById();
		init();
	}

	private void findViewById() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mRegister = (TextView) findViewById(R.id.tv_headTitleRight);
	}
	private void init() {
		mHeadContent.setText("登 录");
		mRegister.setText("注册");
	}
}
