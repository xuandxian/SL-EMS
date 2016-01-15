package com.overtech.ems.test;

import java.util.ArrayList;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.entity.test.Data7;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.swipemenu.SwipeMenu;
import com.overtech.ems.widget.swipemenu.SwipeMenuCreator;
import com.overtech.ems.widget.swipemenu.SwipeMenuItem;
import com.overtech.ems.widget.swipemenu.SwipeMenuListView;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.TypedValue;

public class TestActivity extends BaseActivity implements OnRefreshListener{
	private SwipeRefreshLayout mSwipeLayout;
	private SwipeMenuListView listview;
	private SwipeMenuCreator creator;
	private ArrayList<Data7> list=new ArrayList<Data7>();
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		findViewById();
		getData();
		init();
		initHistoryItem();
	}


	private void findViewById() {
		mSwipeLayout=(SwipeRefreshLayout) findViewById(R.id.srl_container);
		listview=(SwipeMenuListView)findViewById(R.id.lv_swipe_history);
	}
	private void getData() {
		Data7 data1=new Data7("1");
		Data7 data2=new Data7("1");
		Data7 data3=new Data7("1");
		Data7 data4=new Data7("1");
		Data7 data5=new Data7("1");
		list.add(data1);
		list.add(data2);
		list.add(data3);
		list.add(data4);
		list.add(data5);
	}
	
	private void init() {
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,  
				android.R.color.holo_orange_light, android.R.color.holo_red_light);
		TestAdapter adapter=new TestAdapter(list, context);
		listview.setAdapter(adapter);
	}
	private void initHistoryItem() {
		creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem deleteItem = new SwipeMenuItem(TestActivity.this);
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				deleteItem.setWidth(dp2px(90));
				deleteItem.setTitle("删除");
				deleteItem.setTitleSize(18);
				deleteItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(deleteItem);
			}
		};
		listview.setMenuCreator(creator);
	}
	
	@Override
	public void onRefresh() {
		Utilities.showToast("SwipeRefreshLayout-->onRefresh", getApplicationContext());
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Utilities.showToast("onRefresh-->complete", getApplicationContext());
				mSwipeLayout.setRefreshing(false);
			}
		}, 3000);
	}
	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
}
