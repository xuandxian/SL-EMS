package com.overtech.ems.activity.parttime.personal;

import java.util.LinkedList;

import com.overtech.ems.R;
import com.overtech.ems.widget.pulltorefresh.PullToRefreshBase;
import com.overtech.ems.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.overtech.ems.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.overtech.ems.widget.pulltorefresh.PullToRefreshListView;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PersonalCompanyNoticesActivity extends Activity {
	private PullToRefreshListView mCompanyNoticesListView;
	private LinkedList<String> mListItems;    //显示的列表对应原字符串  
	private ArrayAdapter<String> mAdapter;  //ListView的适配器 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_company_notice);
		mCompanyNoticesListView = (PullToRefreshListView) findViewById(R.id.lv_company_notice);
		mCompanyNoticesListView.setMode(Mode.PULL_FROM_END);
		mCompanyNoticesListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
