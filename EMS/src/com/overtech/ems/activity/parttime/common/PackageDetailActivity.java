package com.overtech.ems.activity.parttime.common;

import java.io.IOException;
import java.util.ArrayList;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.adapter.PackageDetailAdapter;
import com.overtech.ems.entity.bean.TaskPackageDetailBean;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.entity.parttime.TaskPackageDetail;
import com.overtech.ems.http.HttpEngine.Param;
import com.overtech.ems.widget.dialogeffects.Effectstype;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class PackageDetailActivity extends BaseActivity {
	private ListView mPackageDetailListView;
	private PackageDetailAdapter adapter;
	private ArrayList<TaskPackageDetail> list;
	private Button mGrabTaskBtn;
	private Effectstype effect;
	private ImageView mDoBack;
	private String mCommunityName;
	private TextView mHeadTitleCommunity;
	private ImageView mRightContent;
	private String mTaskNo;
	private String mLongitude; // 经度
	private String mLatitude; // 纬度
	private TextView mHeadTitleTaskNo;
	private int totalPrice;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			String json = (String) msg.obj;
			Gson gson = new Gson();
			TaskPackageDetailBean tasks = gson.fromJson(json,
					TaskPackageDetailBean.class);
			list = (ArrayList<TaskPackageDetail>) tasks.getModel();
			adapter = new PackageDetailAdapter(context, list);
			mPackageDetailListView.setAdapter(adapter);
			for (int i = 0; i < list.size(); i++) {
				totalPrice += Integer.valueOf(list.get(i).getMaintainPrice());
			}
			mGrabTaskBtn.setText("抢单(￥" + String.valueOf(totalPrice) + "元)");
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_package_detail);
		getExtrasData();
		findViewById();
		getDataByTaskNo(ServicesConfig.TASK_PACKAGE_DETAIL);
		init();
	}

	private void getExtrasData() {
		Bundle bundle = getIntent().getExtras();
		if (null == bundle) {
			return;
		}
		mCommunityName = bundle.getString("CommunityName");
		mTaskNo = bundle.getString("TaskNo");
		mLongitude = bundle.getString("Longitude");
		mLatitude = bundle.getString("Latitude");
	}

	private void findViewById() {
		mPackageDetailListView = (ListView) findViewById(R.id.grab_task_package_listview);
		mGrabTaskBtn = (Button) findViewById(R.id.btn_grab_task_package);
		mHeadTitleCommunity = (TextView) findViewById(R.id.tv_headTitle_community_name);
		mHeadTitleTaskNo = (TextView) findViewById(R.id.tv_headTitle_taskno);
		mDoBack = (ImageView) findViewById(R.id.iv_grab_headBack);
		mRightContent = (ImageView) findViewById(R.id.iv_navicate_right);
		mRightContent.setBackgroundResource(R.drawable.icon_map);
		mRightContent.setVisibility(View.VISIBLE);
	}

	private void getDataByTaskNo(String url) {
		startProgressDialog("正在查询...");
		Param param = new Param("mTaskNo", mTaskNo);
		Request request = httpEngine.createRequest(url, param);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				stopProgressDialog();
				Message msg = new Message();
				msg.obj = response.body().string();
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(Request request, IOException e) {
				stopProgressDialog();
			}
		});

	}

	private void init() {
		mHeadTitleCommunity.setText(mCommunityName);
		mHeadTitleTaskNo.setText(mTaskNo);
		mPackageDetailListView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Intent intent = new Intent(PackageDetailActivity.this,
								ElevatorDetailActivity.class);
						startActivity(intent);
					}
				});
		mGrabTaskBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showDialog();
			}
		});
		mDoBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		mRightContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(PackageDetailActivity.this,
						ShowCommunityLocationActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("CommunityName", mCommunityName);
				bundle.putString("Longitude", mLongitude);
				bundle.putString("Latitude", mLatitude);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	private void showDialog() {
		effect = Effectstype.Slideright;
		dialogBuilder.withTitle("温馨提示").withTitleColor(R.color.main_primary)
				.withDividerColor("#11000000").withMessage("您是否要接此单？")
				.withMessageColor(R.color.main_primary)
				.withDialogColor("#FFFFFFFF").isCancelableOnTouchOutside(true)
				.withDuration(700).withEffect(effect)
				.withButtonDrawable(R.color.main_white).withButton1Text("否")
				.withButton1Color(R.color.main_primary).withButton2Text("是")
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
						startProgressDialog("正在抢单...");
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								stopProgressDialog();
							}
						}, 3000);
					}
				}).show();
	}

}
