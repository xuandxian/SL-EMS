package com.overtech.ems.activity.parttime.personal;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;

public class PersonalAboutAppActivity extends BaseActivity {
	private TextView mHead;
	private ImageView mDoBack;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_about_app);
		findViewById();
		init();
	}

	private void init() {
		mHead.setText("关于APP");
		mDoBack.setVisibility(View.VISIBLE);
		mDoBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void findViewById() {
		mHead=(TextView) findViewById(R.id.tv_headTitle);
		mDoBack=(ImageView) findViewById(R.id.iv_headBack);
	}
}
