package com.overtech.ems.activity.parttime.fragment;

import com.overtech.ems.R;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class NearByFragment extends Fragment implements OnClickListener {

	private Button mNearByMapBtn;
	private Button mNearByListBtn;
	private View view;
	private FragmentManager manager;
	private FragmentTransaction transaction;
	private Fragment mContent;
	private Fragment mNearByMap;
	private Fragment mNearByList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_nearby, container, false);
		init();
		setDefaultView();
		return view;
	}

	private void init() {
		mNearByMapBtn = (Button) view.findViewById(R.id.btn_nearby_title_map);
		mNearByListBtn = (Button) view.findViewById(R.id.btn_nearby_title_list);
		mNearByMapBtn.setOnClickListener(this);
		mNearByListBtn.setOnClickListener(this);
		manager = getFragmentManager();
		mNearByMap = new NearByMapFragment();
		mNearByList=new NearByListFragment();
	}

	private void setDefaultView() {
		transaction = manager.beginTransaction();
		transaction.replace(R.id.rl_nearby_content, mNearByMap, "mNearByMap");
		transaction.commit();
	}

	public void switchContent(Fragment from, Fragment to) {
		transaction = manager.beginTransaction();
		if (!to.isAdded()) { 
			transaction.hide(from).add(R.id.rl_nearby_content, to).commit(); 
		} else {
			transaction.hide(from).show(to).commit();
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_nearby_title_map:
			switchContent(mNearByList, mNearByMap);
			break;
		case R.id.btn_nearby_title_list:
			switchContent(mNearByMap, mNearByList);
			break;
		}
	}

}
