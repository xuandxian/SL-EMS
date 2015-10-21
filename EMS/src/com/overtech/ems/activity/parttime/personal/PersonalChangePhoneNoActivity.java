package com.overtech.ems.activity.parttime.personal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.EditTextWithDelete;

public class PersonalChangePhoneNoActivity extends Activity implements OnClickListener {
	private TextView mHeadContent;
	private ImageView mDoBack;
	private TextView mVerifyPassword;
	private TextView mVerification;
	private TextView mComplication;
	private EditTextWithDelete mPhone;
	private EditTextWithDelete mPassword;
	private Button mNextContent;
	private Button mNewVerification;
	private int currentState=1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_personal_change_phoneno);
		initView();
		initEvent();
		showState();
	}

	private void showState() {
		mVerifyPassword.setTextColor(Color.GREEN);
		mDoBack.setVisibility(View.VISIBLE);
	}

	private void initEvent() {
		mDoBack.setOnClickListener(this);
		mNextContent.setOnClickListener(this);
	}

	private void initView() {
		mHeadContent=(TextView) findViewById(R.id.tv_headTitle);
		mDoBack=(ImageView) findViewById(R.id.iv_headBack);
		mVerifyPassword=(TextView) findViewById(R.id.verify_password);
		mVerification=(TextView) findViewById(R.id.verification);
		mComplication=(TextView) findViewById(R.id.complication);
		mPhone=(EditTextWithDelete) findViewById(R.id.et_phone);
		mPassword=(EditTextWithDelete) findViewById(R.id.et_verificate_password);
		mNextContent=(Button) findViewById(R.id.btn_next);
		mNewVerification=(Button) findViewById(R.id.get_verification);
		mHeadContent.setText("验证密码");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_headBack:
			if(currentState==1){
				finish();
			}else if(currentState==2){
				currentState=1;
				mHeadContent.setText("验证密码");
				mVerifyPassword.setTextColor(Color.GREEN);
				mVerification.setTextColor(Color.GRAY);
				mPhone.setHint("150****5127");
				mPassword.setHint("请输入登录密码");
				mNewVerification.setVisibility(View.GONE);
			}else if(currentState==3){
				currentState=2;
				mHeadContent.setText("输入验证码");
				mVerification.setTextColor(Color.GREEN);
				mComplication.setTextColor(Color.GRAY);
			}
			break;
		case R.id.btn_next:
			if(currentState==1){
				currentState=2;
				mHeadContent.setText("输入验证码");
				mVerification.setTextColor(Color.GREEN);
				mVerifyPassword.setTextColor(Color.GRAY);
				mPhone.setHint("输入新手机号");
				mPassword.setHint("请输入验证码");
				mNewVerification.setVisibility(View.VISIBLE);
			}else if(currentState==2){
				currentState=3;
				
				mHeadContent.setText("更换成功");
				mVerification.setTextColor(Color.GRAY);
				mComplication.setTextColor(Color.GREEN);
				showDialog();
			}else if(currentState==3){
				Utilities.showToast("已经是最后一个页面了", this);
			}
			break;
		default:
			break;
		}
	}

	private void showDialog() {
		ProgressDialog mDialog=new ProgressDialog(this);
		mDialog.setMessage("信息更新中...");
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mDialog.show();
	}
}
