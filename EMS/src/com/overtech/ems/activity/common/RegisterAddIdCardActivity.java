package com.overtech.ems.activity.common;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.overtech.ems.R;

public class RegisterAddIdCardActivity extends Activity {
	private TextView mHeadContent;
	private ImageView mHeadBack;

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
	}

	private void findViewById() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
	}
}
