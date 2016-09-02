package com.overtech.ems.activity.fulltime.activity.fragment;

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
import com.overtech.ems.activity.fulltime.activity.MaintenanceTaskActivity;
import com.overtech.ems.activity.fulltime.adapter.MaintenanceHasChooseAdapter;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.http.HttpConnector;
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
	private List<Map<String, Object>> selectDatas;
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
		Bundle bundle = getArguments();
		hasSelectJson = bundle.getString(ARGUMENT);
		if (!TextUtils.isEmpty(hasSelectJson)) {
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

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_maintenance_has_choose,
				container);
		initView(v);
		initEvent();
		return v;

	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		activity = (MaintenanceTaskActivity) getActivity();
		certificate = activity.getCertificate();
		uid = activity.getUid();
		isMain = activity.getIsMain();
		workorderCode = activity.getWorkorderCode();
		hasReport = activity.getHasReport();
		hasChooseComponent = activity.getHasChooseComponent();
	}

	private void initEvent() {
		// TODO Auto-generated method stub
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		if (TextUtils.equals(hasReport, "0")) {
			btSubmit.setVisibility(View.VISIBLE);
			if (adapter == null) {
				adapter = new MaintenanceHasChooseAdapter(activity, selectDatas);
				recyclerView.setAdapter(adapter);
			}
		} else {
			btNext.setVisibility(View.VISIBLE);
			loadingHasReportDatas();
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
				List<Map<String,Object>> lists=(List<Map<String, Object>>) response.body.get("data");
				if(adapter==null){
					adapter=new MaintenanceHasChooseAdapter(activity, lists);
					recyclerView.setAdapter(adapter);
				}else{
					adapter.setDatas(lists);
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

	private void uploadMaintenanceData() {// 提交维修报告
		// TODO Auto-generated method stub
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put("workorderCode", workorderCode);
		body.put("isMain", isMain);
		body.put("data", hasSelectJson);
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

			}

			@Override
			public void bizStIs1Deal() {
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
		alertBuilder.setTitle("更换配件").setMessage("是否需要更换配件？")
				.setNegativeButton("否", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				})
				.setPositiveButton("是", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						changePartsConfirm();
					}
				}).show();
	}

	private void changePartsConfirm() {
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put("workorderCode", workorderCode);
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
				toPcHandle();
			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void bizStIs1Deal() {
				// TODO Auto-generated method stub
				activity.stackInstance.popActivity(activity);
			}

			@Override
			public void stopDialog() {
				// TODO Auto-generated method stub

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
			uploadMaintenanceData();
			break;
		case R.id.bt_next:
			if(TextUtils.equals(hasChooseComponent, "1")){
				Intent i=new Intent(activity,MaintenanceComponentActivity.class);
				i.putExtra("workorderCode", workorderCode);
				i.putExtra("isMain", isMain);
				startActivity(i);
				//前往配件列表
			}else{
				Utilities.showToast("请等待主修完成配件确认", activity);
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
