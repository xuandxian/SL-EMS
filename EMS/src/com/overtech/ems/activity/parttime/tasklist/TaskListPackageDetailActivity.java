package com.overtech.ems.activity.parttime.tasklist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
import com.overtech.ems.activity.adapter.PackageDetailAdapter;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.bean.TaskPackageDetailBean;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.entity.parttime.TaskPackageDetail;
import com.overtech.ems.http.HttpEngine.Param;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.dialogeffects.Effectstype;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class TaskListPackageDetailActivity extends BaseActivity {
	private ImageView mDoBack;
	private ListView mTask;
	private Button mCancle;
	private TextView mTaskPackageName;
	private TextView mTaskNo;
	private String mPhone;
	private String mZonePhone;
	private Context mActivity;
	private Effectstype effect;
	private ImageView mDoMore;
	private PopupWindow popupWindow;
	private LatLng mStartPoint;
	private LatLng destination;
	private String mDesName;
	private PackageDetailAdapter adapter;
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
				if(list!=null){
					adapter = new PackageDetailAdapter(context, list);
					mTask.setAdapter(adapter);
				}else{
					Utilities.showToast("试试重新打开该页面", mActivity);
				}
				break;
			case StatusCode.PACKAGE_DETAILS_FAILED:
				Utilities.showToast((String)msg.obj, mActivity);
				break;

			default:
				break;
			}
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
		mDoBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mTask.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TaskPackageDetail detail =(TaskPackageDetail)parent.getItemAtPosition(position);
				String workType=detail.getWorkType();
				Intent intent = new Intent(context, QueryTaskListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(Constant.WORKTYPE, workType);
				bundle.putString(Constant.ZONEPHONE, mZonePhone);
				intent.putExtras(bundle);
				startActivity(intent);
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
		String taskNo = bundle.getString(Constant.TASKNO);
		mTaskPackageName.setText(taskPackage);
		mTaskNo.setText(taskNo);
		Param param=new Param(Constant.TASKNO,taskNo);
		Request request = httpEngine
				.createRequest(ServicesConfig.TASK_PACKAGE_DETAIL,param);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					msg.what = StatusCode.PACKAGE_DETAILS_SUCCESS;
					msg.obj = response.body().string();
				} else {
					msg.what = StatusCode.PACKAGE_DETAILS_FAILED;
					msg.obj = "数据异常";
				}
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(Request arg0, IOException arg1) {
				Message msg = new Message();
				msg.what = StatusCode.PACKAGE_DETAILS_FAILED;
				msg.obj = "请检查网络";
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
	}

	
}
