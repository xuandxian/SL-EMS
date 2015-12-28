package com.overtech.ems.activity.parttime.personal;

import java.io.IOException;
import java.util.ArrayList;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.MyApplication;
import com.overtech.ems.activity.adapter.PersonalBonusListAdapter;
import com.overtech.ems.entity.bean.BonusBean;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.entity.test.Data2;
import com.overtech.ems.http.HttpEngine.Param;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class PersonalBoundsActivity extends BaseActivity implements OnClickListener {
	private ImageView mDoBack;
	private TextView mHeadContent;
	private ExpandableListView mPersonalAccountListView;
	private PersonalBonusListAdapter adapter;
	private Context context;
	private SharedPreferences sp;
	private final int SUCCESS=1;
	private final int FAILED=0;
	private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS:
				String json=(String) msg.obj;
				Log.e("bonus======", json);
				Gson gson=new Gson();
				BonusBean bean=gson.fromJson(json, BonusBean.class);
				adapter=new PersonalBonusListAdapter(bean.getModel(), context);
				mPersonalAccountListView.setAdapter(adapter);
				stopProgressDialog();//加载完成后dialog消失
				break;
			case FAILED:
				Utilities.showToast("链接异常，请检查网络链接...", context);
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
		setContentView(R.layout.activity_personal_bounds);
		findViewById();
		initData();
	}
	private void findViewById() {
		mDoBack=(ImageView)findViewById(R.id.iv_headBack);
		mHeadContent=(TextView)findViewById(R.id.tv_headTitle);
		mPersonalAccountListView=(ExpandableListView)findViewById(R.id.lv_personal_account_list);
		sp=((MyApplication)getApplication()).getSharePreference();
	}
	
	private void initData() {
		mDoBack.setVisibility(View.VISIBLE);
		mHeadContent.setText("奖励记录");
		context=PersonalBoundsActivity.this;
		mDoBack.setOnClickListener(this);
		startLoading();
	}
	private void startLoading() {
		startProgressDialog("正在玩命加载中...");
		Param param=new Param("mPhoneNo", sp.getString("mPhoneNo", null));
		Request request=httpEngine.createRequest(ServicesConfig.PERSONAL_BONUS_LIST, param);
		Call call=httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {
			
			@Override
			public void onResponse(Response response) throws IOException {
				Message msg=new Message();
				if(response.isSuccessful()){
					msg.what=SUCCESS;
					msg.obj=response.body().string();
				}else{
					msg.what=FAILED;
					msg.obj=response.body().string();
					Log.e("response===failed===", "==response failed=");
				}
				handler.sendMessage(msg);
			}
			
			@Override
			public void onFailure(Request request, IOException arg1) {
				Message msg=new Message();
				msg.what=FAILED;
				handler.sendMessage(msg);
				Log.e("request====", "request====failed==");
			}
		});
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.iv_headBack:
			finish();
			break;
		default:
			break;
		}
	}

}
