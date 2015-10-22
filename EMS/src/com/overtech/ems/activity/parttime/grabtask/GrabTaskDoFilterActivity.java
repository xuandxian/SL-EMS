package com.overtech.ems.activity.parttime.grabtask;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.adapter.GridViewAdapter;

public class GrabTaskDoFilterActivity extends Activity implements
		OnClickListener {

	private ImageView mHeadBack;
	private TextView mHeadContent;
	private TextView mHeadContentRight;
	private GridView gridView;
	private GridViewAdapter adapter;
	private GridViewAdapter adapter2;
	private Button mZone;
	private Button mTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_grab_task_filter);
		findViewById();
		init();
		flushContent();
	}

	private void flushContent() {
		mZone.setOnClickListener(this);
		mTime.setOnClickListener(this);
		mHeadBack.setOnClickListener(this);
	}

	private void findViewById() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
		mHeadContentRight = (TextView) findViewById(R.id.tv_headTitleRight);
		mZone = (Button) findViewById(R.id.button1);
		mTime = (Button) findViewById(R.id.button2);
		gridView = (GridView) findViewById(R.id.gridView1);

	}

	private void init() {
		mHeadContent.setText("筛 选");
		mHeadContentRight.setText("确定");
		mHeadBack.setVisibility(View.VISIBLE);
		int[] image = { R.drawable.huangpu, R.drawable.xuhui,
				R.drawable.changning, R.drawable.jingan, R.drawable.zhabei,
				R.drawable.putuo, R.drawable.hongkou, R.drawable.yangpu,
				R.drawable.minghang, R.drawable.baoshan, R.drawable.jiading,
				R.drawable.pudong, R.drawable.jinshan, R.drawable.songjiang,
				R.drawable.qingpu, R.drawable.fengxian, R.drawable.chongming };
		adapter = new GridViewAdapter(image, getApplicationContext());
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				adapter.chiceState(position);
			}
		});
		mZone.setBackgroundResource(R.drawable.selector);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}
			gridView.setAdapter(adapter);
			gridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					adapter.chiceState(position);
				}
			});
			mZone.setBackgroundResource(R.drawable.selector);
			mTime.setBackgroundDrawable(null);
			break;
		case R.id.button2:
			int[] image2 = { R.drawable.one, R.drawable.two,
					R.drawable.three2five, R.drawable.five2seven,
					R.drawable.seven, R.drawable.halfmonth, R.drawable.month };
			if (adapter2 != null) {
				adapter2.notifyDataSetChanged();
			} else {
				adapter2 = new GridViewAdapter(image2, getApplicationContext());
			}
			gridView.setAdapter(adapter2);
			gridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					adapter2.chiceState(position);
				}
			});
			mTime.setBackgroundResource(R.drawable.selector);
			mZone.setBackgroundDrawable(null);
			break;
		case R.id.iv_headBack:
			finish();
			break;
		}
	}
}
