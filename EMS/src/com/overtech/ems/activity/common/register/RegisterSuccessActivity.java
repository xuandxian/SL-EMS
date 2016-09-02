package com.overtech.ems.activity.common.register;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.common.LoginActivity;

public class RegisterSuccessActivity extends BaseActivity {
	private ImageView mDoBack;
	private TextView mTitle;
	private RegisterSuccessActivity mActivity;

	@Override
	protected int getLayoutResIds() {
		// TODO Auto-generated method stub
		return R.layout.activity_register_success;
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mActivity = this;
		initView();
	}

	private void initView() {
		mDoBack = (ImageView) findViewById(R.id.iv_headBack);
		mTitle = (TextView) findViewById(R.id.tv_headTitle);
		mDoBack.setVisibility(View.GONE);
		mTitle.setText("注册成功");
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(RegisterSuccessActivity.this,
						LoginActivity.class);
				mActivity.startActivity(intent);
				finish();
			}
		}, 3000);
	}

}
