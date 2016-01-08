package com.overtech.ems.activity.parttime.tasklist;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.overtech.ems.http.HttpEngine.Param;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.adapter.PackageDetailAdapter;
import com.overtech.ems.activity.adapter.TaskListDetailsAdapter;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.bean.TaskPackageDetailBean;
import com.overtech.ems.entity.bean.WorkTypeBean;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.entity.parttime.MaintenanceType;
import com.overtech.ems.entity.parttime.TaskPackageDetail;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.CustomProgressDialog;
import com.overtech.ems.widget.dialogeffects.Effectstype;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;

public class QueryTaskListActivity extends BaseActivity implements OnClickListener {
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
	private final String TYPE1="CALL_PHONE";
	private final String TYPE2="CONFIRM";
	ArrayList<MaintenanceType> list=new ArrayList<MaintenanceType>();
	
	private Handler handler = new Handler() {
		Gson gson=new Gson();
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.WORK_DETAILS_SUCCESS:
				String json=(String) msg.obj;
				WorkTypeBean bean=gson.fromJson(json, WorkTypeBean.class);
				ArrayList<String> tempList=bean.getModel();
				list.add(new MaintenanceType("0", "Title", "content"));
				for (int i = 0; i < tempList.size(); i++) {
					String allString=tempList.get(i);
					String[] data=allString.split("\\|");
					MaintenanceType type=new MaintenanceType(String.valueOf(i+1), data[0], data[1]);
					list.add(type);
				}
				mTaskDetailsTitle.setVisibility(View.VISIBLE);
				adapter = new TaskListDetailsAdapter(context, list);
				mTaskListData.setAdapter(adapter);
				break;
			case StatusCode.WORK_DETAILS_FAILED:
				Utilities.showToast("服务器异常", context);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast("网络链接失败", context);
				break;
			default:
				break;
			}
			stopProgressDialog();
		};
	};
	
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
		startProgressDialog("正在加载...");
		Param param = new Param(Constant.WORKTYPE, type);
		final Request request = httpEngine.createRequest(ServicesConfig.WORK_TYPE, param);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					msg.what = StatusCode.WORK_DETAILS_SUCCESS;
					msg.obj = response.body().string();
				} else {
					msg.what = StatusCode.WORK_DETAILS_FAILED;
				}
				handler.sendMessage(msg);
			}
			@Override
			public void onFailure(Request request, IOException e) {
				Log.e("QueryTaskListActivity:onFailure","onFailure");
			}

		});

	}



	@Override
	protected void onDestroy() {
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
			showDialog(TYPE2,"该电梯维保任务已完成？");
			break;
		case R.id.iv_headTitleRight:
			showDialog(TYPE1,"是否拨打技术支持电话？");
			break;
		default:
			break;
		}
	}

	private void showDialog(final String type,String message) {
		Effectstype effect = Effectstype.Slideright;
		dialogBuilder.withTitle("温馨提示").withTitleColor(R.color.main_primary)
		.withDividerColor("#11000000").withMessage(message)
		.withMessageColor(R.color.main_primary).withDialogColor("#FFFFFFFF")
		.isCancelableOnTouchOutside(true).withDuration(700)
		.withEffect(effect).withButtonDrawable(R.color.main_white)
		.withButton1Text("是").withButton1Color("#DD47BEE9")
		.withButton2Text("否").withButton2Color("#DD47BEE9")
		.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (TextUtils.equals(type, TYPE1)) {
							String phoneNo="15021565127";
							Intent intent2 =new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phoneNo));
							startActivity(intent2);
						}else {
							//请求服务器
							if(isAllAchieve){
								Utilities.showToast("恭喜，你的工作全部完成", context);
								Intent intent =new Intent(QueryTaskListActivity.this,QuestionResponseActivity.class);
								startActivity(intent);
							}else{
								Utilities.showToast("恭喜你，该电梯已完成，请继续剩下的维保工作", context);
							}
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
