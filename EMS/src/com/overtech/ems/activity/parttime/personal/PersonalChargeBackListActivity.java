package com.overtech.ems.activity.parttime.personal;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.adapter.PersonalChargebackAdapter;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.bean.ChargebackBean;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class PersonalChargeBackListActivity extends BaseActivity {
	private ImageView mDoBack;
	private TextView mHeadTitle;
	private ListView mChargeback;
	private TextView tvNoData;
	private List<Map<String, Object>> list;
	private PersonalChargebackAdapter adapter;
	private String uid;
	private String certificate;
	private PersonalChargeBackListActivity activity;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			stopProgressDialog();
			switch (msg.what) {
			case StatusCode.PERSONAL_CHARGEBACK_SUCCESS:
				String info = (String) msg.obj;
				Logr.e(info);
				ChargebackBean bean = gson.fromJson(info, ChargebackBean.class);
				int st = bean.st;
				if (st == -1 || st == -2) {
					Utilities.showToast(bean.msg, activity);
					SharePreferencesUtils.put(activity,
							SharedPreferencesKeys.UID, "");
					SharePreferencesUtils.put(activity,
							SharedPreferencesKeys.CERTIFICATED, "");
					Intent intent = new Intent(activity, LoginActivity.class);
					startActivity(intent);
				} else {
					list = (List<Map<String, Object>>) bean.body.get("data");
					if (list == null || list.size() == 0) {
						Utilities.showToast(
								getString(R.string.response_no_data), activity);
						tvNoData.setVisibility(View.VISIBLE);
						mChargeback.setVisibility(View.GONE);
					} else {
						tvNoData.setVisibility(View.GONE);
						mChargeback.setVisibility(View.VISIBLE);
						adapter = new PersonalChargebackAdapter(activity, list);
						mChargeback.setAdapter(adapter);
					}
				}
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				String exception = (String) msg.obj;
				Utilities.showToast(R.string.response_failure_msg, activity);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast(R.string.request_error_msg, activity);
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected int getLayoutResIds() {
		// TODO Auto-generated method stub
		return R.layout.activity_personal_cancle_list;
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		initView();
		init();
	}

	private void init() {
		activity = this;
		stackInstance.pushActivity(activity);
		certificate = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.CERTIFICATED, "");
		uid = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.UID, "");
		mDoBack.setVisibility(View.VISIBLE);
		mDoBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stackInstance.popActivity(activity);
			}
		});
		mHeadTitle.setText("退单记录");
		startLoading();
	}

	private void startLoading() {
		startProgressDialog(getResources().getString(
				R.string.loading_public_default));
		Requester requester = new Requester();
		requester.cmd = 20076;
		requester.certificate = certificate;
		requester.uid = uid;

		Request request = httpEngine.createRequest(SystemConfig.NEWIP,
				gson.toJson(requester));
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					msg.what = StatusCode.PERSONAL_CHARGEBACK_SUCCESS;
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

	private void initView() {
		mDoBack = (ImageView) findViewById(R.id.iv_headBack);
		mHeadTitle = (TextView) findViewById(R.id.tv_headTitle);
		mChargeback = (ListView) findViewById(R.id.lv_cancle_task_record);
		tvNoData = (TextView) findViewById(R.id.tv_no_data);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		stackInstance.popActivity(activity);
	}

}
