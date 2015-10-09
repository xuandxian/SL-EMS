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

public class RegisterAddPersonEduAndWorkActivity extends BaseActivity {
	private TextView mHeadContent;
	private ImageView mHeadBack;
	private Button mGoIdCard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_add_person_edu_and_work);
		findViewById();
		init();
		mGoIdCard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(
						RegisterAddPersonEduAndWorkActivity.this,
						RegisterAddIdCardActivity.class);
				startActivity(intent);
			}
		});
	}

	private void init() {
		mHeadContent.setText("学历/工作信息");
		mHeadBack.setVisibility(View.VISIBLE);
	}

	private void findViewById() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
		mGoIdCard = (Button) findViewById(R.id.btn_add_id_card);
	}

}
