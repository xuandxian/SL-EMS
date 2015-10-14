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

public class NearByFragment extends Fragment {
	
	private Button mNearByMapBtn;
	private Button mNearByListBtn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_nearby, container, false);
		initMapView();
		init(view);
		initEvents();
		return view;
	}

	private void init(View view) {
		mNearByMapBtn=(Button)view.findViewById(R.id.btn_nearby_title_map);
		mNearByListBtn=(Button)view.findViewById(R.id.btn_nearby_title_list);
	}

	private void initEvents() {
		mNearByMapBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				initMapView();
			}
		});
		mNearByListBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				initNearByListView();
			}
		});
	}

	private void initMapView() {
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		Fragment mNearByMap=new NearByMapFragment();
		transaction.replace(R.id.rl_nearby_content, mNearByMap);
		transaction.commit();
	}
	
	private void initNearByListView() {
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		Fragment mNearByList=new NearByListFragment();
		transaction.replace(R.id.rl_nearby_content, mNearByList);
		transaction.commit();
	}
	
}
