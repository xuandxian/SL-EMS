package com.overtech.ems.activity.parttime.personal;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
/**
 * 公告栏
 * 
 */
public class PersonalAnnouncementActivity extends BaseActivity {
	private ImageView mDoBack;
	private	TextView mHeadContent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_announcement);
		findViewById();
		init();
	}


	private void findViewById() {
		mHeadContent=(TextView) findViewById(R.id.tv_headTitle);
		mDoBack=(ImageView) findViewById(R.id.iv_headBack);
	}
	
	private void init() {
		mHeadContent.setText("公告");
		mDoBack.setVisibility(View.VISIBLE);
		mDoBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
