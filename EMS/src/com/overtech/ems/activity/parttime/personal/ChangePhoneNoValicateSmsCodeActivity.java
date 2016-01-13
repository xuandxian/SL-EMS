package com.overtech.ems.activity.parttime.personal;

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

public class ChangePhoneNoValicateSmsCodeActivity extends BaseActivity implements OnClickListener {
	private TextView mHeadContent;
	private ImageView mDoBack;
	private Button mNextContent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_change_phoneno_in_vc);
		initView();
		initEvent();
	}

	private void initEvent() {
		mDoBack.setOnClickListener(this);
		mNextContent.setOnClickListener(this);
	}

	private void initView() {
		mHeadContent=(TextView) findViewById(R.id.tv_headTitle);
		mDoBack=(ImageView) findViewById(R.id.iv_headBack);
		mNextContent=(Button) findViewById(R.id.btn_next);
		mHeadContent.setText("输入验证码");
		mDoBack.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_headBack:
			finish();
			break;
		case R.id.btn_next:
			Intent intent=new Intent(this,ChangePhoneNoSuccessActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
}
