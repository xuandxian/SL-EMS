package com.overtech.ems.activity.parttime.fragment;

import com.overtech.ems.R;
import com.overtech.ems.activity.adapter.HotWorkAdapter;
import com.overtech.ems.utils.Utilities;
import com.overtech.views.swipemenu.SwipeMenu;
import com.overtech.views.swipemenu.SwipeMenuCreator;
import com.overtech.views.swipemenu.SwipeMenuItem;
import com.overtech.views.swipemenu.SwipeMenuListView;
import com.overtech.views.swipemenu.SwipeMenuListView.OnMenuItemClickListener;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class GrabTaskFragment extends Fragment {

	private SwipeMenuListView mSwipeListView;
	private SwipeMenuCreator creator;
	private Activity mActivity;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity=activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_grab_task, container,
				false);
		findViewById(view);
		init();
		return view;
	}

	private void findViewById(View view) {
		mSwipeListView = (SwipeMenuListView) view
				.findViewById(R.id.sl_qiandan_listview);
	}

	private void init() {
		initListView();
		mSwipeListView.setMenuCreator(creator);
		HotWorkAdapter mAdapter = new HotWorkAdapter(mActivity);
		mSwipeListView.setAdapter(mAdapter);
		mSwipeListView
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public void onMenuItemClick(int position, SwipeMenu menu,
							int index) {
						Utilities.showToast("你抢了" + position + "位置的单子",
								mActivity);
					}
				});
		mSwipeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Utilities.showToast("你点击了" + position + "位置", mActivity);
			}
		});
	}

	private void initListView() {
		creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem openItem = new SwipeMenuItem(
						mActivity);
				openItem.setBackground(new ColorDrawable(Color.rgb(0x00, 0xff,
						0x00)));
				openItem.setWidth(dp2px(90));
				openItem.setTitle("抢");
				openItem.setTitleSize(18);
				openItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(openItem);
			}
		};
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
}
