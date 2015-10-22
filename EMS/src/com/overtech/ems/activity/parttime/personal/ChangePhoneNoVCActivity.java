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
import com.overtech.ems.widget.EditTextWithDelete;

public class ChangePhoneNoVCActivity extends BaseActivity implements OnClickListener {
	private TextView mHeadContent;
	private ImageView mDoBack;
	private Button mNextContent;
	private String phone;
	private EditTextWithDelete mPhone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_change_phoneno_vc);
		phone=getIntent().getStringExtra("phone");
		initView();
		initEvent();
	}

	private void initEvent() {
		mDoBack.setVisibility(View.VISIBLE);
		mDoBack.setOnClickListener(this);
		mNextContent.setOnClickListener(this);
	}

	private void initView() {
		mHeadContent=(TextView) findViewById(R.id.tv_headTitle);
		mHeadContent.setText("验证密码");
		mDoBack=(ImageView) findViewById(R.id.iv_headBack);
		mNextContent=(Button) findViewById(R.id.btn_next);
		mPhone=(EditTextWithDelete) findViewById(R.id.et_phone);
		mPhone.setHint(phone);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_headBack:
			finish();
			break;
		case R.id.btn_next:
			Intent intent=new Intent(this,ChangePhoneNoInVCActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
}
