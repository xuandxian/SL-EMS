package com.overtech.ems.activity.parttime.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.parttime.nearby.NearByListFragment;
import com.overtech.ems.activity.parttime.nearby.NearByMapFragment;

public class NearByFragment extends Fragment implements OnClickListener {

	private TextView mNearByMapTextView;
	private TextView mNearByListTextView;
	private View view;
	private FragmentManager manager;
	private FragmentTransaction transaction;
	private Fragment mNearByMap;
	private Fragment mNearByList;
	private int one;
	private TextView mHeadTitle;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_nearby, container, false);
		init();
		setDefaultView();
		return view;
	}


	private void init() {
		mNearByMapTextView = (TextView) view.findViewById(R.id.tv_nearby_map);
		mNearByListTextView = (TextView) view.findViewById(R.id.tv_nearby_list);
		mHeadTitle=(TextView)view.findViewById(R.id.tv_headTitle);
		mHeadTitle.setText("附近");
		mNearByMapTextView.setOnClickListener(this);
		mNearByListTextView.setOnClickListener(this);
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
		case R.id.tv_nearby_map:
			switchContent(mNearByList, mNearByMap);
			mNearByMapTextView.setBackgroundResource(R.drawable.horizontal_line);
			mNearByListTextView.setBackgroundResource(R.drawable.bg_white);
			mNearByMapTextView.setTextColor(Color.rgb(0, 163, 233));
			mNearByListTextView.setTextColor(getResources().getColor(R.color.main_secondary));
			break;
		case R.id.tv_nearby_list:
			switchContent(mNearByMap, mNearByList);
			mNearByMapTextView.setBackgroundResource(R.drawable.bg_white);
			mNearByListTextView.setBackgroundResource(R.drawable.horizontal_line);
			mNearByMapTextView.setTextColor(getResources().getColor(R.color.main_secondary));
			mNearByListTextView.setTextColor(Color.rgb(0, 163, 233));
			break;
		}
	}
}
