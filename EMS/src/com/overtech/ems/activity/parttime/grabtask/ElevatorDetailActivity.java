package com.overtech.ems.activity.parttime.grabtask;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.overtech.ems.R;

public class ElevatorDetailActivity extends Activity {

	private ImageView mGoBack;
	private TextView mHeadContent;

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
		mGoBack.setVisibility(View.VISIBLE);
		mHeadContent.setText("电梯详情");
	}
}
