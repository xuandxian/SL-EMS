package com.overtech.ems.activity.parttime.tasklist;

import java.io.IOException;
import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.baidu.mapapi.utils.route.RouteParaOption.EBusStrategyType;
import com.google.gson.Gson;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.adapter.TaskListPackageDetailAdapter;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.bean.TaskPackageDetailBean;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.entity.parttime.TaskPackageDetail;
import com.overtech.ems.http.HttpEngine.Param;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.dialogeffects.Effectstype;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class TaskListPackageDetailActivity extends BaseActivity implements OnRefreshListener {
	private ImageView mDoBack;
	private ListView mTask;
	private Button mCancle;
	private TextView mTaskPackageName;
	private TextView mTaskNo;
	private String mPhone;
	private String mZonePhone;
	private String taskNo;
	private String mLoginName;
	private Context mActivity;
	private Effectstype effect;
	private ImageView mDoMore;
	private PopupWindow popupWindow;
	private LatLng mStartPoint;
	private LatLng destination;
	private String mDesName;
	
	private SwipeRefreshLayout mSwipeLayout;
	private TaskListPackageDetailAdapter adapter;
	private ArrayList<TaskPackageDetail> list;
	/**
	 * 列表弹窗的间隔
	 */
	protected final int LIST_PADDING = 10;
	/**
	 * 实例化一个矩形
	 */
	private Rect mRect = new Rect();
		
	/**
	 * 坐标的位置（x、y）
	 */
	private final int[] mLocation = new int[2];
		
	/**
	 * 屏幕的宽度
	 */
	private int mScreenWidth;
	/**
	 * 屏幕的高度
	 */
	private int mScreenHeight;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.PACKAGE_DETAILS_SUCCESS:
				String json=(String) msg.obj;
				Gson gson=new Gson();
				TaskPackageDetailBean bean=gson.fromJson(json, TaskPackageDetailBean.class);
				list=(ArrayList<TaskPackageDetail>)bean.getModel();
				mPhone=bean.getPartnerPhone();
				mZonePhone=bean.getZonePhone();
				if(null==list||list.size()==0){
					Utilities.showToast("无数据", mActivity);
				}else{
					adapter = new TaskListPackageDetailAdapter(context, list);
					mTask.setAdapter(adapter);
				}
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast((String)msg.obj, context);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast((String)msg.obj, context);
			default:
				break;
			}
			mSwipeLayout.setRefreshing(false);
		};
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tasklist_package_detail);
		initView();
		initData();
		initEvent();
	}

	private void initEvent() {
		mActivity = TaskListPackageDetailActivity.this;
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,  
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
		mDoBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mTask.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(adapter.isAllCompleted()){
					Intent intent =new Intent(context,QuestionResponseActivity.class);
					startActivity(intent);
				}else{
					TaskPackageDetail detail =(TaskPackageDetail)parent.getItemAtPosition(position);
					String workType=detail.getWorkType();
					if(detail.getIsFinish().equals("1")){
						Utilities.showToast("你好，该电梯已经完成", mActivity);
					}else{
						Intent intent = new Intent(context, QueryTaskListActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString(Constant.WORKTYPE, workType);
						bundle.putString(Constant.ZONEPHONE, mZonePhone);
						bundle.putString(Constant.TASKNO, taskNo);
						bundle.putString(Constant.ELEVATORNO, detail.getElevatorNo());
						intent.putExtras(bundle);
						startActivityForResult(intent, StatusCode.RESULT_TASKLIST_PACKAGEDETAIL);
					}
				}
			}
		});
		mCancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				effect = Effectstype.Slideright;
				dialogBuilder.withTitle("温馨提示")
						.withTitleColor(R.color.main_primary)
						.withDividerColor("#11000000").withMessage("您是否要退掉此单？")
						.withMessageColor(R.color.main_primary)
						.withDialogColor("#FFFFFFFF")
						.isCancelableOnTouchOutside(true).withDuration(700)
						.withEffect(effect)
						.withButtonDrawable(R.color.main_white)
						.withButton1Text("否")
						.withButton1Color(R.color.main_primary)
						.withButton2Text("是")
						.withButton2Color(R.color.main_primary)
						.setButton1Click(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialogBuilder.dismiss();
							}
						}).setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
						progressDialog.setMessage("正在退单");
						progressDialog.show();
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								finish();
							}
						}, 3000);
					}
				}).show();
			}
		});
		initPopupWindow();
		mDoMore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showPopupWindow(v);
			}
		});
	}
	private void initPopupWindow() {
		mScreenWidth=context.getResources().getDisplayMetrics().widthPixels;
		mScreenHeight=context.getResources().getDisplayMetrics().heightPixels;
		popupWindow=new PopupWindow(mActivity);
		popupWindow.setWidth(mScreenWidth/2);
		popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
		popupWindow.setFocusable(true);
		popupWindow.setTouchable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setContentView(LayoutInflater.from(activity).inflate(R.layout.layout_tasklist_pop, null));
		initUI();
	}
	//对popupWindow的ui进行初始化
	private void initUI() {
		popupWindow.getContentView().findViewById(R.id.ll_pop_1).setOnClickListener(//地图导航
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						startNavicate(mStartPoint, destination, mDesName);
					}
				});
		popupWindow.getContentView().findViewById(R.id.ll_pop_2).setOnClickListener(//拨打搭档电话
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent=new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+mPhone));
						startActivity(intent);
					}
				});
	}
	//弹出popupWindow
	protected void showPopupWindow(View v) {
		
		v.getLocationOnScreen(mLocation);
		//设置矩形的大小
		mRect.set(mLocation[0], mLocation[1], mLocation[0] + v.getWidth(), mLocation[1] + v.getHeight());
		popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, mScreenWidth - LIST_PADDING - (popupWindow.getWidth() / 2), mRect.bottom);
	}

	public void startNavicate(LatLng startPoint, LatLng endPoint,String endName) {
		// 构建 route搜索参数
		RouteParaOption para = new RouteParaOption().startName("我的位置")
				.startPoint(startPoint)// 路线检索起点
				.endPoint(endPoint)// 路线检索终点
				.endName(endName)
				.busStrategyType(EBusStrategyType.bus_recommend_way);
		try {
			BaiduMapRoutePlan.setSupportWebRoute(true);
			BaiduMapRoutePlan.openBaiduMapTransitRoute(para, mActivity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initData() {
		Bundle bundle = getIntent().getExtras();
		mStartPoint = bundle.getParcelable(Constant.CURLOCATION);
		destination = bundle.getParcelable(Constant.DESTINATION);
		mDesName=bundle.getString(Constant.DESNAME);
		String taskPackage=bundle.getString(Constant.TASKPACKAGENAME);
		taskNo = bundle.getString(Constant.TASKNO);
		mTaskPackageName.setText(taskPackage);
		mTaskNo.setText(taskNo);
		Param param=new Param(Constant.TASKNO,taskNo);
		mLoginName=mSharedPreferences.getString(SharedPreferencesKeys.CURRENT_LOGIN_NAME, null);
		Param param2=new Param(Constant.LOGINNAME,mLoginName);
		startLoading(param,param2);
	}

	private void startLoading(Param... params) {
		Request request = httpEngine.createRequest(ServicesConfig.TASK_PACKAGE_DETAIL,params);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					msg.what = StatusCode.PACKAGE_DETAILS_SUCCESS;
					msg.obj = response.body().string();
				} else {
					msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
					msg.obj = "服务器异常";
				}
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(Request arg0, IOException arg1) {
				Message msg = new Message();
				msg.what = StatusCode.RESPONSE_NET_FAILED;
				msg.obj = "网络异常";
				handler.sendMessage(msg);
			}
		});
	}

	private void initView() {
		mDoBack = (ImageView) findViewById(R.id.iv_grab_headBack);
		mTask = (ListView) findViewById(R.id.lv_tasklist);
		mCancle = (Button) findViewById(R.id.bt_cancle_task);
		mDoMore = (ImageView) findViewById(R.id.iv_navicate_right);
		mDoMore.setBackgroundResource(R.drawable.icon_common_more);
		mDoMore.setVisibility(View.VISIBLE);
		mTaskPackageName=(TextView) findViewById(R.id.tv_headTitle_community_name);
		mTaskNo=(TextView) findViewById(R.id.tv_headTitle_taskno);
		mSwipeLayout=(SwipeRefreshLayout) findViewById(R.id.srl_container);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case StatusCode.RESULT_TASKLIST_PACKAGEDETAIL:
			onRefresh();
			break;

		default:
			break;
		}
	}
	@Override
	public void onRefresh() {
		Param param =new Param(Constant.TASKNO,taskNo);
		Param param2=new Param(Constant.LOGINNAME,mLoginName);
		startLoading(param,param2);
	}
}
