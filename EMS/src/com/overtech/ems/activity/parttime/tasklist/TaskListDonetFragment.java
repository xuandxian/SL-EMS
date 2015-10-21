package com.overtech.ems.activity.parttime.tasklist;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.overtech.ems.R;
import com.overtech.ems.activity.adapter.HotWork2Adapter;

public class TaskListDonetFragment extends Fragment {
	private ListView mDonet;
	private Activity mActivity;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.fragment_task_list_donet, container, false);
		findViewById(view);
		initView();
		return view;
	}

	private void initView() {
		HotWork2Adapter adapter=new HotWork2Adapter(mActivity);
		mDonet.setAdapter(adapter);
	}

	private void findViewById(View view) {
		mDonet=(ListView) view.findViewById(R.id.donet_task_list_listview);
	}
}
