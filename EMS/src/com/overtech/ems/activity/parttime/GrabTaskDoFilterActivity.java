package com.overtech.ems.activity.parttime;

import com.overtech.ems.R;
import com.overtech.ems.activity.adapter.GridViewAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class GrabTaskDoFilterActivity extends Activity {

	private ImageView mHeadBack;
	private TextView mHeadContent;
	private TextView mHeadContentRight;
	private GridView gridView;
	private GridViewAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_grab_task_filter);
		findViewById();
		init();
	}

	private void findViewById() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
		mHeadContentRight = (TextView) findViewById(R.id.tv_headTitleRight);
		gridView = (GridView) findViewById(R.id.gridView1);
	}

	private void init() {
		mHeadContent.setText("筛 选");
		mHeadContentRight.setText("确定");
		mHeadBack.setVisibility(View.VISIBLE);
		int[] image = { R.drawable.grid, R.drawable.grid, R.drawable.grid,
				R.drawable.grid, R.drawable.grid, R.drawable.grid,
				R.drawable.grid, R.drawable.grid, R.drawable.grid,
				R.drawable.grid, R.drawable.grid, R.drawable.grid,
				R.drawable.grid, R.drawable.grid, R.drawable.grid,
				R.drawable.grid, R.drawable.grid, R.drawable.grid };
		adapter = new GridViewAdapter(image, getApplicationContext());
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				adapter.chiceState(position);
			}
		});
	}
}
