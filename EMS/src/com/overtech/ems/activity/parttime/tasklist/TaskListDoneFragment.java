package com.overtech.ems.activity.parttime.tasklist;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.adapter.TaskListAdapter;
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.http.HttpConnector;
import com.overtech.ems.http.constant.Constant;

public class TaskListDoneFragment extends BaseFragment implements
		OnRefreshListener {
	private SwipeRefreshLayout swipeRefresh;
	private ListView listView;
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
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String taskNo = list.get(position).get("taskNo").toString();
				Intent intent = new Intent(getActivity(),
						TaskListPackageDoneDetailActivity.class);
				intent.putExtra(Constant.TASKNO, taskNo);
				startActivity(intent);
			}
		});
	}

	private void findViewById(View view) {
		listView = (ListView) view.findViewById(R.id.donet_task_list_listview);
		swipeRefresh = (SwipeRefreshLayout) view
				.findViewById(R.id.swipeRefresh);
		llNoPage = (LinearLayout) view.findViewById(R.id.page_no_result);
		btLoadRetry = (Button) view.findViewById(R.id.load_btn_retry);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20054, uid,
				certificate, null) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub

				list = (List<Map<String, Object>>) response.body.get("data");
				if (list == null || list.size() == 0) {
					llNoPage.setVisibility(View.VISIBLE);
					swipeRefresh.setVisibility(View.GONE);
				} else {
					llNoPage.setVisibility(View.GONE);
					swipeRefresh.setVisibility(View.VISIBLE);
					if (adapter == null) {
						adapter = new TaskListAdapter(list, mActivity,
								StatusCode.TASK_DO);
						listView.setAdapter(adapter);
					} else {
						adapter.setData(list);
						adapter.notifyDataSetChanged();
					}
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

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (!hidden) {
			onRefresh();
		}
	}
}
