package com.overtech.ems.activity.parttime.personal;

import com.overtech.ems.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class PersonalDeatilsActivity extends Activity implements OnClickListener {
	private TextView mHeadContent;
	private ImageView iv_headBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_personal_details);
		initViews();
		initEvents();
	}

	private void initViews() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		iv_headBack=(ImageView) findViewById(R.id.iv_headBack);
		mHeadContent.setText("账号信息");
	}

	private void initEvents() {
		//返回键 
		iv_headBack.setVisibility(View.VISIBLE);
		iv_headBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		this.finish();
	}
}
