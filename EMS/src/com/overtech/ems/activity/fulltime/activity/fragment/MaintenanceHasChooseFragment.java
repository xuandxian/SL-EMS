package com.overtech.ems.activity.fulltime.activity.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.fulltime.activity.MaintenanceComponentActivity;
import com.overtech.ems.activity.fulltime.activity.MaintenanceResponseActivity;
import com.overtech.ems.activity.fulltime.activity.MaintenanceTaskActivity;
import com.overtech.ems.activity.fulltime.adapter.MaintenanceHasChooseAdapter;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.http.HttpConnector;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.Utilities;

public class MaintenanceHasChooseFragment extends BaseFragment implements
		OnClickListener {
	public static final String ARGUMENT = "argument";
	private List<Map<String, Object>> datas;
	private RecyclerView recyclerView;
	private MaintenanceHasChooseAdapter adapter;
	private AppCompatButton btPrevious;
	private AppCompatButton btSubmit;
	private AppCompatButton btNext;
	private List<Map<String, Object>> selectDatas = new ArrayList<Map<String, Object>>();
	private MaintenanceTaskActivity activity;
	private String uid;
	private String certificate;
	private String isMain;
	private String workorderCode;
	private String hasReport;
	private String hasChooseComponent;
	private String hasSelectJson;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Logr.e("===hasChooseFragment=onCreate执行了=");
		Bundle bundle = getArguments();
		if (bundle != null) {
			selectDatas.clear();// 清空之前选择的信息
			hasSelectJson = bundle.getString(ARGUMENT);
			if (!TextUtils.isEmpty(hasSelectJson)) {
				Logr.e("获取到穿过来的参数了，并且参数中的值不为空");
				datas = gson.fromJson(hasSelectJson,
						new TypeToken<List<Map<String, Object>>>() {
						}.getType());
				for (int i = 0; i < datas.size(); i++) {
					Map<String, Object> firstLevel = datas.get(i);
					List<Map<String, Object>> secondLevel = (List<Map<String, Object>>) firstLevel
							.get("children");
					for (int j = 0; j < secondLevel.size(); j++) {
						Map<String, Object> thirdLevel = secondLevel.get(j);
						List<Map<String, Object>> finalLevel = (List<Map<String, Object>>) thirdLevel
								.get("children");
						for (Map<String, Object> m : finalLevel) {
							if (m.get("checked").equals("1")) {
								selectDatas.add(m);
							}
						}
					}
				}
			}
		}
		activity = (MaintenanceTaskActivity) getActivity();
		certificate = activity.getCertificate();
		uid = activity.getUid();
		isMain = activity.getIsMain();
		workorderCode = activity.getWorkorderCode();
		hasReport = activity.getHasReport();
		hasChooseComponent = activity.getHasChooseComponent();

		Logr.e("certificate==" + certificate + "uid=" + uid + "isMain="
				+ isMain + "=workorderCode=" + workorderCode + "=hasReport="
				+ hasReport + "=hasChooseComponent==" + hasChooseComponent);
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Logr.e("=hasChooseFragment=onCreateView===执行了");
		View v = inflater.inflate(R.layout.fragment_maintenance_has_choose,
				container, false);
		initView(v);
		initEvent();
		return v;

	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		Logr.e("==backstackentrycount=="
				+ fragmentManager.getBackStackEntryCount());
	}

	private void initEvent() {
		// TODO Auto-generated method stub
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		if (TextUtils.equals(hasReport, "1")) {
			btNext.setVisibility(View.VISIBLE);
			loadingHasReportDatas();
		} else {
			btSubmit.setVisibility(View.VISIBLE);
			if (adapter == null) {
				adapter = new MaintenanceHasChooseAdapter(activity, selectDatas);
				recyclerView.setAdapter(adapter);
			} else {
				adapter.setDatas(selectDatas);
				recyclerView.setAdapter(adapter);
			}
		}
		btPrevious.setOnClickListener(this);
		btSubmit.setOnClickListener(this);
		btNext.setOnClickListener(this);
	}

	private void initView(View v) {
		// TODO Auto-generated method stub
		recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
		btPrevious = (AppCompatButton) v.findViewById(R.id.bt_previous);
		btSubmit = (AppCompatButton) v.findViewById(R.id.bt_submit);
		btNext = (AppCompatButton) v.findViewById(R.id.bt_next);
	}

	private void loadingHasReportDatas() {
		startProgressDialog(getResources().getString(
				R.string.loading_public_default));
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put("workorderCode", workorderCode);
		Logr.e("==haha 我执行了==");
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20009, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				List<Map<String, Object>> lists = (List<Map<String, Object>>) response.body
						.get("data");
				if (adapter == null) {
					adapter = new MaintenanceHasChooseAdapter(activity, lists);
					recyclerView.setAdapter(adapter);
				} else {
					adapter.setDatas(lists);
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

	private void uploadMaintenanceData() {// 提交维修报告
		// TODO Auto-generated method stub
		for(Map<String,Object> m:datas){
			for(Map<String,Object> n:(List<Map<String,Object>>)m.get("children")){
				for(Map<String,Object> o:(List<Map<String,Object>>)n.get("children")){
					if(o.get("code").equals("101")){
						Logr.e("101乱码你出来==="+o.get("name"));
					}
				}
			}
		}
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put("workorderCode", workorderCode);
		body.put("isMain", isMain);
		body.put("data", datas);
		HttpConnector<Bean> callback = new HttpConnector<Bean>(20005, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				changeParts();
			}

			@Override
			public void bizStIs1Deal(Bean response) {
				// TODO Auto-generated method stub
				activity.stackInstance.popActivity(activity);
			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub
			}

			@Override
			public void stopDialog() {
				// TODO Auto-generated method stub
				stopProgressDialog();
			}
		};
		callback.sendRequest();
	}

	private void changeParts() {
		alertBuilder
				.setTitle("更换配件")
				.setMessage("是否需要更换配件？")
				.setNegativeButton("否", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						startProgressDialog("正在请求中...");
						changePartsConfirm("-1");//不申请配件
					}
				})
				.setPositiveButton("是", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						startProgressDialog("正在申请配件...");
						changePartsConfirm("0");//申请配件
					}
				}).show();
	}

	private void changePartsConfirm(final String requestComponent) {
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put("workorderCode", workorderCode);
		body.put("requestComponent", requestComponent);
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20007, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				if(TextUtils.equals(requestComponent, "0")){
					toPcHandle();
				}else if(TextUtils.equals(requestComponent, "-1")){
					Intent intent = new Intent(activity,
							MaintenanceResponseActivity.class);
					intent.putExtra(Constant.WORKORDERCODE, workorderCode);
					intent.putExtra("isMain", isMain);
					startActivity(intent);
				}else{
					Utilities.showToast("配件申请已经下来了",activity );//1
				}
			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void bizStIs1Deal(Bean response) {
				// TODO Auto-generated method stub
				activity.stackInstance.popActivity(activity);
			}

			@Override
			public void stopDialog() {
				// TODO Auto-generated method stub
				stopProgressDialog();
			}
		};
		conn.sendRequest();
	}

	public void toPcHandle() {
		alertBuilder.setTitle("温馨提示").setMessage("请上PC端继续操作！")
				.setNegativeButton("取消", null)
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						activity.stackInstance.popActivity(activity);
					}
				}).show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_previous:
			activity.onBackPressed();
			break;
		case R.id.bt_submit:
			startProgressDialog("正在提交中...");
			uploadMaintenanceData();
			break;
		case R.id.bt_next:
			if (TextUtils.equals(hasChooseComponent, "1")) {//申请了配件，配件已到货
				Intent i = new Intent(activity,
						MaintenanceComponentActivity.class);
				i.putExtra("workorderCode", workorderCode);
				i.putExtra("isMain", isMain);
				startActivity(i);
				// 前往配件列表
			} else if(TextUtils.equals(hasChooseComponent, "0")){//申请了配件，操作正在进行中
				Utilities.showToast("请等待主修完成配件确认", activity);
			}else if(TextUtils.equals(hasChooseComponent, "-1")){//没有申请配件
				Intent i = new Intent(activity, MaintenanceResponseActivity.class);
				i.putExtra("workorderCode", workorderCode);
				i.putExtra("isMain", isMain);
				startActivity(i);
			}else{
				alertBuilder
				.setTitle("更换配件")
				.setMessage("是否需要更换配件？")
				.setNegativeButton("否", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						startProgressDialog("正在请求中...");
						changePartsConfirm("-1");//不申请配件
					}
				})
				.setPositiveButton("是", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						startProgressDialog("正在申请配件...");
						changePartsConfirm("0");//申请配件
					}
				}).show();
			}
			break;
		default:
			break;
		}
	}

	public static MaintenanceHasChooseFragment newInstance(String arguments) {
		Bundle options = new Bundle();
		options.putString(ARGUMENT, arguments);
		MaintenanceHasChooseFragment fragment = new MaintenanceHasChooseFragment();
		fragment.setArguments(options);
		return fragment;
	}
}
