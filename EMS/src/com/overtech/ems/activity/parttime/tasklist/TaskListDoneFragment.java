package com.overtech.ems.activity.parttime.tasklist;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.adapter.TaskListAdapter;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.bean.TaskPackageBean;
import com.overtech.ems.entity.bean.TaskPackageBean.TaskPackage;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class TaskListDoneFragment extends BaseFragment {
	private ListView mDonet;
	private Activity mActivity;
	private List<TaskPackage> list;
	private TaskListAdapter adapter;
	private String uid;
	private String certificate;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.TASKLIST_DONET_SUCCESS:
				String json = (String) msg.obj;
				TaskPackageBean bean = gson.fromJson(json,
						TaskPackageBean.class);
				list = bean.body.data;
				int st = bean.st;
				if (st == -1 || st == -2) {
					if(activity!=null){
						Utilities.showToast(bean.msg, activity);
						SharePreferencesUtils.put(activity,
								SharedPreferencesKeys.UID, "");
						SharePreferencesUtils.put(activity,
								SharedPreferencesKeys.CERTIFICATED, "");
						Intent intent = new Intent(activity, LoginActivity.class);
						startActivity(intent);
						activity.finish();
					}
				}
				if (list == null || list.size() == 0) {
					Utilities.showToast("无数据", mActivity);
				} else {
					adapter = new TaskListAdapter(list, mActivity,
							StatusCode.TASK_DO);
					mDonet.setAdapter(adapter);
				}
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast((String) msg.obj, mActivity);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast((String) msg.obj, mActivity);
				break;

			default:
				break;
			}
		};
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_task_list_donet,
				container, false);
		uid = ((MainActivity) getActivity()).getUid();
		certificate = ((MainActivity) getActivity()).getCertificate();
		findViewById(view);
		startLoading();
		return view;
	}

	private void startLoading() {
		Requester requester = new Requester();
		requester.certificate = certificate;
		requester.uid = uid;
		requester.cmd = 20054;
		Request request = httpEngine.createRequest(SystemConfig.NEWIP,
				gson.toJson(requester));
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					msg.what = StatusCode.TASKLIST_DONET_SUCCESS;
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

	private void findViewById(View view) {
		mDonet = (ListView) view.findViewById(R.id.donet_task_list_listview);
	}
}
