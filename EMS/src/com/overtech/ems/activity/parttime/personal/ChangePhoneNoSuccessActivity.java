package com.overtech.ems.activity.parttime.personal;

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
 *@author Tony
 *@date change on 2016-01-13
 *
 */
public class ChangePhoneNoSuccessActivity extends BaseActivity {
	private TextView mHeadContent;
	private ImageView mDoBack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_phoneno_success);
		initView();
	}

	private void initView() {
		mHeadContent=(TextView) findViewById(R.id.tv_headTitle);
		mDoBack=(ImageView) findViewById(R.id.iv_headBack);
		mHeadContent.setText("更换成功");
		mDoBack.setVisibility(View.VISIBLE);
		startProgressDialog("2秒后跳转到登录");
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				stopProgressDialog();
				Intent intent = new Intent(ChangePhoneNoSuccessActivity.this,LoginActivity.class);
				startActivity(intent);
				finish();
			}
		}, 2000);
	}
}
