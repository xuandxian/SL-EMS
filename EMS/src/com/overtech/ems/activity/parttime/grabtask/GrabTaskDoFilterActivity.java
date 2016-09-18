package com.overtech.ems.activity.parttime.grabtask;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar.OnMenuItemClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.adapter.GrabTaskFilterAdapter;
import com.overtech.ems.utils.Logr;

/**
 * Created by Tony1213 on 15/12/05. Change on 15/12/17
 * 
 * @author Tony1213 抢单条件筛选
 */
public class GrabTaskDoFilterActivity extends BaseActivity implements
		OnClickListener {
	private Toolbar toolbar;
	private ActionBar actionBar;
	private AppCompatTextView tvTitle;
	private GridView gridView;
	private GrabTaskFilterAdapter adapter;
	private GrabTaskFilterAdapter adapter2;
	private Button mZone;
	private Button mTime;
	private GrabTaskDoFilterActivity activity;
	int[] image = { R.drawable.filter_zone_huangpu,
			R.drawable.filter_zone_xuhui, R.drawable.filter_zone_changning,
			R.drawable.filter_zone_jingan, R.drawable.filter_zone_putuo,
			R.drawable.filter_zone_hongkou, R.drawable.filter_zone_yangpu,
			R.drawable.filter_zone_minhang, R.drawable.filter_zone_baoshan,
			R.drawable.filter_zone_jiading, R.drawable.filter_zone_pudong,
			R.drawable.filter_zone_jinshan, R.drawable.filter_zone_songjiang,
			R.drawable.filter_zone_qingpu, R.drawable.filter_zone_fengxian,
			R.drawable.filter_zone_chongming, R.drawable.filter_zone_zhabei,
			R.drawable.filter_zone_luwan, R.drawable.filter_zone_nanhui };
	int[] image2 = { R.drawable.filter_time_fifteen_in,
			R.drawable.filter_time_fifteen_out };

	@Override
	protected int getLayoutResIds() {
		// TODO Auto-generated method stub
		return R.layout.activity_grab_task_filter;
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		initView();
		init();
		setOnClickListener();
	}

	private void setOnClickListener() {
		mZone.setOnClickListener(this);
		mTime.setOnClickListener(this);
	}

	private void initView() {
		toolbar=(Toolbar) findViewById(R.id.toolBar);
		setSupportActionBar(toolbar);
		actionBar = getSupportActionBar();
		tvTitle = (AppCompatTextView) findViewById(R.id.tvTitle);
		mZone = (Button) findViewById(R.id.button1);
		mTime = (Button) findViewById(R.id.button2);
		gridView = (GridView) findViewById(R.id.gridView1);
	}

	private void init() {
		activity = this;
		tvTitle.setText("筛 选");
		Logr.e("toolbar==="+toolbar);
		toolbar.setNavigationOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		toolbar.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem menuItem) {
				// TODO Auto-generated method stub
				switch (menuItem.getItemId()) {
				case R.id.menu_confirm:
					confirm();
					break;

				default:
					break;
				}
				return true;
			}
		});
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		adapter = new GrabTaskFilterAdapter(image, activity);
		adapter2 = new GrabTaskFilterAdapter(image2, activity);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.choiceStatus(position);
			}
		});
		mZone.setBackgroundResource(R.drawable.selector);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		toolbar.inflateMenu(R.menu.menu_filter_confirm);
		return true;
	}

	private void confirm() {
		String mZone = "";
		String mTime = "";
		ArrayList<String> list = adapter.getTypePositon(image);
		ArrayList<String> list2 = adapter2.getTypePositon(image2);
		for (int i = 0; i < list.size(); i++) {
			int temp = Integer.valueOf(list.get(i));
			switch (temp) {
			case 0:
				mZone += "黄浦区|";
				break;
			case 1:
				mZone += "徐汇区|";
				break;
			case 2:
				mZone += "长宁区|";
				break;
			case 3:
				mZone += "静安区|";
				break;
			case 4:
				mZone += "普陀区|";
				break;
			case 5:
				mZone += "虹口区|";
				break;
			case 6:
				mZone += "杨浦区|";
				break;
			case 7:
				mZone += "闵行区|";
				break;
			case 8:
				mZone += "宝山区|";
				break;
			case 9:
				mZone += "嘉定区|";
				break;
			case 10:
				mZone += "浦东新区|";
				break;
			case 11:
				mZone += "金山区|";
				break;
			case 12:
				mZone += "松江区|";
				break;
			case 13:
				mZone += "青浦区|";
				break;
			case 14:
				mZone += "奉贤区|";
				break;
			case 15:
				mZone += "崇明县|";
				break;
			case 16:
				mZone += "闸北区|";
				break;
			case 17:
				mZone += "卢湾区|";
				break;
			case 18:
				mZone += "南汇区|";
				break;
			}
		}
		for (int i = 0; i < list2.size(); i++) {
			int temp2 = Integer.valueOf(list2.get(i));
			switch (temp2) {
			case 0:
				mTime += "0";
				break;
			case 1:
				mTime += "1";
				break;
			}
		}
		Intent intent = new Intent();
		intent.putExtra("mZone", mZone);
		intent.putExtra("mTime", mTime);
		setResult(Activity.RESULT_OK, intent);
		finish();
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
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					adapter.choiceStatus(position);
				}
			});
			mZone.setBackgroundResource(R.drawable.selector);
			mTime.setBackgroundDrawable(null);
			break;
		case R.id.button2:
			if (adapter2 != null) {
				adapter2.notifyDataSetChanged();
			}
			gridView.setAdapter(adapter2);
			gridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					adapter2.choiceStatus(position);
				}
			});
			mTime.setBackgroundResource(R.drawable.selector);
			mZone.setBackgroundDrawable(null);
			break;
		}
	}

}
