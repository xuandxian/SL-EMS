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

public class RegisterAddWorkCertificateActivity extends Activity {
	
	private ImageView mDoBack;
	private TextView mHeadTitle ;
	private Button mNext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register_add_work_certificate);
		findViewById();
		init();
	}

	private void init() {
		mHeadTitle.setText("上岗证确认");
		mDoBack.setVisibility(View.VISIBLE);
		mNext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(RegisterAddWorkCertificateActivity.this,RegisterAddOtherCertificateActivity.class);
				startActivity(intent);
			}
		});
	}

	private void findViewById() {
		mDoBack=(ImageView) findViewById(R.id.iv_headBack);
		mHeadTitle=(TextView) findViewById(R.id.tv_headTitle);
		mNext=(Button) findViewById(R.id.btn_register_next_work);
	}
}
