package com.overtech.ems.activity.fulltime.maintenance;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.fulltime.activity.MaintenanceDoneDetailActivity;
import com.overtech.ems.activity.fulltime.adapter.MaintenaceNoneAdapter;
import com.overtech.ems.activity.fulltime.adapter.MaintenanceDoneAdapter;
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.http.HttpConnector;

public class MaintenanceDoneFragment extends BaseFragment {
	private ListView listview;
	private SwipeRefreshLayout swipeRefresh;
	private TextView tvNoData;
	private String uid;
	private String certificate;
	private List<Map<String,Object>> list;
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

		swipeRefresh.setColorSchemeResources(R.color.colorPrimary,
				R.color.colorPrimary30);
		swipeRefresh.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				requestLoading();
			}
		});
		swipeRefresh.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				swipeRefresh.setRefreshing(true);
			}
		});
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Map<String,Object> data=((MaintenanceDoneAdapter)parent.getAdapter()).getData(position);
				Intent intent =new Intent(activity,MaintenanceDoneDetailActivity.class);
				intent.putExtra("workorderCode", data.get("workorderCode").toString());
				startActivity(intent);
			}
		});
		
		requestLoading();
		return view;
	}

	private void requestLoading() {
		// TODO Auto-generated method stub
		HttpConnector<Bean> conn=new HttpConnector<Bean>(20001,uid,certificate,null) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				
				list = (List<Map<String,Object>>) response.body.get("data");
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

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub
			}

			@Override
			public void bizStIs1Deal() {
				// TODO Auto-generated method stub
			}

			@Override
			public void stopDialog() {
				// TODO Auto-generated method stub
				if (swipeRefresh.isRefreshing()) {
					swipeRefresh.setRefreshing(false);
				}
				stopProgressDialog();
			}
		};
		conn.sendRequest();
	}

}
