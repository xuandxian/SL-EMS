package com.overtech.ems.activity.common.register;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.http.HttpEngine.Param;
import com.overtech.ems.http.constant.Constant;
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
	private EditText mEtValidateCode;
	private String validateCode;
	private TextView mHeadTitle;
	private ImageView mDoBack;
	private Button mNext;
	public String mPhoneNo;
	private RegFraBtnClickListener listener;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.SUBMIT_PHONENO_SUCCESS:
				String json = (String) msg.obj;
				try {
					JSONObject jsonObj = new JSONObject(json);
					String model = jsonObj.getString("model");
					if (model.equals("0")) {
						Utilities.showToast("手机号被占用", context);
					} else if (model.equals("1")) {
						Utilities.showToast("验证码发送成功", context);
					} else if (model.equals("2")) {
						Utilities.showToast("验证码发送失败", context);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case StatusCode.COMMOM_SUBMIT_SMS_CODE:
				String result = (String) msg.obj;
				try {
					JSONObject jsonObject2 = new JSONObject(result);
					String model = jsonObject2.getString("model");
					if (model.equals("3")) {
						Utilities.showToast("验证成功", context);
						if (listener != null) {
							listener.onRegFraBtnClick();
						}
					} else if (model.equals("4")) {
						Utilities.showToast("验证失败", context);
					} else if (model.equals("5")) {
						Utilities.showToast("验证码失效", context);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast("服务器异常", mContext);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast("网络异常", mContext);
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
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				if(!TextUtils.isEmpty(arg0)&&Utilities.isMobileNO(arg0.toString())){
					mGetValidate.setEnabled(true);
				}else{
					mGetValidate.setEnabled(false);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		mGetValidate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPhoneNo = mRegisterPhone.getText().toString().trim();
				if (!TextUtils.isEmpty(mPhoneNo)&& Utilities.isMobileNO(mPhoneNo)) {
					Param param = new Param(Constant.PHONENO, mPhoneNo);
					Param flag = new Param(Constant.FLAG, "1");// 告诉服务器需要验证该手机是否已经注册
					Request request = httpEngine.createRequest(ServicesConfig.COMMON_GET_SMS_CODE, param, flag);
					Call call = httpEngine.createRequestCall(request);
					call.enqueue(new Callback() {

						@Override
						public void onResponse(Response response)throws IOException {
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
		});
		mNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
					validateCode = mEtValidateCode.getText().toString().trim();
					if (TextUtils.isEmpty(validateCode)) {
						Utilities.showToast("验证码不能为空", mContext);
					} else {
						Param param = new Param(Constant.PHONENO, mPhoneNo);
						Param smsCode = new Param(Constant.SMSCODE,validateCode);
						Request request = httpEngine.createRequest(ServicesConfig.COMMON_VARLICATE_SMS_CODE,param, smsCode);
						Call call = httpEngine.createRequestCall(request);
						call.enqueue(new Callback() {

							@Override
							public void onResponse(Response response)throws IOException {
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
							public void onFailure(Request request,IOException ioe) {
								Message msg = new Message();
								msg.what = StatusCode.RESPONSE_NET_FAILED;
								handler.sendMessage(msg);
							}
						});
					}
			}
		});
	}

	private void findViewById(View view) {
		mHeadTitle = (TextView) view.findViewById(R.id.tv_headTitle);
		mDoBack = (ImageView) view.findViewById(R.id.iv_headBack);
		mNext = (Button) view.findViewById(R.id.btn_next_fragment);
		mRegisterPhone = (EditTextWithDelete) view.findViewById(R.id.et_register_phone);
		mGetValidate = (TimeButton) view.findViewById(R.id.btn_get_valicate_code);
		mEtValidateCode = (EditText) view.findViewById(R.id.et_valicate_code);
	}

	public void setRegFraBtnClickListener(RegFraBtnClickListener listener) {
		this.listener = listener;
	}

	public interface RegFraBtnClickListener {
		void onRegFraBtnClick();
	}
}
