package com.overtech.ems.activity.parttime.fragment;

import com.overtech.ems.R;
import com.overtech.ems.activity.adapter.HotWorkAdapter;
import com.overtech.ems.utils.Utilities;
import com.overtech.views.swipemenu.SwipeMenu;
import com.overtech.views.swipemenu.SwipeMenuCreator;
import com.overtech.views.swipemenu.SwipeMenuListView;
import com.overtech.views.swipemenu.SwipeMenuListView.OnMenuItemClickListener;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class NearByListFragment extends Fragment {

	private SwipeMenuListView mNearBySwipeListView;
	private SwipeMenuCreator creator;
	private Activity mActivity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_nearby_list, container,
				false);
		initListView(view);
		return view;
	}

	private void initListView(View view) {
		mNearBySwipeListView = (SwipeMenuListView) view
				.findViewById(R.id.sl_nearby_listview);
		mNearBySwipeListView.setMenuCreator(creator);
		HotWorkAdapter mAdapter = new HotWorkAdapter(mActivity);
		mNearBySwipeListView.setAdapter(mAdapter);
		mNearBySwipeListView
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public void onMenuItemClick(int position, SwipeMenu menu,
							int index) {
						Utilities.showToast("你抢了" + position + "位置的单子",
								mActivity);
					}
				});
		mNearBySwipeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Utilities.showToast("你点击了" + position + "位置", mActivity);
			}
		});
	}

}
