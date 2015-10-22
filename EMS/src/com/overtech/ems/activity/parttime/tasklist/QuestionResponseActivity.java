package com.overtech.ems.activity.parttime.tasklist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;

public class QuestionResponseActivity extends BaseActivity {
	private TextView mHeadContent;
	private ImageView mDoBack;
	private Button mConfirm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_question_response);
		init();
		initEvent();
	}

	private void initEvent() {
		mConfirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(QuestionResponseActivity.this,EvaluationActivity.class);
				startActivity(intent);
			}
		});
	}

	private void init() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mDoBack = (ImageView) findViewById(R.id.iv_headBack);
		mConfirm=(Button) findViewById(R.id.bt_confirm);
		mHeadContent.setText("问题反馈");
		mDoBack.setVisibility(View.VISIBLE);
	}
}
