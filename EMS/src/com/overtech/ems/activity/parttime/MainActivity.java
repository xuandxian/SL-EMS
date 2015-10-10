package com.overtech.ems.activity.parttime;

import com.overtech.ems.R;
import com.overtech.ems.activity.adapter.HotWorkAdapter;
import com.overtech.views.swipemenu.SwipeMenu;
import com.overtech.views.swipemenu.SwipeMenuCreator;
import com.overtech.views.swipemenu.SwipeMenuItem;
import com.overtech.views.swipemenu.SwipeMenuListView;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Window;
import android.widget.TextView;

/**
 * @author Tony
 * @description 主界面
 * @date 2015-10-05
 */
public class MainActivity extends Activity {
	private TextView mHeadContent;
	private SwipeMenuListView mSwipeListView;
	private SwipeMenuCreator creator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		findViewById();
		init();
	}

	private void findViewById() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mSwipeListView = (SwipeMenuListView) findViewById(R.id.sl_qiandan_listview);
	}

	private void init() {
		Context mContext = MainActivity.this;
		mHeadContent.setText("抢单");
		initListView();
		mSwipeListView.setMenuCreator(creator);
		HotWorkAdapter mAdapter = new HotWorkAdapter(mContext);
		mSwipeListView.setAdapter(mAdapter);
	}

	private void initListView() {
		creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem openItem = new SwipeMenuItem(MainActivity.this);
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
