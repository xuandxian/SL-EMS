package com.overtech.ems.activity.parttime.fragment;

import java.util.List;

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
import com.overtech.ems.activity.parttime.nearby.NearByListFragment;
import com.overtech.ems.activity.parttime.nearby.NearByMapFragment;
import com.overtech.ems.entity.bean.TaskPackageBean.TaskPackage;
import com.overtech.ems.utils.FragmentUtils;
import com.overtech.ems.utils.Logr;

public class NearByFragment extends BaseFragment implements OnClickListener {

	private TextView mNearByMapTextView;
	private TextView mNearByListTextView;
	private View view;
	private Fragment currentFragment;
	private NearByMapFragment mNearByMap;
	private NearByListFragment mNearByList;
	private TextView mHeadTitle;
	private List<TaskPackage> list;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_nearby, container, false);
		init();
		return view;
	}

	private void init() {
		mNearByMapTextView = (TextView) view.findViewById(R.id.tv_nearby_map);
		mNearByListTextView = (TextView) view.findViewById(R.id.tv_nearby_list);
		mHeadTitle = (TextView) view.findViewById(R.id.tv_headTitle);
		mHeadTitle.setText("附近");
		mNearByMapTextView.setOnClickListener(this);
		mNearByListTextView.setOnClickListener(this);
		currentFragment = FragmentUtils.switchFragment(
				getChildFragmentManager(), R.id.rl_nearby_content,
				currentFragment, NearByMapFragment.class, null);
		mNearByMap = (NearByMapFragment) currentFragment;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		Logr.e("NearByFragment当前的隐藏状态==" + hidden);
		if (!hidden) {
			if(mNearByMap.isVisible()){
				mNearByMap.onRefresh();
			}else if(mNearByList.isVisible()){
				mNearByList.onRefresh();
			}
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.tv_nearby_map:
			currentFragment = FragmentUtils.switchFragment(
					getChildFragmentManager(), R.id.rl_nearby_content,
					currentFragment, NearByMapFragment.class, null);
			mNearByMap = (NearByMapFragment) currentFragment;
			mNearByMapTextView
					.setBackgroundResource(R.drawable.horizontal_line);
			mNearByListTextView.setBackgroundResource(R.color.main_white);
			mNearByMapTextView.setTextColor(Color.rgb(0, 163, 233));
			mNearByListTextView.setTextColor(getResources().getColor(
					R.color.main_secondary));
			break;
		case R.id.tv_nearby_list:
			currentFragment = FragmentUtils.switchFragment(
					getChildFragmentManager(), R.id.rl_nearby_content,
					currentFragment, NearByListFragment.class, null);
			mNearByList = (NearByListFragment) currentFragment;

			mNearByMapTextView.setBackgroundResource(R.color.main_white);
			mNearByListTextView
					.setBackgroundResource(R.drawable.horizontal_line);
			mNearByMapTextView.setTextColor(getResources().getColor(
					R.color.main_secondary));
			mNearByListTextView.setTextColor(Color.rgb(0, 163, 233));
			break;
		}
	}

}