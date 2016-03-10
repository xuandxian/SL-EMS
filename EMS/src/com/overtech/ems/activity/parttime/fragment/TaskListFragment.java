package com.overtech.ems.activity.parttime.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.overtech.ems.R;
import com.overtech.ems.activity.parttime.tasklist.ScanCodeActivity;
import com.overtech.ems.activity.parttime.tasklist.TaskListDonetFragment;
import com.overtech.ems.activity.parttime.tasklist.TaskListNoneFragment;

public class TaskListFragment extends Fragment implements OnClickListener {

	private Activity mActivity;
	private TextView mNone;
	private TextView mDonet;
	private TextView mHead;
	private ImageView mQrCode;
	private FragmentManager manager;
	private FragmentTransaction transaction;
	private Fragment mTaskNone;
	private Fragment mTaskDonet;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_task_list, container,false);
		initView(view);
		setDefaultView();
		return view;
	}

	
	private void initView(View view) {
		mHead=(TextView) view.findViewById(R.id.tv_tasklist_title);
		mQrCode=(ImageView) view.findViewById(R.id.iv_common_qrcode);		
		mNone=(TextView) view.findViewById(R.id.tv_tasklist_none);
		mDonet=(TextView) view.findViewById(R.id.tv_tasklist_donet);
		mNone.setOnClickListener(this);
		mDonet.setOnClickListener(this);
		mQrCode.setOnClickListener(this);
		manager = getFragmentManager();
		mTaskNone=new TaskListNoneFragment();
		mTaskDonet=new TaskListDonetFragment();
	}
	
	private void setDefaultView() {
		mHead.setText("任务单");
		mQrCode.setVisibility(View.VISIBLE);
		transaction=manager.beginTransaction();
		transaction.replace(R.id.fl_container, mTaskNone).commit();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_tasklist_none:
			switchContent(mTaskDonet,mTaskNone);
			mQrCode.setVisibility(View.VISIBLE);
			mNone.setBackgroundResource(R.drawable.horizontal_line);
			mDonet.setBackgroundResource(R.color.main_white);
			mNone.setTextColor(Color.rgb(0, 185, 239));
			mDonet.setTextColor(getResources().getColor(R.color.main_secondary));
			break;
		case R.id.tv_tasklist_donet:
			switchContent(mTaskNone,mTaskDonet);
			mQrCode.setVisibility(View.GONE);
			mNone.setBackgroundResource(R.color.main_white);
			mDonet.setBackgroundResource(R.drawable.horizontal_line);
			mNone.setTextColor(getResources().getColor(R.color.main_secondary));
			mDonet.setTextColor(Color.rgb(0, 185, 239));
			break;
		case R.id.iv_common_qrcode:
			Intent intent = new Intent();
			intent.setClass(mActivity, ScanCodeActivity.class);
			startActivity(intent);
			break;
		}
	}

	private void switchContent(Fragment from, Fragment to) {
		transaction = manager.beginTransaction();
		if (!to.isAdded()) { 
			transaction.hide(from).add(R.id.fl_container, to).commit(); 
		} else {
			transaction.hide(from).show(to).commit();
		}
	}
}
