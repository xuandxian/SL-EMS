package com.overtech.ems;

import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.view.View;
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		findViewById();
		init();
	}

	private void findViewById() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack=(ImageView)findViewById(R.id.iv_headBack);
	}
	private void init() {
		mHeadContent.setText("登 录");
		mHeadBack.setVisibility(View.VISIBLE);
	}
}
