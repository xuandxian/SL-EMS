package com.overtech.ems.activity.parttime.personal;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.adapter.PersonalAccountHasCountAdapter;
import com.overtech.ems.activity.adapter.PersonalAccountNoCountAdapter;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.bean.BillBean;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class PersonalAccountListActivity extends BaseActivity implements
		OnClickListener {
	private ImageView mDoBack;
	private TextView mHeadContent;
	private ListView mPersonalAccountListView;
	private TextView mHasCount;
	private TextView mNoCount;
	private BaseAdapter adapter;
	private static final String HASCOUNT = "1";
	private static final String NOCOUNT = "0";
	private String uid;
	private String certificate;
	private PersonalAccountListActivity activity;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.ACCOUNT_LIST_SUCCESS:
				String json = (String) msg.obj;
				Log.e("==w我的账单==" + msg.arg1, json);
				BillBean datas = gson.fromJson(json, BillBean.class);
				int st = datas.st;
				if (st == -1 || st == -2) {
					Utilities.showToast(datas.msg, activity);
					SharePreferencesUtils.put(activity,
							SharedPreferencesKeys.UID, "");
					SharePreferencesUtils.put(activity,
							SharedPreferencesKeys.CERTIFICATED, "");
					Intent intent = new Intent(activity, LoginActivity.class);
					startActivity(intent);
					return;
				}
				List<Map<String, Object>> data = (List<Map<String, Object>>) datas.body
						.get("data");
				if (msg.arg1 == 1) {
					adapter = new PersonalAccountHasCountAdapter(activity, data);
				} else {
					adapter = new PersonalAccountNoCountAdapter(activity, data);
				}
				mPersonalAccountListView.setAdapter(adapter);
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast("服务端异常", activity);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast("网络异常", activity);
				break;
			}
			stopProgressDialog();
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		activity = this;
		stackInstance.pushActivity(activity);
		setContentView(R.layout.activity_personal_account_list);
		findViewById();
		initData();
		startLoading(HASCOUNT);// 默认已结算
	}

	private void startLoading(final String billState) {
		startProgressDialog("正在加载...");
		Requester requester = new Requester();
		requester.cmd = 20074;
		requester.certificate = certificate;
		requester.uid = uid;
		requester.body.put("closeStatus", billState);// 0未结算

		Request requst = httpEngine.createRequest(SystemConfig.NEWIP,
				gson.toJson(requester));
		Call call = httpEngine.createRequestCall(requst);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					msg.what = StatusCode.ACCOUNT_LIST_SUCCESS;
					msg.arg1 = Integer.parseInt(billState);
					msg.obj = response.body().string();
				} else {
					msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
				}
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(Request response, IOException arg1) {
				Message msg = new Message();
				msg.what = StatusCode.RESPONSE_NET_FAILED;
				handler.sendMessage(msg);
			}
		});
	}

	private void findViewById() {
		mDoBack = (ImageView) findViewById(R.id.iv_headBack);
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mPersonalAccountListView = (ListView) findViewById(R.id.lv_personal_account_list);
		mHasCount = (TextView) findViewById(R.id.tv_account_donet);
		mNoCount = (TextView) findViewById(R.id.tv_account_none);
	}

	private void initData() {
		certificate = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.CERTIFICATED, "");
		uid = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.UID, "");
		mDoBack.setVisibility(View.VISIBLE);
		mHeadContent.setText("我的账单");
		mDoBack.setOnClickListener(this);
		mHasCount.setOnClickListener(this);
		mNoCount.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.iv_headBack:
			stackInstance.popActivity(activity);
			break;
		case R.id.tv_account_donet:
			startLoading(HASCOUNT);
			mHasCount.setBackgroundResource(R.drawable.horizontal_line);
			mNoCount.setBackgroundResource(R.color.main_white);
			mHasCount.setTextColor(Color.rgb(0, 163, 233));
			mNoCount.setTextColor(getResources().getColor(
					R.color.main_secondary));
			break;
		case R.id.tv_account_none:
			startLoading(NOCOUNT);
			mHasCount.setBackgroundResource(R.color.main_white);
			mNoCount.setBackgroundResource(R.drawable.horizontal_line);
			mHasCount.setTextColor(getResources().getColor(
					R.color.main_secondary));
			mNoCount.setTextColor(Color.rgb(0, 163, 233));
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		stackInstance.popActivity(activity);
	}
}
