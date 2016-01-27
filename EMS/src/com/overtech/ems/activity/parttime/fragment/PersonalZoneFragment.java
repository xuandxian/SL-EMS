package com.overtech.ems.activity.parttime.fragment;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.parttime.personal.PersonalAboutAppActivity;
import com.overtech.ems.activity.parttime.personal.PersonalAccountListActivity;
import com.overtech.ems.activity.parttime.personal.PersonalAnnouncementActivity;
import com.overtech.ems.activity.parttime.personal.PersonalBoundsActivity;
import com.overtech.ems.activity.parttime.personal.PersonalCancleListActivity;
import com.overtech.ems.activity.parttime.personal.PersonalDeatilsActivity;
import com.overtech.ems.activity.parttime.personal.PersonalHelpDocActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.http.HttpEngine.Param;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.picasso.Picasso;
import com.overtech.ems.picasso.Transformation;
import com.overtech.ems.utils.ImageCacheUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.CustomScrollView;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class PersonalZoneFragment extends BaseFragment implements
		OnClickListener, OnTouchListener {

	private final int STUB_ID = R.drawable.icon_personal_my;// 此处为了将ImageLoader里面的方法抽出来单独使用，而将里面的字段提出来
	private final Config DEFAULT_CONFIG = Config.RGB_565;// 同上
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

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case StatusCode.PERSONAL_ZONE_SUCCESS:
				String info = (String) msg.obj;
				Log.e("==personZone==", info);
				try {
					JSONObject json = new JSONObject(info);
					JSONObject model = (JSONObject) json.get("model");
					String imageUrl = model.getString("path");
					String name = model.getString("name");
					if (imageUrl == null || "".equals(imageUrl)) {
						mAvator.setScaleType(ScaleType.FIT_XY);
						mAvator.setImageResource(STUB_ID);
					} else {
						Log.e("==图片路径==", imageUrl);
						// 调用从网络中加载过来的图片
						Picasso.with(context).load(imageUrl)
								.placeholder(STUB_ID).error(STUB_ID)
								.config(DEFAULT_CONFIG)
								.transform(new Transformation() {
									// 圆角图片的实现
									@Override
									public Bitmap transform(Bitmap source) {
										return ImageCacheUtils
												.toRoundBitmap(source);
									}

									@Override
									public String key() {
										return null;
									}
								}).into(mAvator);

					}
					mName.setText(name);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast("服务器异常", mActivity);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast("网络异常", mActivity);
				break;
			default:
				break;
			}
			stopProgressDialog();// 图片加载完成后停止进度框

		};
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_personal_zone, container,
				false);
		initViews();
		initEvents();
		onLoading();
		return view;
	}

	private void onLoading() {
		startProgressDialog("请稍后...");
		String mLoginName = mSharedPreferences.getString(
				SharedPreferencesKeys.CURRENT_LOGIN_NAME, null);
		Param param = new Param(Constant.LOGINNAME, mLoginName);
		Request request = httpEngine.createRequest(
				ServicesConfig.PERSONAL_AVATOR, param);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					msg.what = StatusCode.PERSONAL_ZONE_SUCCESS;
					msg.obj = response.body().string();
				} else {
					msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
				}
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(Request request, IOException e) {
				Message msg = new Message();
				msg.what = StatusCode.RESPONSE_NET_FAILED;
				handler.sendMessage(msg);
			}
		});
	}

	private void initViews() {
		mBackgroundImageView = (ImageView) view
				.findViewById(R.id.personal_background_image);
		mAvator = (ImageView) view.findViewById(R.id.imageView1);
		mScrollView = (CustomScrollView) view
				.findViewById(R.id.personal_scrollView);
		mPersonalDetail = (RelativeLayout) view
				.findViewById(R.id.rl_personal_details);
		mPersonalAccountList = (RelativeLayout) view
				.findViewById(R.id.rl_personal_account_list);
		mPersonalBounds = (RelativeLayout) view
				.findViewById(R.id.rl_personal_bounds);
		mCompanyNotice = (RelativeLayout) view
				.findViewById(R.id.rl_personal_notice);
		mCancleList = (RelativeLayout) view.findViewById(R.id.rl_cancle_list);
		mHelpDoc = (RelativeLayout) view.findViewById(R.id.rl_help_doc);
		mHeadContent = (TextView) view.findViewById(R.id.tv_headTitle);
		mName = (TextView) view.findViewById(R.id.textView1);
		mPhone = (TextView) view.findViewById(R.id.textViewPhone);
		mApp = (RelativeLayout) view.findViewById(R.id.rl_about_app);
		mHeadContent.setText("我的");
		mPhone.setText(mSharedPreferences.getString(
				SharedPreferencesKeys.CURRENT_LOGIN_NAME, null));// 设置登陆时的个人手机号
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
		case R.id.rl_personal_details:// 我的账户
			intent.setClass(mActivity, PersonalDeatilsActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_personal_account_list:// 我的账单
			intent.setClass(mActivity, PersonalAccountListActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_personal_bounds:// 奖励记录
			intent.setClass(mActivity, PersonalBoundsActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_personal_notice:// 公告
			// Utilities.showToast("你点击了公告", mActivity);
			intent.setClass(mActivity, PersonalAnnouncementActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_cancle_list:// 退单记录
			intent.setClass(mActivity, PersonalCancleListActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_help_doc:// 帮助文档
			intent.setClass(mActivity, PersonalHelpDocActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_about_app:// 关于app
			intent.setClass(mActivity, PersonalAboutAppActivity.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		switch (v.getId()) {
		case R.id.rl_personal_details:
			if (event.getAction() == MotionEvent.ACTION_UP) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
			}
			break;

		default:
			break;
		}
		return false;
	}
}
