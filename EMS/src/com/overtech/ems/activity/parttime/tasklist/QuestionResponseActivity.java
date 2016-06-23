package com.overtech.ems.activity.parttime.tasklist;

import java.io.IOException;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.bean.CommonBean;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class QuestionResponseActivity extends BaseActivity implements
		OnClickListener {
	private TextView mHeadContent;
	private ImageView mDoBack;
	private Button mConfirm;
	private EditText mFeedbackInfo;
	private String feedBackInfo;
	private String taskNo;
	private String uid;
	private String certificate;
	private QuestionResponseActivity activity;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast((String) msg.obj, activity);
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast((String) msg.obj, activity);
				break;
			case StatusCode.QUESTIONFEEDBACK:
				stopProgressDialog();
				String json = (String) msg.obj;
				Logr.e(json);
				CommonBean bean = gson.fromJson(json, CommonBean.class);
				if (bean == null) {
					Utilities.showToast("无数据", activity);
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
				} else {
					Intent intent = new Intent(QuestionResponseActivity.this,
							EvaluationActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString(Constant.TASKNO, taskNo);
					intent.putExtras(bundle);
					startActivity(intent);
				}
				stopProgressDialog();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_response);
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
		mFeedbackInfo = (EditText) findViewById(R.id.et_question_response);
		mHeadContent.setText("问题反馈");
		mDoBack.setVisibility(View.VISIBLE);
		taskNo = getIntent().getExtras().getString(Constant.TASKNO, "");
		activity = this;
		stackInstance.pushActivity(activity);
		uid = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.UID, "");
		certificate = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.CERTIFICATED, "");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_headBack:
			stackInstance.popActivity(activity);
			break;
		case R.id.bt_confirm:
			feedBackInfo = mFeedbackInfo.getText().toString();
			if (TextUtils.isEmpty(feedBackInfo)) {
				Intent intent = new Intent(QuestionResponseActivity.this,
						EvaluationActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(Constant.TASKNO, taskNo);
				intent.putExtras(bundle);
				startActivity(intent);
			} else {
				startProgressDialog("正在提交...");
				startLoading();
			}
			break;

		default:
			break;
		}
	}

	private void startLoading() {
		Requester requester = new Requester();
		requester.uid = uid;
		requester.certificate = certificate;
		requester.cmd = 20057;
		requester.body.put(Constant.TASKNO, taskNo);
		requester.body.put(Constant.FEEDBACKINFO, feedBackInfo);
		Request request = httpEngine.createRequest(
				SystemConfig.NEWIP, gson.toJson(requester));
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					msg.obj = response.body().string();
					msg.what = StatusCode.QUESTIONFEEDBACK;
				} else {
					msg.obj = "服务器异常";
					msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
				}
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(Request request, IOException arg1) {
				Message msg = new Message();
				msg.what = StatusCode.RESPONSE_NET_FAILED;
				msg.obj = "网络异常";
				handler.sendMessage(msg);
			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		stackInstance.popActivity(activity);
	}
}
