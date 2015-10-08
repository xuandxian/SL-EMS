package com.overtech.ems;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class RegisterAddPersonInfoActivity extends BaseActivity {
	private TextView mHeadContent;
	private ImageView mHeadBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_add_person_info);
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
