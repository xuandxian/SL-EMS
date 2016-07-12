package com.overtech.ems.activity.parttime.fragment;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.activity.parttime.personal.PersonalAccountListActivity;
import com.overtech.ems.activity.parttime.personal.PersonalBoundsActivity;
import com.overtech.ems.activity.parttime.personal.PersonalChargeBackListActivity;
import com.overtech.ems.activity.parttime.personal.PersonalDeatilsActivity;
import com.overtech.ems.activity.parttime.personal.notice.PersonalNoticeActivity;
import com.overtech.ems.activity.parttime.personal.others.PersonalAboutAppActivity;
import com.overtech.ems.activity.parttime.personal.others.PersonalHelpDocActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.bean.StatusCodeBean;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.picasso.Transformation;
import com.overtech.ems.utils.ImageUtils;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.bitmap.ImageLoader;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class PersonalZoneFragment extends BaseFragment implements
		OnClickListener {

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
	private ImageView mAvator;
	private String uid;
	private String certificate;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case StatusCode.PERSONAL_ZONE_SUCCESS:
				stopProgressDialog();
				String info = (String) msg.obj;
				Logr.e("====" + info);
				StatusCodeBean bean = gson.fromJson(info, StatusCodeBean.class);
				int st = bean.st;
				if (st == -1 || st == -2) {
					Utilities.showToast(bean.msg, mActivity);
					SharePreferencesUtils.put(mActivity,
							SharedPreferencesKeys.CERTIFICATED, "");
					SharePreferencesUtils.put(mActivity,
							SharedPreferencesKeys.UID, "");
					Intent intent = new Intent(mActivity, LoginActivity.class);
					startActivity(intent);
					return;
				}
				mName.setText(bean.body.get("name").toString());
				mPhone.setText(bean.body.get("phone").toString());
				if (TextUtils.isEmpty(bean.body.get("avator").toString())) {
					mAvator.setImageResource(STUB_ID);
				} else {
					ImageLoader.getInstance().displayImage(
							bean.body.get("avator").toString(), mAvator,
							STUB_ID, STUB_ID, DEFAULT_CONFIG,
							new Transformation() {

								@Override
								public Bitmap transform(Bitmap source) {
									// TODO Auto-generated method
									// stub
									return ImageUtils.toRoundBitmap(source);
								}

								@Override
								public String key() {
									// TODO Auto-generated method
									// stub
									return null;
								}
							});
				}
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast(R.string.response_failure_msg, mActivity);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast(R.string.request_error_msg, mActivity);
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
		uid = ((MainActivity) getActivity()).getUid();
		certificate = ((MainActivity) getActivity()).getCertificate();
		initViews();
		initEvents();
		onLoading();
		return view;
	}

	private void onLoading() {
		startProgressDialog(getResources().getString(R.string.loading_public_default));
		Requester requester = new Requester();
		requester.cmd = 20070;
		requester.certificate = certificate;
		requester.uid = uid;
		Request request = httpEngine.createRequest(SystemConfig.NEWIP,
				gson.toJson(requester));
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
		mAvator = (ImageView) view.findViewById(R.id.iv_headview);
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
		mName = (TextView) view.findViewById(R.id.tv_name);
		mPhone = (TextView) view.findViewById(R.id.textViewPhone);
		mApp = (RelativeLayout) view.findViewById(R.id.rl_about_app);
		mHeadContent.setText("我的");
		// mPhone.setText(mSharedPreferences.getString(SharedPreferencesKeys.CURRENT_LOGIN_NAME,
		// null));// 设置登陆时的个人手机号
	}

	private void initEvents() {
		// mScrollView.setImageView(mBackgroundImageView);
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
			intent.setClass(mActivity, PersonalNoticeActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_cancle_list:// 退单记录
			intent.setClass(mActivity, PersonalChargeBackListActivity.class);
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
}
