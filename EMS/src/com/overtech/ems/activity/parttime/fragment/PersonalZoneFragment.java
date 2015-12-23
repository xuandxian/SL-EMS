package com.overtech.ems.activity.parttime.fragment;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.MyApplication;
import com.overtech.ems.activity.parttime.personal.PersonalAboutAppActivity;
import com.overtech.ems.activity.parttime.personal.PersonalAccountListActivity;
import com.overtech.ems.activity.parttime.personal.PersonalAnnouncementActivity;
import com.overtech.ems.activity.parttime.personal.PersonalBoundsActivity;
import com.overtech.ems.activity.parttime.personal.PersonalCancleListActivity;
import com.overtech.ems.activity.parttime.personal.PersonalDeatilsActivity;
import com.overtech.ems.activity.parttime.personal.PersonalHelpDocActivity;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.http.HttpEngine.Param;
import com.overtech.ems.widget.CustomScrollView;
import com.overtech.ems.widget.bitmap.ImageLoader;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class PersonalZoneFragment extends BaseFragment implements OnClickListener ,OnTouchListener {
	private View view;
	private RelativeLayout mPersonalDetail;
	private RelativeLayout mPersonalAccountList;
	private RelativeLayout mPersonalBounds;
	private RelativeLayout mCompanyNotice;
	private RelativeLayout mCancleList;
	private RelativeLayout mHelpDoc;
	private RelativeLayout mApp;
	private TextView mHeadContent;
	private TextView mName;
	private TextView mPhone;
	private Activity mActivity;
	private CustomScrollView mScrollView;
	private ImageView mBackgroundImageView;
	private ImageView mAvator;
	private SharedPreferences sp;
	private Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			String info=(String) msg.obj;
			try {
				JSONObject json=new JSONObject(info);
				JSONObject model=(JSONObject) json.get("model");
				String url=model.getString("avatorUrl");
				String name=model.getString("name");
				System.out.println("图片地址======"+url);
				System.out.println("个人用户名===="+name);
				imageLoader.displayImage(url, mAvator);
				mName.setText(name);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			
			
		};
	};
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity=activity;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_personal_zone, container,
				false);
		
		initViews();
		initEvents();
		startProgressDialog("请稍后...");
		onLoading();
		return view;
	}

	private void onLoading() {
		String mPhoneNo=sp.getString("mPhoneNo", null);
		Param param=new Param("mPhoneNo",mPhoneNo);
		Request request=httpEngine.createRequest(ServicesConfig.PERSONAL_AVATOR, param);
		Call call=httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {
			
			@Override
			public void onResponse(Response arg0) throws IOException {
				Message msg=new Message();
				msg.obj=arg0.body().string();
				handler.sendMessage(msg);
			}
			
			@Override
			public void onFailure(Request arg0, IOException arg1) {
				
			}
		});
	}
	private void initViews() {
		sp=((MyApplication)getActivity().getApplication()).getSharePreference();
		
		imageLoader.initContext(mActivity);
		
		mBackgroundImageView = (ImageView) view
				.findViewById(R.id.personal_background_image);
		mAvator=(ImageView)view
				.findViewById(R.id.imageView1);
		mScrollView=(CustomScrollView) view
				.findViewById(R.id.personal_scrollView);
		mPersonalDetail = (RelativeLayout) view
				.findViewById(R.id.rl_personal_details);
		mPersonalAccountList = (RelativeLayout) view
				.findViewById(R.id.rl_personal_account_list);
		mPersonalBounds = (RelativeLayout) view
				.findViewById(R.id.rl_personal_bounds);
		mCompanyNotice = (RelativeLayout) view
				.findViewById(R.id.rl_personal_notice);
		mCancleList = (RelativeLayout) view
				.findViewById(R.id.rl_cancle_list);
		mHelpDoc = (RelativeLayout) view
				.findViewById(R.id.rl_help_doc);
		mHeadContent=(TextView) view
				.findViewById(R.id.tv_headTitle);
		mName=(TextView)view
				.findViewById(R.id.textView1);
		mPhone=(TextView)view
				.findViewById(R.id.textViewPhone);
		mApp= (RelativeLayout) view
				.findViewById(R.id.rl_about_app);
		mHeadContent.setText("我的");
		mPhone.setText(sp.getString("mPhoneNo", null));//设置登陆时的个人手机号
	}

	private void initEvents() {
		mScrollView.setImageView(mBackgroundImageView);
		mPersonalDetail.setOnClickListener(this);
		mPersonalAccountList.setOnClickListener(this);
		mPersonalBounds.setOnClickListener(this);
		mCompanyNotice.setOnClickListener(this);
		mCancleList.setOnClickListener(this);
		mHelpDoc.setOnClickListener(this);
		mApp.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.rl_personal_details:
			intent.setClass(mActivity, PersonalDeatilsActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_personal_account_list:
			intent.setClass(mActivity, PersonalAccountListActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_personal_bounds:
			intent.setClass(mActivity, PersonalBoundsActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_personal_notice:
//			Utilities.showToast("你点击了公告", mActivity);
			intent.setClass(mActivity, PersonalAnnouncementActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_cancle_list:
			intent.setClass(mActivity, PersonalCancleListActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_help_doc:
			intent.setClass(mActivity, PersonalHelpDocActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_about_app:
			intent.setClass(mActivity, PersonalAboutAppActivity.class);
			startActivity(intent);
			break;
		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		switch (v.getId()) {
		case R.id.rl_personal_details:
			if(event.getAction()==MotionEvent.ACTION_UP){
				v.getParent().requestDisallowInterceptTouchEvent(true);
			}
			break;

		default:
			break;
		}
		return false;
	}
	

}
