package com.overtech.ems.activity.parttime.personal;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.bean.ChargebackBean;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class PersonalChargeBackListActivity extends BaseActivity {
	private ImageView mDoBack;
	private TextView mHeadTitle;
	private ListView mChargeback;
	private List<Map<String, Object>> list;
	private PersonalChargebackAdapter adapter;
	private String uid;
	private String certificate;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.PERSONAL_CHARGEBACK_SUCCESS:
				String info = (String) msg.obj;
				ChargebackBean bean = gson.fromJson(info, ChargebackBean.class);
				int st = bean.st;
				if (st != 0) {
					Utilities.showToast(bean.msg, context);
				} else {
					list = (List<Map<String, Object>>) bean.body.get("data");
					if (list.size() == 0) {
						Utilities.showToast("无数据", context);
					} else {
						adapter = new PersonalChargebackAdapter(context, list);
						mChargeback.setAdapter(adapter);
					}
				}
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				String exception = (String) msg.obj;
				Utilities.showToast(exception, context);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast((String) msg.obj, context);
				break;
			default:
				break;
			}
			stopProgressDialog();
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_cancle_list);
		initView();
		init();
	}

	private void init() {
		mDoBack.setVisibility(View.VISIBLE);
		mDoBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mHeadTitle.setText("退单记录");
		startLoading();
	}

	private void startLoading() {
		startProgressDialog("正在加载中...");
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
	}
}
