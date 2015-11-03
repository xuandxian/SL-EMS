package com.overtech.ems.activity.parttime.fragment;

import com.overtech.ems.R;
import com.overtech.ems.widget.PullToZoomListView;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class PersonalZoneFragment2 extends Fragment {
	private PullToZoomListView listView;
	private String[] adapterData;
	private Activity mActivity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_personal_zone2,
				container, false);
		initViews(view);
		// initEvents();
		return view;
	}

	private void initViews(View view) {
		listView = (PullToZoomListView) view.findViewById(R.id.listview);
		adapterData = new String[] { "我的收藏", "我的评论", "我要售购", "出售/求购", "捡拾/寻物",
				"注销登陆", "退出程序" };
		listView.setAdapter(new ArrayAdapter<String>(mActivity,
				android.R.layout.simple_list_item_1, adapterData));
	}

}
