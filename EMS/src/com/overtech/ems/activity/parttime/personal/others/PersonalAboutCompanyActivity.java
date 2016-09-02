package com.overtech.ems.activity.parttime.personal.others;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;

public class PersonalAboutCompanyActivity extends BaseActivity {
	private TextView mHeadContent;
	private ImageView mDoBack;

	@Override
	protected int getLayoutResIds() {
		// TODO Auto-generated method stub
		return R.layout.activity_personal_aboutcompany;
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mDoBack = (ImageView) findViewById(R.id.iv_headBack);
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadContent.setText("关于我们");
		mDoBack.setVisibility(View.VISIBLE);
		mDoBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
}