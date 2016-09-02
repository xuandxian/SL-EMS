package com.overtech.ems.activity.parttime.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.parttime.tasklist.TaskListDoneFragment;
import com.overtech.ems.activity.parttime.tasklist.TaskListNoneFragment;
import com.overtech.ems.utils.FragmentUtils;
import com.overtech.ems.utils.Logr;

public class TaskListFragment extends BaseFragment implements OnClickListener {

	private Activity mActivity;
	private TextView mNone;
	private TextView mDonet;
	private TextView mHead;
	private Fragment currentFragment;
	private TaskListNoneFragment mTaskNone;
	private TaskListDoneFragment mTaskDone;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_task_list, container,
				false);
		initView(view);
		setDefaultView();
		return view;
	}

	private void initView(View view) {
		mHead = (TextView) view.findViewById(R.id.tv_tasklist_title);
		mNone = (TextView) view.findViewById(R.id.tv_tasklist_none);
		mDonet = (TextView) view.findViewById(R.id.tv_tasklist_donet);
		mNone.setOnClickListener(this);
		mDonet.setOnClickListener(this);
	}

	private void setDefaultView() {
		mHead.setText("任务单");
		currentFragment = FragmentUtils.switchFragment(
				getChildFragmentManager(), R.id.fl_container, currentFragment,
				TaskListNoneFragment.class, null);
		mTaskNone = (TaskListNoneFragment) currentFragment;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		Logr.e("TaskListFragment=="+hidden);
		if(!hidden){
			if(mTaskNone.isVisible()){
				mTaskNone.onRefresh();
			}else if(mTaskDone.isVisible()){
				mTaskDone.onRefresh();
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_tasklist_none:
			currentFragment = FragmentUtils.switchFragment(
					getChildFragmentManager(), R.id.fl_container,
					currentFragment, TaskListNoneFragment.class, null);
			mTaskNone = (TaskListNoneFragment) currentFragment;
			mNone.setBackgroundResource(R.drawable.horizontal_line);
			mDonet.setBackgroundResource(R.color.main_white);
			mNone.setTextColor(Color.rgb(0, 185, 239));
			mDonet.setTextColor(getResources().getColor(R.color.main_secondary));
			break;
		case R.id.tv_tasklist_donet:
			currentFragment = FragmentUtils.switchFragment(
					getChildFragmentManager(), R.id.fl_container,
					currentFragment, TaskListDoneFragment.class, null);
			mTaskDone = (TaskListDoneFragment) currentFragment;
			mNone.setBackgroundResource(R.color.main_white);
			mDonet.setBackgroundResource(R.drawable.horizontal_line);
			mNone.setTextColor(getResources().getColor(R.color.main_secondary));
			mDonet.setTextColor(Color.rgb(0, 185, 239));
			break;
		}
	}

}
