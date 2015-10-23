package com.overtech.ems.activity.common;

import com.overtech.ems.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class ResetPasswordSuccessActivity extends Activity {
	private TextView mHeadContent;
	private ImageView mHeadBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_reset_password_success);
		findViewById();
		init();
		mHeadBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	private void findViewById() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
	}

	private void init() {
		mHeadContent.setText("密码重置");
		mHeadBack.setVisibility(View.VISIBLE);
	}
}
