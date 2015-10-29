package com.overtech.ems.activity.parttime.common;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;

public class ElevatorDetailActivity extends Activity {

	private ImageView mGoBack;
	private TextView mHeadContent;
	private Context mActivity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_elevator_detail);
		findViewById();
		init();
	}

	private void findViewById() {
		mGoBack = (ImageView) findViewById(R.id.iv_headBack);
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
	}

	private void init() {
		mActivity=ElevatorDetailActivity.this;
		mGoBack.setVisibility(View.VISIBLE);
		mHeadContent.setText("电梯详情");
		mGoBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
}
