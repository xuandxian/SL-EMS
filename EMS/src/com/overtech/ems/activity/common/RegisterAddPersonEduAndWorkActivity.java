package com.overtech.ems.activity.common;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class RegisterAddPersonEduAndWorkActivity extends BaseActivity {
	private TextView mHeadContent;
	private ImageView mHeadBack;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_add_person_edu_and_work);
		findViewById();
		init();
	}
	private void init() {
		mHeadContent.setText("基本信息");
		mHeadBack.setVisibility(View.VISIBLE);
	}

	private void findViewById() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
	}
	
	

}
