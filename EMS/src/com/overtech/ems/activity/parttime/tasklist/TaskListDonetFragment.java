package com.overtech.ems.activity.parttime.tasklist;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.overtech.ems.R;
import com.overtech.ems.activity.adapter.TaskListAdapter;
import com.overtech.ems.entity.test.Data4;

public class TaskListDonetFragment extends Fragment {
	private ListView mDonet;
	private Activity mActivity;
	private List<Data4> list;
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
		initData();
		initView();
		return view;
	}

	private void initData() {
		list=new ArrayList<Data4>();
		Data4 data1=new Data4("南虹小区","5部","徐汇区广元西路","13.5km","2015/10/10");
		Data4 data2=new Data4("徐家汇景园","5部","徐汇区广元西路","12.5km","2015/10/11");
		Data4 data3=new Data4("南虹小区2","5部","徐汇区广元西路","14.5km","2015/10/11");
		Data4 data4=new Data4("徐家汇景园3","5部","徐汇区广元西路","13.5km","2015/10/12");
		Data4 data5=new Data4("南虹小区4","5部","徐汇区广元西路","18.5km","2015/10/12");
		Data4 data6=new Data4("徐家汇景园5","5部","徐汇区广元西路","13.5km","2015/10/13");
		Data4 data7=new Data4("南虹小区6","5部","徐汇区广元西路","13.5km","2015/10/14");
		Data4 data8=new Data4("南虹小区7","5部","徐汇区广元西路","19.5km","2015/10/15");
		Data4 data9=new Data4("南虹小区8","5部","徐汇区广元西路","10.5km","2015/10/16");
		Data4 data10=new Data4("南虹小区9","5部","徐汇区广元西路","13.5km","2015/10/17");
		list.add(data1);
		list.add(data2);
		list.add(data3);
		list.add(data4);
		list.add(data5);
		list.add(data6);
		list.add(data7);
		list.add(data8);
		list.add(data9);
		list.add(data10);
		
	}
	private void initView() {
		TaskListAdapter adapter=new TaskListAdapter(list,mActivity);
		mDonet.setAdapter(adapter);
	}

	private void findViewById(View view) {
		mDonet=(ListView) view.findViewById(R.id.donet_task_list_listview);
	}
}
