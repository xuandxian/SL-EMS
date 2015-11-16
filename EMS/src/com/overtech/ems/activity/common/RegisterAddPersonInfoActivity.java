package com.overtech.ems.activity.common;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class RegisterAddPersonInfoActivity extends BaseActivity {
	private TextView mHeadContent;
	private ImageView mHeadBack;
	private Button mGoEduAndWork;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_add_person_info);
		findViewById();
		init();
		mGoEduAndWork.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(RegisterAddPersonInfoActivity.this,
						RegisterAddPersonEduAndWorkActivity.class);
				startActivity(intent);
			}
		});
	}

	private void init() {
		mHeadContent.setText("基本信息");
		mHeadBack.setVisibility(View.VISIBLE);
	}

	private void findViewById() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
		mGoEduAndWork = (Button) findViewById(R.id.btn_register_next_work);
	}

}
