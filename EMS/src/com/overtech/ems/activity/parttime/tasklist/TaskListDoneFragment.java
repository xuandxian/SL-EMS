package com.overtech.ems.activity.parttime.tasklist;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.adapter.TaskListAdapter;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.entity.bean.TaskPackageBean;
import com.overtech.ems.entity.bean.TaskPackageBean.TaskPackage;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.http.OkHttpClientManager;
import com.overtech.ems.http.OkHttpClientManager.ResultCallback;
import com.overtech.ems.utils.Logr;
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
	private List<Map<String,Object>> list;
	private TextView tvNoData;
	private TaskListAdapter adapter;
	private String uid;
	private String certificate;

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
		startProgressDialog(getResources().getString(R.string.loading_public_default));
		Requester requester = new Requester();
		requester.certificate = certificate;
		requester.uid = uid;
		requester.cmd = 20054;
		ResultCallback<Bean> callback = new ResultCallback<Bean>() {

			@Override
			public void onError(Request request, Exception e) {
				// TODO Auto-generated method stub
				Logr.e(request.toString());
				stopProgressDialog();
			}

			@Override
			public void onResponse(Bean response) {
				// TODO Auto-generated method stub
				stopProgressDialog();
				if (response == null) {
					Utilities.showToast(R.string.response_no_object, activity);
					return;
				}
				list = (List<Map<String, Object>>) response.body.get("data");
				int st = response.st;
				if (st == -1 || st == -2) {
					if (activity != null) {
						Utilities.showToast(response.msg, activity);
						SharePreferencesUtils.put(activity,
								SharedPreferencesKeys.UID, "");
						SharePreferencesUtils.put(activity,
								SharedPreferencesKeys.CERTIFICATED, "");
						Intent intent = new Intent(activity,
								LoginActivity.class);
						startActivity(intent);
					}
				}
				if (list == null || list.size() == 0) {
					Utilities.showToast(getResources().getString(R.string.response_no_data), mActivity);
					tvNoData.setVisibility(View.VISIBLE);
					mDonet.setVisibility(View.GONE);
				} else {
					tvNoData.setVisibility(View.GONE);
					mDonet.setVisibility(View.VISIBLE);
					adapter = new TaskListAdapter(list, mActivity,
							StatusCode.TASK_DO);
					mDonet.setAdapter(adapter);
				}
			}
		};
		OkHttpClientManager.postAsyn(SystemConfig.NEWIP, callback,
				gson.toJson(requester));
		
	}

	private void findViewById(View view) {
		mDonet = (ListView) view.findViewById(R.id.donet_task_list_listview);
		tvNoData=(TextView) view.findViewById(R.id.tv_no_data);
	}
}
