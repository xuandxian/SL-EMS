package com.overtech.ems.activity.parttime.common;

import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.adapter.PackageDetailAdapter;
import com.overtech.ems.entity.test.Data2;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.CustomProgressDialog;
import com.overtech.ems.widget.dialogeffects.Effectstype;
import com.overtech.ems.widget.dialogeffects.NiftyDialogBuilder;

public class PackageDetailActivity extends BaseActivity {
	private ListView mPackageDetailListView;
	private PackageDetailAdapter adapter;
	private ArrayList<Data2> list;
	private Button mGrabTaskBtn;
	private NiftyDialogBuilder dialogBuilder;
	private Effectstype effect;
	private Context context;
	private ImageView mDoBack;
	private CustomProgressDialog progressDialog;
	private String mCommunityName;
	private TextView mHeadTitle;
	private ImageView mRightContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_package_detail);
		findViewById();
		initData();
		init();
	}

	private void init() {
		context = PackageDetailActivity.this;
		dialogBuilder = NiftyDialogBuilder.getInstance(context);
		progressDialog = CustomProgressDialog.createDialog(context);
		Bundle bundle = getIntent().getExtras();
        //TODO下一行会出现空指针，什么情况？
		if (bundle==null) {
			return;
		}
		mCommunityName = bundle.getString("CommunityName");
		mHeadTitle.setText(mCommunityName);
		adapter = new PackageDetailAdapter(context, list);
		mPackageDetailListView.setAdapter(adapter);
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
				Utilities.showToast("hahahahahaha", context);
			}
		});
	}

	private void findViewById() {
		mPackageDetailListView = (ListView) findViewById(R.id.grab_task_package_listview);
		mGrabTaskBtn = (Button) findViewById(R.id.btn_grab_task_package);
		mHeadTitle = (TextView) findViewById(R.id.tv_grab_package_headTitle);
		mDoBack = (ImageView) findViewById(R.id.iv_grab_headBack);
		mRightContent=(ImageView)findViewById(R.id.iv_map);
	}
	private void showDialog() {
		effect = Effectstype.Slideright;
		dialogBuilder.withTitle("温馨提示").withTitleColor("#FFFFFF")
				.withDividerColor("#11000000").withMessage("您是否要接此单？")
				.withMessageColor("#FF333333").withDialogColor("#FFFFFFFF")
				.withIcon(getResources().getDrawable(R.drawable.icon_dialog))
				.isCancelableOnTouchOutside(true).withDuration(700)
				.withEffect(effect).withButtonDrawable(R.color.main_white)
				.withButton1Text("否").withButton1Color("#FF333333")
				.withButton2Text("是").withButton2Color("#FF333333")
				.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
					}
				}).setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
						progressDialog.setMessage("正在抢单");
						progressDialog.show();
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								progressDialog.dismiss();
							}
						}, 3000);
					}
				}).show();
	}

	private void initData() {
		// 测试数据
		Data2 data1 = new Data2("31号楼1号电梯（月包）", "上海三菱", "80032981234",
				"20层/20站");
		Data2 data2 = new Data2("31号楼2号电梯（季包）", "上海三菱", "80032981235",
				"23层/20站");
		Data2 data3 = new Data2("32号楼1号电梯（半年包）", "上海三菱", "80032981236",
				"20层/20站");
		Data2 data4 = new Data2("32号楼2号电梯（月包）", "上海三菱", "80032981237",
				"25层/20站");
		Data2 data5 = new Data2("33号楼2号电梯（半年包）", "上海三菱", "80032981238",
				"25层/20站");
		list = new ArrayList<Data2>();
		list.add(0, data1);
		list.add(1, data2);
		list.add(2, data3);
		list.add(3, data4);
		list.add(4, data5);
	}
}
