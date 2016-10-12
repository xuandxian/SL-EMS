package com.overtech.ems.activity.fulltime.activity.fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.fulltime.activity.MaintenanceResponseActivity;
import com.overtech.ems.activity.fulltime.activity.MaintenanceTaskActivity;
import com.overtech.ems.activity.fulltime.adapter.MaintenanceChildAdapter;
import com.overtech.ems.activity.fulltime.adapter.MaintenanceReportExpandAdapter;
import com.overtech.ems.activity.fulltime.adapter.MaintenanceChildAdapter.ChildViewHolder;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.http.HttpConnector;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.Logr;

public class MaintenanceReportFragment extends BaseFragment implements
		OnClickListener {
	private ExpandableListView expand;
	private ListView lvMaintenanceDetail;
	private AppCompatButton btPrevious;
	private AppCompatButton btNext;
	private MaintenanceReportExpandAdapter mainAdapter;
	private MaintenanceChildAdapter childAdapter;
	private MaintenanceTaskActivity activity;
	private String elevatorNo;
	private String uid;
	private String certificate;
	private String workorderCode;
	private OnNextClickListener listener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		activity = (MaintenanceTaskActivity) getActivity();
		uid = activity.getUid();
		certificate = activity.getCertificate();
		workorderCode = activity.getWorkorderCode();
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_maintenance_report,
				container, false);
		initView(v);
		initEvent();
		initData();
		return v;
	}

	private void initData() {
		// TODO Auto-generated method stub
		startProgressDialog(getResources().getString(
				R.string.loading_public_default));
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put("workorderCode", workorderCode);
		body.put("isMain", activity.getIsMain());
		HttpConnector<Bean> connector = new HttpConnector<Bean>(20010, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				if (mainAdapter == null) {
					List<Map<String, Object>> datas = (List<Map<String, Object>>) response.body
							.get("datas");
					mainAdapter = new MaintenanceReportExpandAdapter(activity,
							datas);
					expand.setAdapter(mainAdapter);
					if (childAdapter == null) {
						if (!mainAdapter.isHaveThreeLevel(0)) {
							childAdapter = new MaintenanceChildAdapter(
									activity, (List<Map<String, Object>>) datas
											.get(0).get("children"));
							lvMaintenanceDetail.setAdapter(childAdapter);
						} else {
							List<Map<String, Object>> childs = (List<Map<String, Object>>) datas
									.get(0).get("children");
							childAdapter = new MaintenanceChildAdapter(
									activity,
									(List<Map<String, Object>>) childs.get(0)
											.get("children"));
							lvMaintenanceDetail.setAdapter(childAdapter);
						}
					}
				} else {
				}
			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void bizStIs1Deal(Bean response) {
				// TODO Auto-generated method stub
				activity.stackInstance.popActivity(activity);
			}

			@Override
			public void stopDialog() {
				// TODO Auto-generated method stub
				stopProgressDialog();
			}
		};
		connector.sendRequest();
	}

	private void initEvent() {
		// TODO Auto-generated method stub
		expand.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// TODO Auto-generated method stub
				MaintenanceReportExpandAdapter adapter = (MaintenanceReportExpandAdapter) parent
						.getExpandableListAdapter();
				Map<String, Object> level1 = adapter.getParent(groupPosition);
				if (!mainAdapter.isHaveThreeLevel(groupPosition)) {
					childAdapter.setData((List<Map<String, Object>>) level1
							.get("children"));
					childAdapter.notifyDataSetChanged();
				}
				return false;
			}
		});
		expand.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				MaintenanceReportExpandAdapter adapter = (MaintenanceReportExpandAdapter) parent
						.getExpandableListAdapter();
				Map<String, Object> childrenLevel2 = adapter.getChildren(
						groupPosition, childPosition);
				childAdapter.setData((List<Map<String, Object>>) childrenLevel2
						.get("children"));
				childAdapter.notifyDataSetChanged();
				return false;
			}
		});
		lvMaintenanceDetail.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				MaintenanceChildAdapter adapter = (MaintenanceChildAdapter) parent
						.getAdapter();
				Map<String, Object> child = (Map<String, Object>) adapter
						.getItem(position);
				ChildViewHolder vh = (ChildViewHolder) view.getTag();
				vh.cbStatus.toggle();
				if (vh.cbStatus.isChecked()) {
					child.put("checked", "1");
				} else {
					child.put("checked", "0");
				}
			}
		});
		btNext.setOnClickListener(this);
		btPrevious.setOnClickListener(this);
	}

	private void initView(View v) {
		// TODO Auto-generated method stub
		expand = (ExpandableListView) v
				.findViewById(R.id.elv_maintenance_report);
		lvMaintenanceDetail = (ListView) v
				.findViewById(R.id.lv_maintenance_detail);
		btPrevious = (AppCompatButton) v.findViewById(R.id.bt_previous);
		btNext = (AppCompatButton) v.findViewById(R.id.bt_next);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_previous:
			activity.onBackPressed();
			break;
		case R.id.bt_next:
			String json = gson.toJson(mainAdapter.getData());
			if (listener != null) {
				listener.onNextClick(json);
			}
			break;

		default:
			break;
		}
	}

	public void setOnNextClickListener(OnNextClickListener listener) {
		this.listener = listener;
	}

	public interface OnNextClickListener {
		void onNextClick(String json);
	}
}
