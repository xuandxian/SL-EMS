package com.overtech.ems.activity.common;

import com.overtech.ems.R;
import com.overtech.ems.R.drawable;
import com.overtech.ems.R.id;
import com.overtech.ems.R.layout;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.utils.Utilities;
import com.overtech.views.EditTextWithDelete;
import com.overtech.views.ValicateCode;

import android.R.bool;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class RegisterActivity extends BaseActivity {
	private Context mContext;
	private TextView mHeadContent;
	private ImageView mHeadBack;
	private ImageView mValicateCodeImage;
	private ValicateCode instance;
	private Button mGetValicateCode;
	private EditTextWithDelete mRegisterPhone;
	private Button mGetMessageCode;
	private EditText mValicateCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		findViewById();
		init();
		mGetValicateCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mValicateCodeImage.setImageBitmap(instance.createBitmap());
			}
		});
		mGetMessageCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String phoneNo = mRegisterPhone.getText().toString().trim();
				String valicateCode = mValicateCode.getText().toString().trim();
				boolean isCorrectCode = instance.getCode().equalsIgnoreCase(
						valicateCode);
				if (TextUtils.isEmpty(phoneNo)
						|| TextUtils.isEmpty(valicateCode)) {
					Utilities.showToast("输入不能为空", mContext);
				} else {
					if (Utilities.isMobileNO(phoneNo)) {
						if (isCorrectCode) {
							// 网络部分逻辑应该添加在此处，目前直接跳轉到下一個界面
							Intent intent = new Intent(RegisterActivity.this,
									RegisterAddPersonInfoActivity.class);
							startActivity(intent);
						} else {
							Utilities.showToast("验证码输入错误", mContext);
						}
					} else {
						Utilities.showToast("手机号码输入不正确", mContext);
					}
				}
			}
		});
		mRegisterPhone.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				boolean isPhoneNo = Utilities.isMobileNO(s.toString().trim());
				if (isPhoneNo) {
					mGetMessageCode
							.setBackgroundResource(R.drawable.btn_action_common_rest);
					mGetMessageCode.setClickable(true);
				} else {
					mGetMessageCode
							.setBackgroundResource(R.drawable.btn_action_commen_disable);
					mGetMessageCode.setClickable(false);
				}
			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});
	}

	private void findViewById() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
		mValicateCodeImage = (ImageView) findViewById(R.id.iv_register_valicate_code);
		mGetValicateCode = (Button) findViewById(R.id.btn_get_valicate_code);
		mGetMessageCode = (Button) findViewById(R.id.btn_get_message_code);
		mRegisterPhone = (EditTextWithDelete) findViewById(R.id.et_register_phone);
		mValicateCode = (EditText) findViewById(R.id.et_valicate_code);
	}

	private void init() {
		mContext = RegisterActivity.this;
		mHeadContent.setText("注 册");
		mHeadBack.setVisibility(View.VISIBLE);
		instance = ValicateCode.getInstance();
		mValicateCodeImage.setImageBitmap(instance.createBitmap());
	}
}
