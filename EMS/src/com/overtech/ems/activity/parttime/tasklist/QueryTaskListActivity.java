package com.overtech.ems.activity.parttime.tasklist;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.overtech.ems.http.HttpEngine.Param;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.adapter.TaskListDetailsAdapter;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.CustomProgressDialog;
import com.overtech.ems.widget.dialogeffects.Effectstype;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class QueryTaskListActivity extends BaseActivity implements OnClickListener {
	private CustomProgressDialog progressDialog = null;
	private Context context;
	private TextView mHeadContent;
	private ImageView mHeadBack,mCallPhone;
	private String mWorktype;
	private ListView mTaskListData;
	private View mListFooterView;
	private Button mDone;
	private TaskListDetailsAdapter adapter;
	private TextView mTaskDetailsTitle;
	/**
	 * 默认的没有维保的电梯
	 */
	private boolean isAchieve=false;
	private boolean isAllAchieve=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_details);
		getExtraData();
		init();
		initEvent();
		getData();
	}

	private void initEvent() {
		mHeadBack.setOnClickListener(this);
		mCallPhone.setOnClickListener(this);
		mDone.setOnClickListener(this);
	}

	private void init() {
		context = QueryTaskListActivity.this;
		mListFooterView=LayoutInflater.from(context).inflate(R.layout.listview_footer_done, null);
		mDone=(Button) mListFooterView.findViewById(R.id.btn_login);
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
		mCallPhone = (ImageView) findViewById(R.id.iv_headTitleRight);
		mTaskDetailsTitle=(TextView)findViewById(R.id.tv_task_detail_title);
		mHeadContent.setText("维保清单");
		mHeadBack.setVisibility(View.VISIBLE);
		mCallPhone.setVisibility(View.VISIBLE);
		mTaskListData = (ListView) findViewById(R.id.lv_task_details);
		mTaskListData.addFooterView(mListFooterView);
	}

	private void getExtraData() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mWorktype = bundle.getString("mWorkType","").trim();
		Utilities.showToast(mWorktype, this);
	}
	private void getData(){
		String type="";
		if (TextUtils.equals("",mWorktype)){
			return;
		}else if (TextUtils.equals("半月保",mWorktype)){
			type="0";
		}else if (TextUtils.equals("季度保",mWorktype)){
			type="1";
		}else if (TextUtils.equals("半年保",mWorktype)){
			type="2";
		}else if (TextUtils.equals("年保",mWorktype)){
			type="3";
		}
		Param param = new Param(Constant.WORKTYPE, type);
		final Request request = httpEngine.createRequest(ServicesConfig.WORK_TYPE, param);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {

			}

			@Override
			public void onResponse(Response response) throws IOException {
				Log.e("QueryTaskListActivity",response.body().toString());

			}
		});

	}

//	public class MainFrameTask extends AsyncTask<Integer, String, Integer> {
//		private QueryTaskListActivity mainFrame = null;
//
//		public MainFrameTask(QueryTaskListActivity mainFrame) {
//			this.mainFrame = mainFrame;
//		}
//
//		@Override
//		protected void onCancelled() {
//			stopProgressDialog();
//			super.onCancelled();
//		}
//
//		@Override
//		protected Integer doInBackground(Integer... params) {
//
//			try {
//				Thread.sleep(3 * 1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			return null;
//		}
//
//		@Override
//		protected void onPreExecute() {
//			startProgressDialog("正在查询...");
//		}
//
//		@Override
//		protected void onPostExecute(Integer result) {
//			stopProgressDialog();
//			mTaskDetailsTitle.setVisibility(View.VISIBLE);
//			list = new ArrayList<Data3>();
//			Data3 data = new Data3("0", "维保内容0", "维保内容和要求");
//			Data3 data1 = new Data3("1", "维保内容1", "维保内容和要求");
//			Data3 data2 = new Data3("2", "维保内容2", "维保内容和要求");
//			Data3 data3 = new Data3("3", "维保内容3", "维保内容和要求");
//			Data3 data4 = new Data3("4", "维保内容4", "维保内容和要求");
//			Data3 data5 = new Data3("5", "维保内容5", "维保内容和要求");
//			Data3 data6 = new Data3("6", "维保内容6", "维保内容和要求");
//			Data3 data7 = new Data3("7", "维保内容7", "维保内容和要求");
//			Data3 data8 = new Data3("8", "维保内容8", "维保内容和要求");
//			Data3 data9 = new Data3("9", "维保内容9", "维保内容和要求");
//			Data3 data10 = new Data3("10", "维保内容10", "维保内容和要求");
//			Data3 data11 = new Data3("11", "维保内容11", "维保内容和要求");
//			Data3 data12 = new Data3("12", "维保内容12", "维保内容和要求");
//			Data3 data13 = new Data3("13", "维保内容13", "维保内容和要求");
//			Data3 data14 = new Data3("14", "维保内容14", "维保内容和要求");
//			Data3 data15 = new Data3("15", "维保内容15", "维保内容和要求");
//			list.add(data);
//			list.add(data1);
//			list.add(data2);
//			list.add(data3);
//			list.add(data4);
//			list.add(data5);
//			list.add(data6);
//			list.add(data7);
//			list.add(data8);
//			list.add(data9);
//			list.add(data10);
//			list.add(data11);
//			list.add(data12);
//			list.add(data13);
//			list.add(data14);
//			list.add(data15);
//			adapter = new TaskListDetailsAdapter(context, list);
//			mTaskListData.setAdapter(adapter);
//		}
//	}

	@Override
	protected void onDestroy() {
		stopProgressDialog();
//		if (mMainFrameTask != null && !mMainFrameTask.isCancelled()) {
//			mMainFrameTask.cancel(true);
//		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_headBack:
			finish();
			break;
		case R.id.btn_login:
			//将对应的电梯的完成状态更新到服务器
			showDialog();
			
			break;
		case R.id.iv_headTitleRight:
			String phoneNo="15021565127";
			Intent intent2 =new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phoneNo));
			startActivity(intent2);
			break;
		default:
			break;
		}
	}

	private void showDialog() {
		Effectstype effect = Effectstype.Shake;
		dialogBuilder.withTitle("温馨提示").withTitleColor(R.color.main_primary)
		.withDividerColor(R.color.divider_color).withMessage("请确认该电梯维保清单已完成？")
		.withMessageColor(R.color.main_primary).withDialogColor("#FFFFFFFF")
		.isCancelableOnTouchOutside(true).withDuration(700)
		.withEffect(effect).withButtonDrawable(R.color.main_white)
		.withButton1Text("确认").withButton1Color(R.color.main_primary)
		.withButton2Text("取消").withButton2Color(R.color.main_primary)
		.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//请求服务器
						if(isAllAchieve){
							Utilities.showToast("恭喜，你的工作全部完成", context);
							Intent intent =new Intent(QueryTaskListActivity.this,QuestionResponseActivity.class);
							startActivity(intent);
						}else{
							Utilities.showToast("恭喜你，该电梯已完成，请继续剩下的维保工作", context);
						}
					}
				}).setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
					}
				}).show();
	}
}
