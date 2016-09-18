package com.overtech.ems.activity.parttime.personal.phoneno;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
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
	private Toolbar toolbar;
	private ActionBar actionBar;
	private AppCompatTextView tvTitle;
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
		toolbar = (Toolbar) findViewById(R.id.toolBar);
		setSupportActionBar(toolbar);
		actionBar = getSupportActionBar();
		tvTitle = (AppCompatTextView) findViewById(R.id.tvTitle);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		tvTitle.setText("更换成功");
		
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
