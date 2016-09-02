package com.overtech.ems.activity.parttime.personal.phoneno;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.common.LoginActivity;

/*
 *修改手机号功能（更新成功）
 *@author Will
 *@date change on 2016-06-17
 *
 */
public class ChangePhoneNoSuccessActivity extends BaseActivity {
	private TextView mHeadContent;
	private ImageView mDoBack;
	private ChangePhoneNoSuccessActivity activity;

	@Override
	protected int getLayoutResIds() {
		// TODO Auto-generated method stub
		return R.layout.activity_change_phoneno_success;
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		activity = this;
		initView();
	}

	private void initView() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mDoBack = (ImageView) findViewById(R.id.iv_headBack);
		mHeadContent.setText("更换成功");
		mDoBack.setVisibility(View.VISIBLE);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				startProgressDialog("跳转中...");
				Intent intent = new Intent(ChangePhoneNoSuccessActivity.this,
						LoginActivity.class);
				startActivity(intent);
				finish();
			}
		}, 2000);
		stopProgressDialog();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
	}

}
