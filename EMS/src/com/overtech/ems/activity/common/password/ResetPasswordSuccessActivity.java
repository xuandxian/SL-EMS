package com.overtech.ems.activity.common.password;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.common.LoginActivity;

public class ResetPasswordSuccessActivity extends BaseActivity {
	private TextView mHeadContent;
	private ImageView mHeadBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		progressDialog.setMessage("正在重新登录...");
		progressDialog.show();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent=new Intent(ResetPasswordSuccessActivity.this,LoginActivity.class);
				startActivity(intent);
				progressDialog.dismiss();
				finish();
			}
		}, 2000);
	}
}
