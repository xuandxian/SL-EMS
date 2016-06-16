package com.overtech.ems.activity.fulltime.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.fulltime.adapter.MaintenanceChildAdapter;
import com.overtech.ems.activity.fulltime.adapter.MaintenanceChildAdapter.ChildViewHolder;
import com.overtech.ems.activity.fulltime.adapter.MaintenanceGrandSonAdapter;
import com.overtech.ems.activity.fulltime.adapter.MaintenanceGrandSonAdapter.GrandSonViewHolder;
import com.overtech.ems.entity.fulltime.MaintenanceReportBean.Children;
import com.overtech.ems.entity.fulltime.MaintenanceReportBean.Parent;
import com.overtech.ems.http.constant.Constant;

/**
 * 维修报告单详细信息
 * 
 * @author Overtech Will
 * 
 */
public class MaintenanceTaskDetailActivity extends BaseActivity {
	private ListView listView;
	private Button btNegative;
	private Button btPositive;
	private Parent maintenanceParent;
	private Children maintenanceChild;
	private String name;
	private TextView title;
	private MaintenanceChildAdapter childAdapter;
	private MaintenanceGrandSonAdapter grandSonAdapter;
	private MaintenanceTaskDetailActivity activity;
	public static final int PARENT = 100;
	public static final int CHILD = 101;
	/**
	 * two level or three level
	 */
	private int type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maintenance_task_detail);
		activity = this;
		stackInstance.pushActivity(activity);
		initView();
		initEvent();
		initData();
	}

	private void initData() {
		// TODO Auto-generated method stub
		Bundle bundle = getIntent().getExtras();
		type = bundle.getInt(Constant.TYPE);
		if (type == PARENT) {// parent
			maintenanceParent = (Parent) bundle
					.getSerializable(Constant.MAINTENANCEDETAIL);
			childAdapter = new MaintenanceChildAdapter(this,
					maintenanceParent.children);
			listView.setAdapter(childAdapter);
		} else {// child
			maintenanceChild = (Children) bundle
					.getSerializable(Constant.MAINTENANCEDETAIL);
			grandSonAdapter = new MaintenanceGrandSonAdapter(this,
					maintenanceChild.children);
		}
		name = bundle.getString(Constant.MAINTENANCENAME);

		title.setText(name);
	}

	private void initEvent() {
		// TODO Auto-generated method stub
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (type == PARENT) {
					ChildViewHolder viewHolder = (ChildViewHolder) view
							.getTag();
					viewHolder.cbStatus.toggle();
					if (viewHolder.cbStatus.isChecked()) {// 选中条目时将对应数据状态改变
						maintenanceParent.children.get(position).checked = "1";
					} else {
						maintenanceParent.children.get(position).checked = "0";
					}
				} else {
					GrandSonViewHolder viewHolder = (GrandSonViewHolder) view
							.getTag();
					viewHolder.cbStatus.toggle();
					if (viewHolder.cbStatus.isChecked()) {
						maintenanceChild.children.get(position).checked = "1";
					} else {
						maintenanceChild.children.get(position).checked = "0";
					}
				}

			}
		});
		btNegative.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(Activity.RESULT_CANCELED);
			}
		});
		btPositive.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(Activity.RESULT_OK, getIntent());
			}
		});
	}

	private void initView() {
		// TODO Auto-generated method stub
		title = (TextView) findViewById(R.id.tv_headTitle);
		listView = (ListView) findViewById(R.id.lv_maintenance_task_detail);
		btNegative = (Button) findViewById(R.id.bt_negative);
		btPositive = (Button) findViewById(R.id.bt_positive);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		stackInstance.popActivity(activity);
	}
}
