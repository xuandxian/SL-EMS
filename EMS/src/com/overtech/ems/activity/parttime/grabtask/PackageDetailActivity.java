package com.overtech.ems.activity.parttime.grabtask;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.adapter.PackageDetailAdapter;
import com.overtech.ems.entity.Data2;

public class PackageDetailActivity extends BaseActivity {
	private ListView mPackageDetailListView;
	private PackageDetailAdapter adapter;
	private ArrayList<Data2> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_package_detail);
		findViewById();
		initData();
		initListView();
	}

	private void initListView() {
		adapter = new PackageDetailAdapter(context, list);
		mPackageDetailListView.setAdapter(adapter);
		mPackageDetailListView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Intent intent = new Intent(PackageDetailActivity.this,
								ElevatorDetailActivity.class);
						startActivity(intent);
					}
				});
	}

	private void findViewById() {
		mPackageDetailListView = (ListView) findViewById(R.id.grab_task_package_listview);
	}

	private void initData() {
		// 测试数据
		Data2 data1 = new Data2("31号楼1号电梯（全包）", "上海三菱", "80032981234",
				"20层/20站");
		Data2 data2 = new Data2("31号楼2号电梯（半包）", "上海三菱", "80032981235",
				"23层/20站");
		Data2 data3 = new Data2("32号楼1号电梯（全包）", "上海三菱", "80032981236",
				"20层/20站");
		Data2 data4 = new Data2("32号楼2号电梯（半包）", "上海三菱", "80032981237",
				"25层/20站");
		Data2 data5 = new Data2("33号楼2号电梯（半包）", "上海三菱", "80032981238",
				"25层/20站");
		list = new ArrayList<Data2>();
		list.add(0, data1);
		list.add(1, data2);
		list.add(2, data3);
		list.add(3, data4);
		list.add(4, data5);
	}
}
