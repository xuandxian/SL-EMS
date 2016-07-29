package com.overtech.ems.activity.fulltime.maintenance;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.activity.fulltime.adapter.MaintenanceDoneAdapter;
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.entity.fulltime.MaintenanceBean;
import com.overtech.ems.entity.fulltime.MaintenanceBean.Workorder;
import com.overtech.ems.http.OkHttpClientManager;
import com.overtech.ems.http.OkHttpClientManager.ResultCallback;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Request;

public class MaintenanceDoneFragment extends BaseFragment {
	private ListView listview;
	private SwipeRefreshLayout swipeRefresh;
	private TextView tvNoData;
	private String uid;
	private String certificate;
	private List<Workorder> list;
	private MaintenanceDoneAdapter adapter;

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_maintenance_done, null);
		listview = (ListView) view.findViewById(R.id.lv_maintenance_done);
		swipeRefresh = (SwipeRefreshLayout) view
				.findViewById(R.id.swipeRefresh);
		tvNoData = (TextView) view.findViewById(R.id.tv_no_data);
		uid = ((MainActivity) getActivity()).getUid();
		certificate = ((MainActivity) getActivity()).getCertificate();

		swipeRefresh.setColorSchemeResources(R.color.material_deep_teal_200,
				R.color.material_deep_teal_500);
		swipeRefresh.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				requestLoading();
			}
		});
		startProgressDialog(getResources().getString(
				R.string.loading_public_default));
		requestLoading();
		return view;
	}

	private void requestLoading() {
		// TODO Auto-generated method stub
		Requester requester = new Requester();
		requester.cmd = 20001;
		requester.uid = uid;
		requester.certificate = certificate;

		ResultCallback<MaintenanceBean> callback = new ResultCallback<MaintenanceBean>() {
			@Override
			public void onError(Request request, Exception e) {
				// TODO Auto-generated method stub
				if (swipeRefresh.isRefreshing()) {
					swipeRefresh.setRefreshing(false);
				}
				stopProgressDialog();
				Logr.e(request.toString());
			}

			@Override
			public void onResponse(MaintenanceBean response) {
				// TODO Auto-generated method stub
				stopProgressDialog();
				if (swipeRefresh.isRefreshing()) {
					swipeRefresh.setRefreshing(false);
				}
				if (response == null) {
					Utilities.showToast(R.string.response_no_object, activity);
					return;
				}
				int st = response.st;
				String msg = response.msg;
				if (st != 0) {
					if (st == -1 || st == -2) {
						if (activity != null) {
							Utilities.showToast(msg, activity);
							SharePreferencesUtils.put(activity,
									SharedPreferencesKeys.UID, "");
							SharePreferencesUtils.put(activity,
									SharedPreferencesKeys.CERTIFICATED, "");
							Intent intent = new Intent(activity,
									LoginActivity.class);
							startActivity(intent);
						}
					} else {
						Utilities.showToast(msg, activity);
					}
				} else {
					list = response.body.data;
					if (list == null || list.size() == 0) {
						tvNoData.setVisibility(View.VISIBLE);
						if (adapter != null) {
							adapter.setData(list);
							adapter.notifyDataSetChanged();
						}
						return;
					}
					tvNoData.setVisibility(View.GONE);
					if (adapter == null) {
						adapter = new MaintenanceDoneAdapter(getActivity(),
								list);
						listview.setAdapter(adapter);
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

}
