package com.overtech.ems.activity.parttime.personal;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.overtech.ems.R;
import com.overtech.ems.activity.adapter.PersonalAccountListAdapter;
import com.overtech.ems.entity.test.Data2;

public class PersonalAccountListActivity extends Activity implements OnClickListener {
	private ImageView mDoBack;
	private TextView mHeadContent;
	private ListView mPersonalAccountListView;
	private TextView mHasCount;
	private TextView mNoCount;
	private PersonalAccountListAdapter adapter;
	private PersonalAccountListAdapter adapter2;
	private ArrayList<Data2> list;
	private ArrayList<Data2> list2;
	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_personal_account_list);
		findViewById();
		getData();
		getData2();
		initData();
	}

	private void findViewById() {
		mDoBack=(ImageView)findViewById(R.id.iv_headBack);
		mHeadContent=(TextView)findViewById(R.id.tv_headTitle);
		mPersonalAccountListView=(ListView)findViewById(R.id.lv_personal_account_list);
		mHasCount=(TextView) findViewById(R.id.tv_account_donet);
		mNoCount = (TextView)findViewById(R.id.tv_account_none);
	}
	private ArrayList<Data2> getData2(){
		list2=new ArrayList<Data2>();
		for(int i=0;i<10;i++){
			Data2 data=new Data2("丰业广元"+i,"2015-10-1"+i,"￥200","2015-10-2"+i);
			list2.add(data);
		}
		return list2;
	}
	private ArrayList<Data2> getData() {
		//模拟数据
		Data2 data=new Data2("丰业广元公寓", "2015-10-10", "￥200", "2015-10-15");
		Data2 data1=new Data2("徐家汇景园", "2015-10-10", "￥200", "2015-10-15");
		Data2 data2=new Data2("丰业广元公寓", "2015-10-11", "￥200", "2015-10-17");
		Data2 data3=new Data2("南虹小区", "2015-10-12", "￥200", "2015-10-16");
		Data2 data4=new Data2("南虹小区", "2015-10-12", "￥100", "2015-10-16");
		Data2 data5=new Data2("南虹小区", "2015-10-12", "￥200", "2015-10-16");
		Data2 data6=new Data2("徐家汇景园", "2015-10-13", "￥300", "2015-10-16");
		Data2 data7=new Data2("徐家汇景园", "2015-10-13", "￥100", "2015-10-16");
		Data2 data8=new Data2("徐家汇景园", "2015-10-13", "￥300", "2015-10-16");
		Data2 data9=new Data2("徐家汇景园", "2015-10-13", "￥150", "2015-10-17");
		list=new ArrayList<Data2>();
		list.add(data);
		list.add(data1);
		list.add(data2);
		list.add(data3);
		list.add(data4);
		list.add(data5);
		list.add(data6);
		list.add(data7);
		list.add(data8);
		list.add(data9);
		return list;
	}
	private void initData() {
		mDoBack.setVisibility(View.VISIBLE);
		mHeadContent.setText("我的账单");
		context=PersonalAccountListActivity.this;
		mDoBack.setOnClickListener(this);
		mHasCount.setOnClickListener(this);
		mNoCount.setOnClickListener(this);
		adapter=new PersonalAccountListAdapter(context, list);
		mPersonalAccountListView.setAdapter(adapter);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.iv_headBack:
			finish();
			break;
		case R.id.tv_account_donet:
			if(adapter!=null){
				adapter.notifyDataSetChanged();
			}
			mPersonalAccountListView.setAdapter(adapter);
			
			mHasCount.setBackgroundResource(R.drawable.horizontal_line);
			mNoCount.setBackgroundResource(R.color.main_white);
			mHasCount.setTextColor(Color.rgb(0, 163, 233));
			mNoCount.setTextColor(getResources().getColor(R.color.main_secondary));
			break;
		case R.id.tv_account_none:
			if(adapter2!=null){
				adapter2.notifyDataSetChanged();
			}
			adapter2=new PersonalAccountListAdapter(context, list2);
			mPersonalAccountListView.setAdapter(adapter2);
			
			mHasCount.setBackgroundResource(R.color.main_white);
			mNoCount.setBackgroundResource(R.drawable.horizontal_line);
			mHasCount.setTextColor(getResources().getColor(R.color.main_secondary));
			mNoCount.setTextColor(Color.rgb(0, 163, 233));
			break;
		default:
			break;
		}
	}
}
