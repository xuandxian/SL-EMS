package com.overtech.ems.activity.fulltime.maintenance;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.activity.fulltime.activity.MaintenanceDetailActivity;
import com.overtech.ems.activity.fulltime.adapter.MaintenaceNoneAdapter;
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.entity.fulltime.MaintenanceBean;
import com.overtech.ems.entity.fulltime.MaintenanceBean.Workorder;
import com.overtech.ems.http.OkHttpClientManager;
import com.overtech.ems.http.OkHttpClientManager.ResultCallback;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Request;

public class MaintenanceNoneFragment extends BaseFragment {
	private ListView listview;
	private String uid;
	private String certificate;
	private List<Workorder> list;
	private MaintenaceNoneAdapter adapter;

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_maintenance_none, null);
		listview = (ListView) view.findViewById(R.id.lv_maintenance_none);
		uid = ((MainActivity) getActivity()).getUid();
		certificate = ((MainActivity) getActivity()).getCertificate();
		startProgressDialog("加载中...");
		requestLoading();
		initEvent();
		return view;
	}

	private void initEvent() {
		// TODO Auto-generated method stub
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String workorderCode = ((MaintenaceNoneAdapter) arg0
						.getAdapter()).getItem(arg2);
				Intent intent = new Intent(getActivity(),
						MaintenanceDetailActivity.class);
				intent.putExtra(Constant.WORKORDERCODE, workorderCode);
				startActivity(intent);
			}

		});
	}

	private void requestLoading() {
		// TODO Auto-generated method stub
		Requester requester = new Requester();
		requester.cmd = 20000;
		requester.uid = uid;
		requester.certificate = certificate;
		ResultCallback<MaintenanceBean> callback = new ResultCallback<MaintenanceBean>() {

			@Override
			public void onResponse(MaintenanceBean response) {
				// TODO Auto-generated method stub
				if (response == null) {
					Utilities.showToast("暂时没有数据", activity);
					stopProgressDialog();
					return;
				}
				int st = response.st;
				String msg = response.msg;
				if (st != 0) {
					if (st == -1 || st == -2) {
						if (activity != null) {
							Utilities.showToast(msg, activity);
							Intent intent = new Intent(activity,
									LoginActivity.class);
							startActivity(intent);
							getActivity().finish();
						}
					} else {
						Utilities.showToast(msg, activity);
					}
				} else {
					list = response.body.data;
					if (adapter == null) {
						adapter = new MaintenaceNoneAdapter(getActivity(), list);
						listview.setAdapter(adapter);
					} else {
						adapter.setData(list);
						adapter.notifyDataSetChanged();
					}
				}
			}

			@Override
			public void onError(Request request, Exception e) {
				// TODO Auto-generated method stub
				Logr.e(request.toString());
			}
		};
		OkHttpClientManager.postAsyn(SystemConfig.NEWIP, callback,
				gson.toJson(requester));
	}
}
