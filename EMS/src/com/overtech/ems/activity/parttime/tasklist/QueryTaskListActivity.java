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

import org.json.JSONException;
import org.json.JSONObject;

public class QueryTaskListActivity extends BaseActivity implements OnClickListener {
	private Context context;
	private TextView mHeadContent;
	private ImageView mHeadBack,mCallPhone;
	private String mWorktype;
	private String mZonePhone;
	private String mTaskNo;
	private String mElevatorNo;
	/**
	 * 当前电梯的完成状态
	 */
	private boolean currentElevatorIsFinish;
	private ListView mTaskListData;
	private View mListFooterView;
	private Button mDone;
	private TaskListDetailsAdapter adapter;
	private TextView mTaskDetailsTitle;
	private final String TYPE1="CALL_PHONE";
	private final String TYPE2="CONFIRM";
	ArrayList<MaintenanceType> list=new ArrayList<MaintenanceType>();
	
	private Handler handler = new Handler() {
		Gson gson=new Gson();
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.WORK_DETAILS_SUCCESS:
				String json=(String) msg.obj;
				Log.e("====",json);
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
			case StatusCode.MAINTENANCE_COMPLETE_SUCCESS:
				
				String maintenanceJson=(String)msg.obj ;//提交电梯完成状态后，后台返回的信息
				try {
					JSONObject jsonObject=new JSONObject(maintenanceJson);
					String updateMsg=jsonObject.getString("msg");//该电梯完成状态是否已经更新，0，表示更新失败，1表示更新成功
					boolean completeProgress=jsonObject.getBoolean("success");//该电梯所在的任务包中，是否还有未完成的电梯，true代表全部完成，false代表还有未完成的
					if(updateMsg.equals("1")){
						currentElevatorIsFinish=true;
					}else{
						currentElevatorIsFinish=false;
					}
					if(completeProgress){
						Intent intent =new Intent(QueryTaskListActivity.this,QuestionResponseActivity.class);
						startActivity(intent);
					}else{
						Utilities.showToast("你还有未完成的电梯", context);
						finish();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case StatusCode.MAINTENENCE_COMPLETE_FAILED:
				Utilities.showToast((String)msg.obj, context);
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
	/**
	 * 前一个页面传过来的值
	 */
	private void getExtraData() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mWorktype = bundle.getString(Constant.WORKTYPE,"").trim();
		mZonePhone=bundle.getString(Constant.ZONEPHONE,"");
		mTaskNo=bundle.getString(Constant.TASKNO);
		mElevatorNo=bundle.getString(Constant.ELEVATORNO);
	}
	private void getData(){
		startProgressDialog("正在加载...");
		Param param = new Param(Constant.WORKTYPE, mWorktype);
		Request request = httpEngine.createRequest(ServicesConfig.WORK_TYPE, param);
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
							if(!currentElevatorIsFinish){
								Param taskNoParam = new Param(Constant.TASKNO,mTaskNo);
								Param elevatorNoParam=new Param(Constant.ELEVATORNO,mElevatorNo);
								startLoading(taskNoParam,elevatorNoParam);
							}else{
								Utilities.showToast("该电梯已经完成", context);
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

	protected void startLoading(Param... params) {
		Request request=httpEngine.createRequest(ServicesConfig.MAINTENCE_LIST_COMPLETE, params);
		Call call=httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {
			
			@Override
			public void onResponse(Response response) throws IOException {
				Message msg=new Message();
				if(response.isSuccessful()){
					msg.what=StatusCode.MAINTENANCE_COMPLETE_SUCCESS;
					msg.obj=response.body().string();
				}else{
					msg.what=StatusCode.MAINTENENCE_COMPLETE_FAILED;
					msg.obj="服务器响应失败";
				}
				handler.sendMessage(msg);
			}
			
			@Override
			public void onFailure(Request arg0, IOException arg1) {
				Message msg=new Message();
				msg.what=StatusCode.RESPONSE_NET_FAILED;
				handler.sendMessage(msg);
			}
		});
		
	}
}
