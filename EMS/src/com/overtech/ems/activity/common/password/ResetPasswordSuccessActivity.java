package com.overtech.ems.activity.common.password;

import com.overtech.ems.R;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.widget.CustomProgressDialog;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class ResetPasswordSuccessActivity extends Activity {
	private TextView mHeadContent;
	private ImageView mHeadBack;
	private CustomProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_reset_password_success);
		findViewById();
		init();
	}

	private void findViewById() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
	}

	private void init() {
		mHeadContent.setText("密码重置");
		mHeadBack.setVisibility(View.VISIBLE);
		progressDialog = CustomProgressDialog.createDialog(this);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				progressDialog.setMessage("正在重新登录...");
				progressDialog.show();
				Intent intent=new Intent(ResetPasswordSuccessActivity.this,LoginActivity.class);
				startActivity(intent);
				finish();
			}
		}, 2000);
	}
}
