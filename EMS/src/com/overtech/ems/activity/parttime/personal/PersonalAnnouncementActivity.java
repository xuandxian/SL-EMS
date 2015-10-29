package com.overtech.ems.activity.parttime.personal;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.adapter.PersonalAnnouncementAdapter;
import com.overtech.ems.entity.test.Data6;
/**
 * 公告栏
 * */
public class PersonalAnnouncementActivity extends BaseActivity {
	private ImageView mDoBack;
	private	TextView mHeadContent;
	private ListView mAnnouncement;
	private List<Data6> list;
	private Context mActivity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_announcement);
		initView();
		init();
		initData();
	}

	private void initData() {
		list=new ArrayList<Data6>();
		Data6 data1=new Data6("1.原型图设计1.0版本验收");
		Data6 data2=new Data6("2.原型图设计1.0版本验收");
		Data6 data3=new Data6("3.原型图设计1.0版本验收");
		Data6 data4=new Data6("4.原型图设计1.0版本验收");
		Data6 data5=new Data6("5.原型图设计1.0版本验收");
		Data6 data6=new Data6("6.原型图设计1.0版本验收");
		list.add(data1);
		list.add(data2);
		list.add(data3);
		list.add(data4);
		list.add(data5);
		list.add(data6);
		PersonalAnnouncementAdapter adapter=new PersonalAnnouncementAdapter(list, mActivity);
		mAnnouncement.setAdapter(adapter);
		mAnnouncement.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent =new Intent (mActivity,PersonalAnnouncementDetailActivity.class);
				startActivity(intent);
			}
		});
	}

	private void init() {
		mHeadContent.setText("公告");
		mDoBack.setVisibility(View.VISIBLE);
		mDoBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void initView() {
		mActivity=PersonalAnnouncementActivity.this;
		mHeadContent=(TextView) findViewById(R.id.tv_headTitle);
		mDoBack=(ImageView) findViewById(R.id.iv_headBack);
		mAnnouncement=(ListView) findViewById(R.id.lv_announcement);
	}
}
