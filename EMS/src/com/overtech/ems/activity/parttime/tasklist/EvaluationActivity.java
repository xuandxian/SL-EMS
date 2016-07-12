package com.overtech.ems.activity.parttime.tasklist;

import java.io.IOException;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.bean.CommonBean;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class EvaluationActivity extends BaseActivity implements OnClickListener {
	private TextView mHeadContent;
	private ImageView mDoBack;
	private Button mConfirm;
	private RadioGroup mChecked;
	private EditText mContent;
	private String uid;
	private String certificate;
	private String evaluateLevel;
	private String evaluateInfo;
	private String taskNo;
	private EvaluationActivity activity;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			stopProgressDialog();
			switch (msg.what) {
			case StatusCode.EVALUATEOTHER:
				String json = (String) msg.obj;
				Logr.e(json);
				CommonBean bean = gson.fromJson(json, CommonBean.class);
				if (bean == null) {
					Utilities.showToast(R.string.response_no_object, activity);
					return;
				}
				int st = bean.st;
				if (st == -1 || st == -2) {
					Utilities.showToast(bean.msg, activity);
					SharePreferencesUtils.put(activity,
							SharedPreferencesKeys.UID, "");
					SharePreferencesUtils.put(activity,
							SharedPreferencesKeys.CERTIFICATED, "");
					Intent intent = new Intent(activity, LoginActivity.class);
					startActivity(intent);
					return;
				}
				if (st == 0) {
					Intent intent = new Intent(activity, MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("flag", "1");
					startActivity(intent);
				} else {
					Utilities.showToast(bean.msg, activity);
				}
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast(R.string.request_error_msg, activity);
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast(R.string.response_failure_msg, activity);
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_evaluation);
		certificate = (String) SharePreferencesUtils.get(this,
				SharedPreferencesKeys.CERTIFICATED, "");
		uid = (String) SharePreferencesUtils.get(this,
				SharedPreferencesKeys.UID, "");
		init();
		initEvent();
	}

	private void initEvent() {
		mDoBack.setOnClickListener(this);
		mConfirm.setOnClickListener(this);
	}

	private void init() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mDoBack = (ImageView) findViewById(R.id.iv_headBack);
		mConfirm = (Button) findViewById(R.id.bt_confirm);
		mChecked = (RadioGroup) findViewById(R.id.rg_container);
		mContent = (EditText) findViewById(R.id.et_evaluation);
		mHeadContent.setText("互相评价");
		mDoBack.setVisibility(View.VISIBLE);
		taskNo = getIntent().getExtras().getString(Constant.TASKNO);
		activity = this;
		stackInstance.pushActivity(activity);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_headBack:
			stackInstance.popActivity(activity);
			break;
		case R.id.bt_confirm:
			upLoading();
			break;
		default:
			break;
		}
	}

	private void startLoading() {
		startProgressDialog(getResources().getString(R.string.loading_public_default));
		Requester requester = new Requester();
		requester.certificate = certificate;
		requester.uid = uid;
		requester.cmd = 20058;
		requester.body.put(Constant.TASKNO, taskNo);
		requester.body.put(Constant.EVALUATELEVEL, evaluateLevel);
		requester.body.put(Constant.EVALUATEINFO, evaluateInfo);

		Request request = httpEngine.createRequest(SystemConfig.NEWIP,
				gson.toJson(requester));
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					msg.what = StatusCode.EVALUATEOTHER;
					msg.obj = response.body().string();
				} else {
					msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
					msg.obj = "服务器异常";
				}
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(Request arg0, IOException arg1) {
				Message msg = new Message();
				msg.what = StatusCode.RESPONSE_NET_FAILED;
				msg.obj = "网络异常";
				handler.sendMessage(msg);
			}
		});
	}

	private void upLoading() {
		evaluateInfo = mContent.getText().toString().trim();
		int rbId = mChecked.getCheckedRadioButtonId();
		switch (rbId) {
		case R.id.rb_verygood:
			evaluateLevel = "3";
			break;
		case R.id.rb_sogood:
			evaluateLevel = "2";
			break;
		case R.id.rb_good:
			evaluateLevel = "1";
			break;
		case R.id.rb_notbad:
			evaluateLevel = "0";
			break;
		case R.id.rb_bad:
			if (TextUtils.isEmpty(evaluateInfo)) {
				Utilities.showToast("请告诉我们您选择差评的原因", this);
				return;
			} else if (evaluateInfo.length() < 15) {
				Utilities.showToast("输入的字符少于15个", this);
				return;
			}
			evaluateLevel = "-2";
			break;
		}
		startLoading();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		stackInstance.popActivity(activity);
	}

}
