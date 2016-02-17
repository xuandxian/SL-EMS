package com.overtech.ems.activity.common.password;

import static cn.smssdk.framework.utils.R.getStringRes;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;
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
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
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
				mSMSCode = mSMSCodeEditText.getText().toString().trim();
				if (TextUtils.isEmpty(mSMSCode)) {
					Utilities.showToast("输入不能为空", context);
				} else {
					SMSSDK.submitVerificationCode("86", mPhoneNo, mSMSCode);
					startProgressDialog("正在验证...");
				}
			}
		});
		mGetValicateCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				mPhoneNo = mPhoneNoEditText.getText().toString().trim();
				if (Utilities.isMobileNO(mPhoneNo)) {
					Param param = new Param(Constant.PHONENO, mPhoneNo);
					verifyPhoneNo(ServicesConfig.LOST_PASSWORD, param);
				} else {
					Utilities.showToast("请输入正确的手机号", context);
				}
			}
		});
	}

	private void findViewById() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
		mPhoneNoEditText = (EditTextWithDelete) findViewById(R.id.et_lost_password_phone);
		mSMSCodeEditText = (EditTextWithDelete) findViewById(R.id.et_valicate_code);
		mGetValicateCode = (TimeButton) findViewById(R.id.btn_get_valicate_code);
		mLostPassword = (Button) findViewById(R.id.btn_lost_password);
	}

	private void init() {
		mHeadContent.setText("密码重置");
		mHeadBack.setVisibility(View.VISIBLE);
		eh = new EventHandler() {
			@Override
			public void afterEvent(int event, int result, Object data) {

				Message msg = new Message();
				msg.what = 0x23;
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}
		};
		SMSSDK.registerEventHandler(eh);

	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0x23:
				int event = msg.arg1;
				int result = msg.arg2;
				Object data = msg.obj;
				if (result == SMSSDK.RESULT_COMPLETE) {
					if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
						Utilities.showToast("验证码已发送", context);
					} else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
						Intent intent = new Intent(LostPasswordActivity.this,ResetPasswordActivity.class);
						intent.putExtra("mPhoneNo", mPhoneNo);
						startActivity(intent);
					}
				} else {
					((Throwable) data).printStackTrace();
					int resId = getStringRes(LostPasswordActivity.this,"smssdk_network_error");
					if (resId > 0) {
						Utilities.showToast("错误码：" + resId, context);
					}
				}
				break;
			case StatusCode.GET_SERVER_SUCCESS://首先请求服务器对该手机号进行验证，根据结果确定要不要进行下一步的验证
				String json=(String)msg.obj;
				try {
					JSONObject jsonObj=new JSONObject(json);
					String model=jsonObj.getString("model");
					if(model.equals("0")){
						Utilities.showToast("该手机号尚未注册",context);
					}else if(model.equals("1")){
						Utilities.showToast("该手机号尚未通过审核,请等待结果", context);
					}else if(model.equals("2")){
						
						SMSSDK.getVerificationCode("86", mPhoneNo);//该手机号是在职的，然后调用第三方的短信验证
						
					}else if(model.equals("3")){
						Utilities.showToast("员工已经离职，不能使用该功能", context);
					}else{
						Utilities.showToast("该手机号已经被禁用", context);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast("网络异常", context);
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast("服务器异常", context);
				break;
			}
			stopProgressDialog();
		}
	};

	public void verifyPhoneNo(String url, Param... params) {
		startProgressDialog("正在验证...");
		Request request = httpEngine.createRequest(url, params);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					String result = response.body().string();
					msg.what=StatusCode.GET_SERVER_SUCCESS;
					msg.obj=result;
				} else {
					msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
				}
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(Request request, IOException e) {
				Message msg = new Message();
				msg.what = StatusCode.RESPONSE_NET_FAILED;
				handler.sendMessage(msg);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SMSSDK.unregisterEventHandler(eh);
	}
}
