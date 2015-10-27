package com.overtech.ems.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;

public class RegisterAddIdCardActivity extends Activity {
	private TextView mHeadContent;
	private ImageView mHeadBack;
	private Button mNext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register_add_id_card);
		findViewById();
		init();
	}

	private void init() {
		mHeadContent.setText("身份证确认");
		mHeadBack.setVisibility(View.VISIBLE);
		mNext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RegisterAddIdCardActivity.this,RegisterAddWorkCertificateActivity.class);
				startActivity(intent);
			}
		});
	}

	private void findViewById() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
		mNext=(Button) findViewById(R.id.btn_register_next_work);
	}
}
