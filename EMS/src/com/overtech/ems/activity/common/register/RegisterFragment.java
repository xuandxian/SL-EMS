package com.overtech.ems.activity.common.register;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.bean.CommonBean;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.EditTextWithDelete;
import com.overtech.ems.widget.TimeButton;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class RegisterFragment extends BaseFragment {
	private Context mContext;
	private View view;
	private TimeButton mGetValidate;
	private EditTextWithDelete mRegisterPhone;
	private EditTextWithDelete mEtValidateCode;
	private String validateCode;
	private TextView mHeadTitle;
	private ImageView mDoBack;
	private Button mNext;
	public String mPhoneNo;
	private RegFraBtnClickListener listener;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			stopProgressDialog();
			switch (msg.what) {
			case StatusCode.SUBMIT_PHONENO_SUCCESS:
				String json = (String) msg.obj;
				Logr.e(TAG + json);
				CommonBean bean = gson.fromJson(json, CommonBean.class);
				Utilities.showToast(bean.msg, activity);
				break;
			case StatusCode.COMMOM_SUBMIT_SMS_CODE:
				String result = (String) msg.obj;
				Logr.e(result);
				CommonBean validateBean = gson.fromJson(result,
						CommonBean.class);
				int validateSt = validateBean.st;
				if (validateSt == 100) {
					Utilities.showToast(validateBean.msg, activity);
					if (listener != null) {
						listener.onRegFraBtnClick();
					}
				} else if (validateSt == 101) {
					Utilities.showToast(validateBean.msg, activity);
				} else if (validateSt == 102) {
					Utilities.showToast(validateBean.msg, activity);
				}
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast(R.string.response_failure_msg, mContext);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast(R.string.request_error_msg, mContext);
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_register, null);
		findViewById(view);
		init();
		return view;
	}

	private void init() {
		mHeadTitle.setText("注册");
		mDoBack.setVisibility(View.VISIBLE);
		mDoBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().onBackPressed();
			}
		});
		mRegisterPhone.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if (!TextUtils.isEmpty(arg0)
						&& Utilities.isMobileNO(arg0.toString())) {
					mGetValidate.setEnabled(true);
				} else {
					mGetValidate.setEnabled(false);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});
		mGetValidate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getSmsCode();
			}
		});
		mNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				submitSmsCode();
			}
		});
	}

	protected void getSmsCode() {
		// TODO Auto-generated method stub
		mPhoneNo = mRegisterPhone.getText().toString().trim();
		if (!TextUtils.isEmpty(mPhoneNo) && Utilities.isMobileNO(mPhoneNo)) {
			startProgressDialog("获取中...");
			Requester requester = new Requester();
			requester.cmd = 10;
			requester.body.put(Constant.FLAG, "0");// 当手机号没有注册过时发送验证码
			requester.body.put("phone", mPhoneNo);

			Request request = httpEngine.createRequest(SystemConfig.NEWIP,
					gson.toJson(requester));
			Call call = httpEngine.createRequestCall(request);
			call.enqueue(new Callback() {

				@Override
				public void onResponse(Response response) throws IOException {
					Message msg = new Message();
					if (response.isSuccessful()) {
						msg.what = StatusCode.SUBMIT_PHONENO_SUCCESS;
						msg.obj = response.body().string();
					} else {
						msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
					}
					handler.sendMessage(msg);
				}

				@Override
				public void onFailure(Request request, IOException ioe) {
					Message msg = new Message();
					msg.what = StatusCode.RESPONSE_NET_FAILED;
					handler.sendMessage(msg);
				}
			});
		} else {
			Utilities.showToast("请输入正确的手机号", mContext);
		}
	}

	protected void submitSmsCode() {
		validateCode = mEtValidateCode.getText().toString().trim();
		if (TextUtils.isEmpty(validateCode)) {
			Utilities.showToast("验证码不能为空", mContext);
		} else {
			startProgressDialog("提交中...");
			Requester requester = new Requester();
			requester.cmd = 11;
			requester.body.put(Constant.SMSCODE, validateCode);
			requester.body.put(Constant.PHONENO, mPhoneNo);

			Request request = httpEngine.createRequest(SystemConfig.NEWIP,
					gson.toJson(requester));
			Call call = httpEngine.createRequestCall(request);
			call.enqueue(new Callback() {

				@Override
				public void onResponse(Response response) throws IOException {
					Message msg = new Message();
					if (response.isSuccessful()) {
						msg.what = StatusCode.COMMOM_SUBMIT_SMS_CODE;
						msg.obj = response.body().string();
					} else {
						msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
					}
					handler.sendMessage(msg);

				}

				@Override
				public void onFailure(Request request, IOException ioe) {
					Message msg = new Message();
					msg.what = StatusCode.RESPONSE_NET_FAILED;
					handler.sendMessage(msg);
				}
			});
		}
	}

	private void findViewById(View view) {
		mHeadTitle = (TextView) view.findViewById(R.id.tv_headTitle);
		mDoBack = (ImageView) view.findViewById(R.id.iv_headBack);
		mNext = (Button) view.findViewById(R.id.btn_next_fragment);
		mRegisterPhone = (EditTextWithDelete) view
				.findViewById(R.id.et_register_phone);
		mGetValidate = (TimeButton) view
				.findViewById(R.id.btn_get_valicate_code);
		mEtValidateCode = (EditTextWithDelete) view
				.findViewById(R.id.et_valicate_code);
	}

	public void setRegFraBtnClickListener(RegFraBtnClickListener listener) {
		this.listener = listener;
	}

	public interface RegFraBtnClickListener {
		void onRegFraBtnClick();
	}
}
