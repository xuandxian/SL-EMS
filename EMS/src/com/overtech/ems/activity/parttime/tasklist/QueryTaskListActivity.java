package com.overtech.ems.activity.parttime.tasklist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar.OnMenuItemClickListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.MyApplication;
import com.overtech.ems.activity.adapter.TaskListDetailsAdapter;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.entity.parttime.MaintenanceType;
import com.overtech.ems.http.HttpConnector;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;

/*
 * 维保清单列表
 *
 * 维保人员通过扫描二维码开启工作，将用户名和电梯编号发送到后台进行校验，如果该电梯是当天的维保任务，并且也有维保搭档，则可以开始工作，
 * 然后为防止维保人员没有到维保地点就扫描开始工作，再加上维保地点经纬度的判断，500米范围之内 需要做的事情：和搭档之间是不是第一次开始该电梯的维保工作
 * 点击的时候该电梯是否已经完成
 * 
 * @author Overtech Will
 * 
 */

public class QueryTaskListActivity extends BaseActivity implements
		OnClickListener {
	private Toolbar toolbar;
	private ActionBar actionBar;
	private AppCompatTextView tvTitle;
	private AppCompatTextView tvElevatorAddress;
	private double mLatitude;
	private double mLongitude;
	private LatLng mCurrentLocation;
	private String sWorkType;
	private String sZonePhone;
	private String sTaskNo;
	private String sElevatorNo;
	private String sElevatorAddress;
	private boolean currentElevatorIsFinish;// 当前电梯的完成状态
	private ListView lvTaskList;
	private View mListFooterView;
	private Button btDone;
	private TaskListDetailsAdapter adapter;
	private TextView tvTaskDetailsTitle;
	private final String TYPE1 = "CALL_PHONE";
	private final String TYPE2 = "CONFIRM";
	private String TAG = "24梯";
	private QueryTaskListActivity activity;
	private String uid;
	private String certificate;
	ArrayList<MaintenanceType> list = new ArrayList<MaintenanceType>();

	@Override
	protected int getLayoutResIds() {
		// TODO Auto-generated method stub
		return R.layout.activity_task_details;
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		activity = this;
		init();
		getExtraDataAndValidate();
	}

	private void init() {
		stackInstance.pushActivity(activity);
		uid = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.UID, "");
		certificate = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.CERTIFICATED, "");
		Intent intent = getIntent();
		sElevatorNo = intent.getStringExtra(Constant.ELEVATORNO);

		mLatitude = ((MyApplication) getApplication()).latitude;
		mLongitude = ((MyApplication) getApplication()).longitude;
		mCurrentLocation = new LatLng(mLatitude, mLongitude);

		mListFooterView = LayoutInflater.from(activity).inflate(
				R.layout.listview_footer_done, null);
		btDone = (Button) mListFooterView.findViewById(R.id.btn_tasklist_done);
		tvElevatorAddress = (AppCompatTextView) findViewById(R.id.tv_elevator_address);
		tvTaskDetailsTitle = (TextView) findViewById(R.id.tv_task_detail_title);
		toolbar = (Toolbar) findViewById(R.id.toolBar);
		setSupportActionBar(toolbar);
		actionBar = getSupportActionBar();
		tvTitle = (AppCompatTextView) findViewById(R.id.tvTitle);
		lvTaskList = (ListView) findViewById(R.id.lv_task_details);
		lvTaskList.addFooterView(mListFooterView);

		toolbar.setNavigationOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				stackInstance.popActivity(activity);
			}
		});
		toolbar.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem menuItem) {
				// TODO Auto-generated method stub
				switch (menuItem.getItemId()) {
				case R.id.menu_phone:
					showDialog(TYPE1, "您确认要拨打技术支持电话？");
					break;

				default:
					break;
				}
				return true;
			}
		});
		tvTitle.setText("维保清单");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		btDone.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		toolbar.inflateMenu(R.menu.menu_phone);
		return super.onCreateOptionsMenu(menu);
	}

	private void getExtraDataAndValidate() {// 验证维保数据，要求
		startProgressDialog(getResources().getString(
				R.string.loading_public_default));
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put(Constant.QRCODE, sElevatorNo);
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20055, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				Map<String, Object> currentElevator = null;
				String isMeetRequire = response.body.get("isMeetRequire")
						.toString();// 是否满足维保要求
				if (TextUtils.equals(isMeetRequire, "1")) {// 满足
					String feedbacked = response.body.get("feedbacked")
							.toString();// 是否已经完成该电梯
					// 0还没做
					// 1已经做了
					List<Map<String, Object>> result = (List<Map<String, Object>>) response.body
							.get("data");
					for (int i = 0; i < result.size(); i++) {// 遍历电梯编号，得到当前电梯正在维保的电梯
						if (sElevatorNo.equals(result.get(i).get("elevatorNo")
								.toString())) {
							currentElevator = result.get(i);
						}
					}
					double latitude = Double.parseDouble(currentElevator.get(
							"latitude").toString());
					double longitude = Double.parseDouble(currentElevator.get(
							"longitude").toString());
					LatLng latlng = new LatLng(latitude, longitude);
					double distance = DistanceUtil.getDistance(
							mCurrentLocation, latlng);
					if (distance > 500.0) {
						Utilities.showToast("您距离维保电梯的距离超出范围", activity);
						stackInstance.popActivity(activity);
					} else {
						if (TextUtils.equals(feedbacked, "1")) {
							Utilities.showToast("你已经完成了该电梯", activity);
							stackInstance.popActivity(activity);
						} else {
							sTaskNo = currentElevator.get("taskNo").toString();
							sWorkType = currentElevator.get("workType")
									.toString();
							sZonePhone = currentElevator.get("zonePhone")
									.toString();
							sElevatorAddress = currentElevator.get("address")
									.toString();
							tvElevatorAddress.setText("地址：" + sElevatorAddress);
							showNiffyDialog(sTaskNo, sElevatorAddress,
									sWorkType, sZonePhone);
						}
					}
				} else {
					Utilities.showToast("您尚未满足维保要求", activity);// 维保要求包括，维保时间正确，有维保搭档，维保电梯正确
					stackInstance.popActivity(activity);
				}
			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub
			}

			@Override
			public void bizStIs1Deal(Bean response) {
				// TODO Auto-generated method stub
			}

			@Override
			public void stopDialog() {
				// TODO Auto-generated method stub
				stopProgressDialog();
			}
		};
		conn.sendRequest();
	}

	// 根据维保类型，获取维保任务列表
	private void getMaintenanceTaskListData(String taskNo, String workType,
			String zonePhone) {
		startProgressDialog(getResources().getString(
				R.string.loading_public_default));
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put(Constant.WORKTYPE, workType);
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20060, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				List<String> tempList = (List<String>) response.body
						.get("data");
				list.add(new MaintenanceType("0", "Title", "content"));
				for (int i = 0; i < tempList.size(); i++) {
					String allString = tempList.get(i);
					String[] data = allString.split("\\|");
					MaintenanceType type = new MaintenanceType(
							String.valueOf(i + 1), data[0], data[1]);
					list.add(type);
				}
				tvTaskDetailsTitle.setVisibility(View.VISIBLE);
				if (adapter == null) {
					adapter = new TaskListDetailsAdapter(activity, list);
					lvTaskList.setAdapter(adapter);
				} else {
					adapter.setData(list);
					adapter.notifyDataSetChanged();
				}
			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub
			}

			@Override
			public void bizStIs1Deal(Bean response) {
				// TODO Auto-generated method stub
			}

			@Override
			public void stopDialog() {
				// TODO Auto-generated method stub
				stopProgressDialog();
			}
		};
		conn.sendRequest();
	}

	private void showNiffyDialog(final String taskNo, final String address,
			final String workType, final String zonePhone) {
		alertBuilder
				.setTitle("温馨提示")
				.setMessage(
						"电梯信息：" + address + "\n\n"
								+ "请将电梯监测设备按钮调至维保状态后开始进行维保工作")
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						getMaintenanceTaskListData(taskNo, workType, zonePhone);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						stackInstance.popActivity(activity);
					}
				}).show();
	}

	private void showDialog(final String type, String message) {
		alertBuilder
				.setTitle("温馨提示")
				.setMessage(message)
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (TextUtils.equals(type, TYPE1)) {
							Intent intent = new Intent(Intent.ACTION_CALL, Uri
									.parse("tel:" + sZonePhone));
							startActivity(intent);
						} else {
							if (!currentElevatorIsFinish) {
								confirmPerformTask();
							} else {
								Utilities.showToast("该电梯已经完成", activity);
							}
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				}).show();
	}

	private void confirmPerformTask() {
		startProgressDialog(getResources().getString(
				R.string.loading_public_default));
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put(Constant.ELEVATORNO, sElevatorNo);
		body.put(Constant.TASKNO, sTaskNo);
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20056, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				String updateMsg = response.body.get("updateStatus").toString();// 该电梯完成状态是否已经更新，0，表示更新失败，1表示更新成功
				String isAllCompleted = response.body.get("isAllCompleted")
						.toString();// 对于维保的单台电梯，true代表该电梯两人都完成，false代表尚未完成或者有一人完成
				String taskStatus = response.body.get("taskStatus").toString();
				if (updateMsg.equals("1")) {
					currentElevatorIsFinish = true;
				} else {
					currentElevatorIsFinish = false;
				}
				Intent intent = new Intent(QueryTaskListActivity.this,
						QuestionResponseActivity.class);
				intent.putExtra(Constant.TASKNO, sTaskNo);
				intent.putExtra(Constant.ELEVATORNO, sElevatorNo);
				startActivity(intent);
				stackInstance.popActivity(activity);

//				if (TextUtils.equals(isAllCompleted, "1")) {// 该业务逻辑暂不需要
//					if (TextUtils.equals("0", taskStatus)) { // 任务包中还有未完成的
//						Utilities.showToast("您还有未完成的电梯", activity); // TODO
//						Intent intent = new Intent(QueryTaskListActivity.this,
//								QuestionResponseActivity.class);
//						intent.putExtra(Constant.TASKNO, sTaskNo);
//						intent.putExtra(Constant.ELEVATORNO, sElevatorNo);
//						startActivity(intent);
//						stackInstance.popActivity(activity);
//					} else { // 任务包中全部都完成了 // TODO
//						Intent intent = new Intent(QueryTaskListActivity.this,
//								EvaluationActivity.class);
//						intent.putExtra(Constant.TASKNO, sTaskNo);
//						intent.putExtra(Constant.ELEVATORNO, sElevatorNo);
//						startActivity(intent);
//						stackInstance.popActivity(activity);
//					}
//				} else {
//					Utilities.showToast("请和搭档确认电梯的完成状态", activity);
//					Intent intent = new Intent(QueryTaskListActivity.this,
//							TaskListPackageDetailActivity.class);
//					intent.putExtra(Constant.TASKNO, sTaskNo); //
//					intent.putExtra(Constant.ELEVATORNO, mElevatorNo);
//					startActivity(intent);
//					stackInstance.popActivity(activity);
//				}

			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub
			}

			@Override
			public void bizStIs1Deal(Bean response) {
				// TODO Auto-generated method stub
				Utilities.showToast(response.msg, activity);
			}

			@Override
			public void stopDialog() {
				// TODO Auto-generated method stub
				stopProgressDialog();
			}
		};
		conn.sendRequest();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_tasklist_done:
			showDialog(TYPE2, "请确认维保工作已完成，并将电梯监测设备按钮调至正常状态!!!");
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		stackInstance.popActivity(activity);
	}

}
