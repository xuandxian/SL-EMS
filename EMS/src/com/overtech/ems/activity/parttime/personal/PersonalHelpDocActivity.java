package com.overtech.ems.activity.parttime.personal;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.adapter.PersonalHelpDocAdapter;

public class PersonalHelpDocActivity extends BaseActivity {
	private ImageView mDoBack;
	private TextView mHeadTitle;
	private ListView mHelpDoc;
	private Context context;
	private PersonalHelpDocAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_helpdoc);
		context=this;
		initView();
		init();
	}

	private void init() {
		mDoBack.setVisibility(View.VISIBLE);
		mDoBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mHeadTitle.setText("帮助文档");
		adapter=new PersonalHelpDocAdapter(context);
		mHelpDoc.setAdapter(adapter);
	}

	private void initView() {
		mDoBack=(ImageView) findViewById(R.id.iv_headBack);
		mHeadTitle=(TextView) findViewById(R.id.tv_headTitle);
		mHelpDoc=(ListView) findViewById(R.id.lv_help_doc);
	}
}
