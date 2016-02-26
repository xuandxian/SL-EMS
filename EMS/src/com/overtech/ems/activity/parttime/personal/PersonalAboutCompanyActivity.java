package com.overtech.ems.activity.parttime.personal;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.R;

public class PersonalAboutCompanyActivity extends BaseActivity {
	private TextView mHeadContent;
	private ImageView mDoBack;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_aboutcompany);
		mDoBack = (ImageView) findViewById(R.id.iv_headBack);
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadContent.setText("公司信息");
		mDoBack.setVisibility(View.VISIBLE);
		mDoBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
}