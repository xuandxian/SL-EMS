package com.overtech.ems.activity.parttime.tasklist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.baidu.mapapi.utils.route.RouteParaOption.EBusStrategyType;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.MyApplication;
import com.overtech.ems.activity.adapter.TaskListPackageDetailAdapter;
import com.overtech.ems.activity.parttime.common.ElevatorDetailActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.http.HttpConnector;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.AppUtils;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;

/*
 *任务包详情(任务单模块)
 * 
 */
public class TaskListPackageDetailActivity extends BaseActivity implements
		OnRefreshListener, OnClickListener {
	private ImageView ivBack;
	private ListView lvTask;
	private Button btChargeback;
	private Button btNext;
	private Button btASComplete;
	private TextView tvTaskPackageName;
	private TextView tvTaskNo;
	private AppCompatTextView tvNavigation;
	private AppCompatTextView tvQrcode;
	private AppCompatTextView tvShare;
	private AppCompatTextView tvPartner;
	private String sPartnerPhone;
	private String sPartnerName;
	private String sTaskPackageName;
	private String latitude; // 纬度
	private String longitude; // 经度
	private String maintenanceDate; // 维保日期
	private String sZone;
	private String sTaskNo;
	private String isAs;// 是否是年检包
	private String isMainAs;// 是否是主年检
	/**
	 * 如果已经请求到收藏的搭档列表，就不需要重复请求了
	 */
	private boolean hasLoadPartners = false;
	private List<Map<String, Object>> listPartners;// 搭档
	private String[] partners;// 搭档集合
	private LatLng llStartPoint;
	private LatLng llDestination;
	// private String mDesName;
	private boolean isToday;
	private SwipeRefreshLayout mSwipeLayout;
	private TaskListPackageDetailAdapter adapter;
	private List<Map<String, Object>> list;
	private final String ALLCOMPLETE = "allComplete";
	private final String NOTCOMPLETE = "notComplete";
	private String uid;
	private String certificate;
	private TaskListPackageDetailActivity activity;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.MSG_SET_TAGS:
				Log.d("24梯", "Set tags in handler.");
				JPushInterface.setAliasAndTags(getApplicationContext(), null,
						(Set<String>) msg.obj, mTagsCallback);
				break;
			default:
				break;
			}
		};
	};

	private final TagAliasCallback mTagsCallback = new TagAliasCallback() {

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			stopProgressDialog();
			String logs;
			switch (code) {
			case 0:
				logs = "Set tag and alias success";
				Logr.e(logs);
				stackInstance.popActivity(activity);
				break;
			case 6002:
				logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
				Logr.e(logs);
				if (AppUtils.isConnected(getApplicationContext())) {
					handler.sendMessageDelayed(handler.obtainMessage(
							StatusCode.MSG_SET_TAGS, tags), 1000 * 60);
				} else {
					Logr.e("No network");
				}
				break;
			default:
				logs = "Failed with errorCode = " + code;
				Logr.e(logs);
			}
		}
	};

	@Override
	protected int getLayoutResIds() {
		// TODO Auto-generated method stub
		return R.layout.activity_tasklist_package_detail;
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		activity = TaskListPackageDetailActivity.this;
		stackInstance.pushActivity(activity);
		initView();
		initEvent();
		getExtraDataAndInit();
	}

	private void initView() {
		llStartPoint = new LatLng(
				((MyApplication) getApplicationContext()).latitude,
				(((MyApplication) getApplicationContext())).longitude);
		ivBack = (ImageView) findViewById(R.id.iv_grab_headBack);
		tvNavigation = (AppCompatTextView) findViewById(R.id.tv_navigation);
		tvQrcode = (AppCompatTextView) findViewById(R.id.tv_qrcode);
		tvShare = (AppCompatTextView) findViewById(R.id.tv_share);
		tvPartner = (AppCompatTextView) findViewById(R.id.tv_partner);
		lvTask = (ListView) findViewById(R.id.lv_tasklist);
		btChargeback = (Button) findViewById(R.id.bt_cancle_task);
		btNext = (Button) findViewById(R.id.bt_next_response);
		btASComplete = (Button) findViewById(R.id.bt_as_complete);

		tvTaskPackageName = (TextView) findViewById(R.id.tv_headTitle_community_name);
		tvTaskNo = (TextView) findViewById(R.id.tv_headTitle_taskno);
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.srl_container);
	}

	private void getExtraDataAndInit() {
		Intent intent = getIntent();
		sTaskNo = intent.getStringExtra(Constant.TASKNO);
		isAs = intent.getStringExtra("isAs");
		uid = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.UID, "");
		certificate = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.CERTIFICATED, "");
		mSwipeLayout.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mSwipeLayout.setRefreshing(true);
			}
		});
		if (TextUtils.equals(isAs, "1")) {
			tvPartner.setVisibility(View.VISIBLE);
			tvShare.setVisibility(View.GONE);
			tvQrcode.setEnabled(false);
		} else {
			// 不好确定，可能有，可能没有
			// requestPartners();
		}
		onRefresh();
	}

	private void initEvent() {
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorSchemeResources(R.color.material_deep_teal_200,
				R.color.material_deep_teal_500);
		ivBack.setOnClickListener(this);
		tvNavigation.setOnClickListener(this);
		tvQrcode.setOnClickListener(this);
		tvShare.setOnClickListener(this);
		tvPartner.setOnClickListener(this);
		btChargeback.setOnClickListener(this);
		btNext.setOnClickListener(this);
		btASComplete.setOnClickListener(this);
		lvTask.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Map<String, Object> detail = (Map<String, Object>) parent
						.getItemAtPosition(position);
				Intent intent = new Intent(activity,
						ElevatorDetailActivity.class);
				intent.putExtra(Constant.ELEVATORNO, detail.get("elevatorNo")
						.toString());
				startActivity(intent);
			}
		});
	}

	private void share() {// 分享给好友
		alertBuilder
				.setTitle("分享")
				.setMessage("分享任务单给你的搭档或者朋友")
				.setNeutralButton("搭档", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (partners != null && partners.length > 0) {
							shareToPartners();
						} else {
							Utilities.showToast("暂无搭档", activity);
						}
					}
				})
				.setPositiveButton("微信QQ等",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								shareToWeChatOrQQ();
							}
						}).show();

	}

	private void shareToPartners() {
		final List<HashMap<String, Object>> selectPartners = new ArrayList<HashMap<String, Object>>();

		alertBuilder
				.setTitle("搭档")
				.setMultiChoiceItems(partners, null,
						new OnMultiChoiceClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								// TODO Auto-generated method stub
								if (isChecked) {
									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("uid",
											listPartners.get(which).get("uid"));
									selectPartners.add(which, map);
								} else {
									selectPartners.remove(which);
								}
							}
						})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				})
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						sharePartnersConfirm(selectPartners);
					}
				}).show();
	}

	private void sharePartnersConfirm(List<HashMap<String, Object>> data) {
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put("taskNo", sTaskNo);
		body.put("partners", data);
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20061, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				Utilities.showToast(response.msg, activity);
			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void bizStIs1Deal() {
				// TODO Auto-generated method stub

			}

			@Override
			public void stopDialog() {
				// TODO Auto-generated method stub

			}
		};
		conn.sendRequest();
	}

	private void shareToWeChatOrQQ() {
		ShareSDK.initSDK(activity);
		OnekeyShare oks = new OnekeyShare();
		oks.setText("我在24T中抢到"
				+ sZone
				+ "的一个维保单，单号为:"
				+ sTaskNo
				+ ",请速度去抢哦！App下载链接：http://www.wandoujia.com/apps/com.overtech.ems");
		oks.setUrl("http://www.wandoujia.com/apps/com.overtech.ems");
		oks.setVenueName("24T");
		oks.show(activity);
	}

	public void startNavicate(LatLng startPoint, LatLng endPoint, String endName) {
		RouteParaOption para = new RouteParaOption().startName("我的位置")
				.startPoint(startPoint).endPoint(endPoint).endName("终点")
				.busStrategyType(EBusStrategyType.bus_recommend_way);
		try {
			BaiduMapRoutePlan.setSupportWebRoute(true);
			BaiduMapRoutePlan.openBaiduMapTransitRoute(para, activity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put(Constant.TASKNO, sTaskNo);
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20051, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				list = (List<Map<String, Object>>) response.body.get("data");
				sTaskPackageName = response.body.get("taskPackageName")
						.toString();
				latitude = response.body.get("latitude").toString();
				longitude = response.body.get("longitude").toString();
				llDestination = new LatLng(Double.valueOf(latitude),
						Double.valueOf(longitude));
				sZone = response.body.get("zone").toString();
				maintenanceDate = response.body.get("maintenanceDate")
						.toString();
				isAs = response.body.get("isAs").toString();
				if (TextUtils.equals(isAs, "1")) {// 年检包
					tvPartner.setVisibility(View.VISIBLE);// 搭档可用
					tvShare.setVisibility(View.GONE);// 分享不可用

					String isMainAs = response.body.get("isMainAs").toString();
					String isAsTimeReach = response.body.get("isAsTimeReach")
							.toString();
					if (TextUtils.equals("1", isMainAs)
							&& TextUtils.equals("1", isAsTimeReach)) {
						btASComplete.setVisibility(View.VISIBLE);
						btChargeback.setVisibility(View.GONE);
						btNext.setVisibility(View.GONE);
					} else {
						btASComplete.setVisibility(View.GONE);
						btChargeback.setVisibility(View.GONE);
						btNext.setVisibility(View.GONE);
					}
					if (!hasLoadPartners) {
						requestLoadASPartners();
					}
				} else if (TextUtils.equals(isAs, "0")) {// 普通包
					sPartnerPhone = response.body.get("partnerPhone")
							.toString();
					sPartnerName = response.body.get("partnerName").toString();
					if (TextUtils.isEmpty(sPartnerName)
							|| TextUtils.isEmpty(sPartnerPhone)) {
						tvPartner.setVisibility(View.GONE);
						tvShare.setVisibility(View.VISIBLE);
						if (!hasLoadPartners) {
							requestPartners();
						}
					} else {
						tvShare.setVisibility(View.GONE);
						tvPartner.setVisibility(View.VISIBLE);
					}

					int count = 0;// 记录完成电梯的数量
					for (Map<String, Object> data : list) {
						if (data.get("isFinish").equals("2")) {
							count++;
						}
					}
					if (count == list.size()) {
						btNext.setVisibility(View.VISIBLE);
						btNext.setBackgroundResource(R.color.btn_visiable_bg);// 绿色
						btNext.setTag(ALLCOMPLETE);
						btChargeback.setVisibility(View.GONE);
					} else {
						isToday = Utilities.isToday(maintenanceDate);
						Logr.e("==是不是当天==" + isToday);
						if (isToday) {
							btNext.setVisibility(View.VISIBLE);
							btNext.setBackgroundResource(R.color.btn_disable_bg);// 灰色
							btNext.setTag(NOTCOMPLETE);
							btChargeback.setVisibility(View.GONE);
						} else {
							btNext.setVisibility(View.GONE);
							btChargeback.setVisibility(View.VISIBLE);
						}
					}

				}
				tvTaskNo.setText(sTaskNo);
				tvTaskPackageName.setText(sTaskPackageName);
				if (null == list || list.size() == 0) {
					Utilities
							.showToast(
									getResources().getString(
											R.string.response_no_data),
									activity);
					btChargeback.setVisibility(View.GONE);
					btNext.setVisibility(View.GONE);
				} else {
					if (adapter == null) {
						adapter = new TaskListPackageDetailAdapter(activity,
								list);
						lvTask.setAdapter(adapter);
					} else {
						adapter.setData(list);
					}

				}
			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub
			}

			@Override
			public void bizStIs1Deal() {
				// TODO Auto-generated method stub
			}

			@Override
			public void stopDialog() {
				// TODO Auto-generated method stub
				if (mSwipeLayout.isRefreshing()) {
					mSwipeLayout.setRefreshing(false);
				}
			}
		};
		conn.sendRequest();
	}

	private void validateChargebackTime() {
		startProgressDialog(getResources().getString(
				R.string.loading_public_default));
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put(Constant.TASKNO, sTaskNo);
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20052, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				String result = response.body.get("result").toString();
				if (result.equals("-1")) {
					Utilities.showToast(response.msg, activity);
					return;
				} else if (result.equals("0")) {
					Utilities.showToast(response.msg, activity);
					return;
				} else if (result.equals("1")) {
					alertBuilder.setMessage("72小时内退单会影响星级评定，你确认要退单？");
				} else {
					alertBuilder.setMessage("你确认要退单？");
				}
				alertBuilder
						.setTitle("温馨提示")
						.setPositiveButton("确认",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										chargeback();
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub

									}
								}).show();
			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub
			}

			@Override
			public void bizStIs1Deal() {
				// TODO Auto-generated method stub
			}

			@Override
			public void stopDialog() {
				// TODO Auto-generated method stub
				stopProgressDialog();
				if (mSwipeLayout.isRefreshing()) {
					mSwipeLayout.setRefreshing(false);
				}
			}
		};
		conn.sendRequest();
	}

	private void requestLoadASPartners() {
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put("taskNo", sTaskNo);
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20062, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				hasLoadPartners = true;
				listPartners = (List<Map<String, Object>>) response.body
						.get("partners");
				partners = new String[listPartners.size()];
				for (int i = 0; i < listPartners.size(); i++) {
					if (listPartners.get(i).get("isMainAs").equals("1")) {
						partners[i] = listPartners.get(i).get("partnerName")
								.toString()
								+ "(主修)";
					} else {
						partners[i] = listPartners.get(i).get("partnerName")
								.toString();
					}
				}
			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub
			}

			@Override
			public void bizStIs1Deal() {
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

	private void requestPartners() {
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20079, uid,
				certificate, null) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				hasLoadPartners = true;
				listPartners = (List<Map<String, Object>>) response.body
						.get("data");
				partners = new String[listPartners.size()];
				for (int i = 0; i < listPartners.size(); i++) {
					partners[i] = listPartners.get(i).get("name").toString();
				}
			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void bizStIs1Deal() {
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

	private void chargeback() {
		startProgressDialog("正在退单...");
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put(Constant.TASKNO, sTaskNo);
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20053, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				loadNotDoneTask();
			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void bizStIs1Deal() {
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_grab_headBack:
			stackInstance.popActivity(activity);
			break;
		case R.id.bt_cancle_task:
			Logr.e("退单验证时间==");
			validateChargebackTime();
			break;
		case R.id.bt_next_response:
			if (btNext.getTag().equals(ALLCOMPLETE)) {
				Intent intent = new Intent(TaskListPackageDetailActivity.this,
						EvaluationActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(Constant.TASKNO, sTaskNo);
				intent.putExtras(bundle);
				startActivity(intent);
			} else if (btNext.getTag().equals(NOTCOMPLETE)) {
				Utilities.showToast("尚有未完成的电梯", activity);
			}
			break;
		case R.id.bt_as_complete:
			asComplete();
			break;
		case R.id.tv_navigation:
			startNavicate(llStartPoint, llDestination, "终点");
			break;
		case R.id.tv_qrcode:
			Intent intent = new Intent();
			intent.setClass(activity, ScanCodeActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_share:
			share();
			break;
		case R.id.tv_partner:
			if (TextUtils.equals(isAs, "1")) {
				dialToASPartner();
			} else {
				dialToPartner();
			}
			break;
		}
	}

	private void asComplete() {// 年检完成
		startProgressDialog("加载中...");
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put("taskNo", sTaskNo);
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20065, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				Utilities.showToast(response.msg, activity);

			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub
			}

			@Override
			public void bizStIs1Deal() {
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

	private void dialToASPartner() {
		alertBuilder
				.setTitle("年检成员")
				.setMessage("请选择你要联系的搭档")
				.setSingleChoiceItems(partners, -1,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						})
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String asPartner = listPartners.get(which)
								.get("partnerPhone").toString();
						Intent intent = new Intent(Intent.ACTION_CALL, Uri
								.parse("tel:" + asPartner));
						startActivity(intent);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				}).show();
	}

	private void dialToPartner() {
		// TODO Auto-generated method stub
		alertBuilder
				.setTitle("温馨提示")
				.setMessage("您确认要拨打电话给您的搭档：" + sPartnerName)
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(Intent.ACTION_CALL, Uri
								.parse("tel:" + sPartnerPhone));
						startActivity(intent);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				}).show();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		stackInstance.popActivity(activity);
	}

	/**
	 * 加载未完成的任务单
	 */
	private void loadNotDoneTask() {
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20050, uid,
				certificate, null) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				stopProgressDialog();
				List<Map<String, Object>> datas = (List<Map<String, Object>>) response.body
						.get("data");
				Set<String> tempSet = new HashSet<String>();
				for (Map<String, Object> data : datas) {
					String sTaskNo = (String) data.get("taskNo");
					tempSet.add(sTaskNo);
				}
				JPushInterface.setAliasAndTags(activity, "", tempSet,
						mTagsCallback);
			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void bizStIs1Deal() {
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

}
