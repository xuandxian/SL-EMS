package com.overtech.ems.activity.parttime.tasklist;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.adapter.TaskListAdapter;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.http.OkHttpClientManager;
import com.overtech.ems.http.OkHttpClientManager.ResultCallback;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Request;

public class TaskListDoneFragment extends BaseFragment implements
		OnRefreshListener {
	private SwipeRefreshLayout swipeRefresh;
	private ListView mDonet;
	private LinearLayout llNoPage;
	private Button btLoadRetry;
	private Activity mActivity;
	private List<Map<String, Object>> list;
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
		initEvent();
		startProgressDialog(getResources().getString(
				R.string.loading_public_default));
		onRefresh();
		return view;
	}

	private void initEvent() {
		// TODO Auto-generated method stub
		swipeRefresh.setColorSchemeResources(R.color.material_deep_teal_200,
				R.color.material_deep_teal_500);
		swipeRefresh.setOnRefreshListener(this);
		btLoadRetry.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onRefresh();
			}
		});
	}

	private void findViewById(View view) {
		mDonet = (ListView) view.findViewById(R.id.donet_task_list_listview);
		swipeRefresh = (SwipeRefreshLayout) view
				.findViewById(R.id.swipeRefresh);
		llNoPage = (LinearLayout) view.findViewById(R.id.page_no_result);
		btLoadRetry = (Button) view.findViewById(R.id.load_btn_retry);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		Requester requester = new Requester();
		requester.certificate = certificate;
		requester.uid = uid;
		requester.cmd = 20054;
		ResultCallback<Bean> callback = new ResultCallback<Bean>() {

			@Override
			public void onError(Request request, Exception e) {
				// TODO Auto-generated method stub
				Logr.e(request.toString());
				if (swipeRefresh.isRefreshing()) {
					swipeRefresh.setRefreshing(false);
				}
				stopProgressDialog();
			}

			@Override
			public void onResponse(Bean response) {
				// TODO Auto-generated method stub
				stopProgressDialog();
				if (swipeRefresh.isRefreshing()) {
					swipeRefresh.setRefreshing(false);
				}
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
					llNoPage.setVisibility(View.VISIBLE);
					swipeRefresh.setVisibility(View.GONE);
				} else {
					llNoPage.setVisibility(View.GONE);
					swipeRefresh.setVisibility(View.VISIBLE);
					if (adapter == null) {
						adapter = new TaskListAdapter(list, mActivity,
								StatusCode.TASK_DO);
						mDonet.setAdapter(adapter);
					} else {
						adapter.setData(list);
						adapter.notifyDataSetChanged();
					}
				}
			}
		};
		OkHttpClientManager.postAsyn(SystemConfig.NEWIP, callback,
				gson.toJson(requester));
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (!hidden) {
			onRefresh();
		}
	}
}
