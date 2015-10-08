package com.overtech.ems;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ResetPasswordActivity extends BaseActivity {
	private TextView mHeadContent;
	private ImageView mHeadBack;
	private Button mResetPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_password);
		findViewById();
		init();
		mResetPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(ResetPasswordActivity.this,
						ResetPasswordSuccessActivity.class);
				startActivity(intent);
			}
		});
	}

	private void findViewById() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
		mResetPassword = (Button) findViewById(R.id.btn_reset_password);
	}

	private void init() {
		mHeadContent.setText("密码重置");
		mHeadBack.setVisibility(View.VISIBLE);
	}
}
