package com.overtech.ems;

import com.overtech.view.ValicateCode;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class RegisterActivity extends BaseActivity {
	private TextView mHeadContent;
	private ImageView mHeadBack;
	private ImageView mValicateCode;
	private ValicateCode instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		findViewById();
		init();
	}

	private void findViewById() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
		mValicateCode = (ImageView) findViewById(R.id.iv_register_valicate_code);
	}

	private void init() {
		mHeadContent.setText("注 册");
		mHeadBack.setVisibility(View.VISIBLE);
		instance = ValicateCode.getInstance();
		mValicateCode.setImageBitmap(instance.createBitmap());
	}
}
