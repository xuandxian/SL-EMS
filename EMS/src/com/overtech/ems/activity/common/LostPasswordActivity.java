package com.overtech.ems.activity.common;

import static cn.smssdk.framework.utils.R.getStringRes;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.CustomProgressDialog;
import com.overtech.ems.widget.EditTextWithDelete;
import com.overtech.ems.widget.TimeButton;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class LostPasswordActivity extends BaseActivity {
	private String mPhoneNo;
	private String mSMSCode;
	private EditTextWithDelete mPhoneNoEditText;
	private EditTextWithDelete mSMSCodeEditText;
	private TextView mHeadContent;
	private ImageView mHeadBack;
	private Button mLostPassword;
	private TimeButton mGetValicateCode;
	private EventHandler eh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lost_password);
		findViewById();
		init();
		mLostPassword.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mSMSCode=mSMSCodeEditText.getText().toString().trim();
				if (TextUtils.isEmpty(mSMSCode)) {
					Utilities.showToast("输入不能为空", context);
				}else {
					SMSSDK.submitVerificationCode("86", mPhoneNo, mSMSCode);
					startProgressDialog("正在验证...");
				}
			}
		});
		mGetValicateCode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mPhoneNo=mPhoneNoEditText.getText().toString().trim();
				if (Utilities.isMobileNO(mPhoneNo)) {
					mGetValicateCode.setTextAfter("秒后重新获取").setTextBefore("点击获取验证码").setLenght(60 * 1000);
					SMSSDK.getVerificationCode("86", mPhoneNo);
				}else {
					Utilities.showToast("请输入正确的手机号", context);
				}
			}
		});
	}
	private void findViewById() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
		mPhoneNoEditText=(EditTextWithDelete)findViewById(R.id.et_lost_password_phone);
		mSMSCodeEditText=(EditTextWithDelete)findViewById(R.id.et_valicate_code);
		mGetValicateCode=(TimeButton)findViewById(R.id.btn_get_valicate_code);
		mLostPassword=(Button)findViewById(R.id.btn_lost_password);
	}

	private void init() {
		mHeadContent.setText("密码重置");
		mHeadBack.setVisibility(View.VISIBLE);

	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int event = msg.arg1;
			int result = msg.arg2;
			Object data = msg.obj;
			if (result == SMSSDK.RESULT_COMPLETE) {
                 if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                	Utilities.showToast("验证码已发送", context);
                 }else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
					stopProgressDialog();
					Intent intent=new Intent(LostPasswordActivity.this, ResetPasswordActivity.class);
					startActivity(intent);
				 }
			} else {
				((Throwable) data).printStackTrace();
				int resId = getStringRes(LostPasswordActivity.this, "smssdk_network_error");
				if (resId > 0) {
					Utilities.showToast("错误码："+resId, context);
				}else {
					stopProgressDialog();
				}
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SMSSDK.unregisterEventHandler(eh);
	}
}
