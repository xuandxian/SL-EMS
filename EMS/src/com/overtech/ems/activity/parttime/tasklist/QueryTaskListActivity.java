package com.overtech.ems.activity.parttime.tasklist;

import java.util.ArrayList;

import com.overtech.ems.R;
import com.overtech.ems.activity.adapter.TaskListDetailsAdapter;
import com.overtech.ems.entity.test.Data3;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.CustomProgressDialog;
import com.overtech.ems.widget.pulltorefresh.internal.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class QueryTaskListActivity extends Activity {
	private MainFrameTask mMainFrameTask = null;
	private CustomProgressDialog progressDialog = null;
	private Context context;
	private TextView mHeadContent;
	private TextView mHeadResult;
	private ImageView mHeadBack;
	private String result;
	private ListView mTaskListData;
	private TaskListDetailsAdapter adapter;
	private ArrayList<Data3> list;
	private TextView mTaskDetailsTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_task_details);
		init();
		initEvent();
		getExtraAndSetData();
	}

	private void initEvent() {
		mHeadResult.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent =new Intent(QueryTaskListActivity.this,QuestionResponseActivity.class);
				startActivity(intent);
			}
		});
	}

	private void init() {
		context = QueryTaskListActivity.this;
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
		mHeadResult=(TextView) findViewById(R.id.tv_headTitleRight);
		mTaskDetailsTitle=(TextView)findViewById(R.id.tv_task_detail_title);
		
		
		mHeadContent.setText("维保清单");
		mHeadBack.setVisibility(View.VISIBLE);
		mHeadResult.setText("完成");
		mHeadResult.setVisibility(View.VISIBLE);
		mTaskListData = (ListView) findViewById(R.id.lv_task_details);
	}

	private void getExtraAndSetData() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		result = bundle.getString("result");
		
		Utilities.showToast(result, this);
		if (TextUtils.isEmpty(result) || result == null) {
			return;
		} else {
			mMainFrameTask = new MainFrameTask(this);
			mMainFrameTask.execute();
		}
	}

	public class MainFrameTask extends AsyncTask<Integer, String, Integer> {
		private QueryTaskListActivity mainFrame = null;

		public MainFrameTask(QueryTaskListActivity mainFrame) {
			this.mainFrame = mainFrame;
		}

		@Override
		protected void onCancelled() {
			stopProgressDialog();
			super.onCancelled();
		}

		@Override
		protected Integer doInBackground(Integer... params) {

			try {
				Thread.sleep(3 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			startProgressDialog();
		}

		@Override
		protected void onPostExecute(Integer result) {
			stopProgressDialog();
			mTaskDetailsTitle.setVisibility(View.VISIBLE);
			list = new ArrayList<Data3>();
			Data3 data = new Data3("0", "维保内容0", "维保内容和要求");
			Data3 data1 = new Data3("1", "维保内容1", "维保内容和要求");
			Data3 data2 = new Data3("2", "维保内容2", "维保内容和要求");
			Data3 data3 = new Data3("3", "维保内容3", "维保内容和要求");
			Data3 data4 = new Data3("4", "维保内容4", "维保内容和要求");
			Data3 data5 = new Data3("5", "维保内容5", "维保内容和要求");
			Data3 data6 = new Data3("6", "维保内容6", "维保内容和要求");
			Data3 data7 = new Data3("7", "维保内容7", "维保内容和要求");
			Data3 data8 = new Data3("8", "维保内容8", "维保内容和要求");
			Data3 data9 = new Data3("9", "维保内容9", "维保内容和要求");
			Data3 data10 = new Data3("10", "维保内容10", "维保内容和要求");
			Data3 data11 = new Data3("11", "维保内容11", "维保内容和要求");
			Data3 data12 = new Data3("12", "维保内容12", "维保内容和要求");
			Data3 data13 = new Data3("13", "维保内容13", "维保内容和要求");
			Data3 data14 = new Data3("14", "维保内容14", "维保内容和要求");
			Data3 data15 = new Data3("15", "维保内容15", "维保内容和要求");
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
			list.add(data10);
			list.add(data11);
			list.add(data12);
			list.add(data13);
			list.add(data14);
			list.add(data15);
			adapter = new TaskListDetailsAdapter(context, list);
			mTaskListData.setAdapter(adapter);
		}
	}

	private void startProgressDialog() {
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createDialog(this);
			progressDialog.setMessage("正在查询中...");
		}
		progressDialog.show();
	}

	private void stopProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	@Override
	protected void onDestroy() {
		stopProgressDialog();
		if (mMainFrameTask != null && !mMainFrameTask.isCancelled()) {
			mMainFrameTask.cancel(true);
		}
		super.onDestroy();
	}
}
