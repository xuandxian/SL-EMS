package com.overtech.ems.activity.parttime.fragment;

import com.overtech.ems.R;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NearByFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_nearby, container, false);
		initView();
		initEvents();
		return view;
	}

	private void initEvents() {
		
	}

	private void initView() {
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		Fragment mNearByMap=new NearByMapFragment();
		transaction.replace(R.id.rl_nearby_content, mNearByMap);
		transaction.commit();
	}
}
