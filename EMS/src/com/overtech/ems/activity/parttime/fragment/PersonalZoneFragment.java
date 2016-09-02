package com.overtech.ems.activity.parttime.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.activity.parttime.personal.PersonalAccountListActivity;
import com.overtech.ems.activity.parttime.personal.PersonalBoundsActivity;
import com.overtech.ems.activity.parttime.personal.PersonalChargeBackListActivity;
import com.overtech.ems.activity.parttime.personal.PersonalDeatilsActivity;
import com.overtech.ems.activity.parttime.personal.PersonalPartnersActivity;
import com.overtech.ems.activity.parttime.personal.notice.PersonalNoticeActivity;
import com.overtech.ems.activity.parttime.personal.others.PersonalAboutAppActivity;
import com.overtech.ems.activity.parttime.personal.others.PersonalHelpDocActivity;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.http.HttpConnector;
import com.overtech.ems.picasso.Transformation;
import com.overtech.ems.utils.ImageUtils;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.widget.bitmap.ImageLoader;

public class PersonalZoneFragment extends BaseFragment implements
		OnClickListener {

	private final int STUB_ID = R.drawable.icon_personal_my;// 此处为了将ImageLoader里面的方法抽出来单独使用，而将里面的字段提出来
	private final Config DEFAULT_CONFIG = Config.RGB_565;// 同上
	private View view;
	private RelativeLayout rlPersonalDetail;
	private LinearLayout llPersonalAccountList;
	private LinearLayout llPersonalBounds;
	private LinearLayout llCompanyNotice;
	private LinearLayout llPartners;
	private LinearLayout llCancleList;
	private LinearLayout llHelpDoc;
	private LinearLayout llApp;
	private TextView tvHeadContent;
	private TextView tvName;
	private TextView tvPhone;
	private TextView tvAccount;
	private TextView tvBonus;
	private TextView tvChargeback;
	private Activity mActivity;
	private ImageView ivAvator;
	private String uid;
	private String certificate;
	private String employeeType;

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
		employeeType = ((MainActivity) getActivity()).getEmployeeType();
		initViews();
		initEvents();
		onLoading();
		return view;
	}

	private void onLoading() {
		startProgressDialog(getResources().getString(
				R.string.loading_public_default));
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20070, uid,
				certificate, null) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return getActivity();
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				tvName.setText(response.body.get("name").toString());
				tvPhone.setText(response.body.get("phone").toString());
				if (TextUtils.isEmpty(response.body.get("avator").toString())) {
					ivAvator.setImageResource(STUB_ID);
				} else {
					ImageLoader.getInstance().displayImage(
							response.body.get("avator").toString(), ivAvator,
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

	private void initViews() {
		ivAvator = (ImageView) view.findViewById(R.id.iv_headview);
		rlPersonalDetail = (RelativeLayout) view
				.findViewById(R.id.rl_personal_details);
		llPersonalAccountList = (LinearLayout) view
				.findViewById(R.id.ll_personal_account_list);
		llPersonalBounds = (LinearLayout) view
				.findViewById(R.id.ll_personal_bounds);
		llPartners=(LinearLayout) view.findViewById(R.id.llPartners);
		llCompanyNotice = (LinearLayout) view
				.findViewById(R.id.ll_personal_notice);
		llCancleList = (LinearLayout) view.findViewById(R.id.ll_cancle_list);
		llHelpDoc = (LinearLayout) view.findViewById(R.id.ll_help_doc);
		tvHeadContent = (TextView) view.findViewById(R.id.tv_headTitle);
		tvName = (TextView) view.findViewById(R.id.tv_name);
		tvPhone = (TextView) view.findViewById(R.id.textViewPhone);
		tvAccount = (TextView) view.findViewById(R.id.tvAccount);
		tvBonus = (TextView) view.findViewById(R.id.tvBounds);
		tvChargeback = (TextView) view.findViewById(R.id.tvChargeback);
		llApp = (LinearLayout) view.findViewById(R.id.ll_about_app);
		tvHeadContent.setText("我的");
		// mPhone.setText(mSharedPreferences.getString(SharedPreferencesKeys.CURRENT_LOGIN_NAME,
		// null));// 设置登陆时的个人手机号
	}

	private void initEvents() {
		// mScrollView.setImageView(mBackgroundImageView);
		rlPersonalDetail.setOnClickListener(this);
		llPersonalAccountList.setOnClickListener(this);
		llPersonalBounds.setOnClickListener(this);
		llPartners.setOnClickListener(this);
		llCompanyNotice.setOnClickListener(this);
		llCancleList.setOnClickListener(this);
		llHelpDoc.setOnClickListener(this);
		llApp.setOnClickListener(this);

		Logr.e("当前角色类型====" + employeeType);
		if (TextUtils.equals("全职", employeeType)) {
			llPersonalAccountList.setEnabled(false);
			llPersonalBounds.setEnabled(false);
			llCancleList.setEnabled(false);
			tvAccount.setEnabled(false);
			tvBonus.setEnabled(false);
			tvChargeback.setEnabled(false);
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.rl_personal_details:// 我的账户
			intent.setClass(mActivity, PersonalDeatilsActivity.class);
			startActivity(intent);
			break;
		case R.id.ll_personal_account_list:// 我的账单
			intent.setClass(mActivity, PersonalAccountListActivity.class);
			startActivity(intent);
			break;
		case R.id.ll_personal_bounds:// 奖励记录
			intent.setClass(mActivity, PersonalBoundsActivity.class);
			startActivity(intent);
			break;
		case R.id.llPartners://搭档收藏
			intent.setClass(activity, PersonalPartnersActivity.class);
			startActivity(intent);
			break;
		case R.id.ll_personal_notice:// 公告
			intent.setClass(mActivity, PersonalNoticeActivity.class);
			startActivity(intent);
			break;
		case R.id.ll_cancle_list:// 退单记录
			intent.setClass(mActivity, PersonalChargeBackListActivity.class);
			startActivity(intent);
			break;
		case R.id.ll_help_doc:// 帮助文档
			intent.setClass(mActivity, PersonalHelpDocActivity.class);
			startActivity(intent);
			break;
		case R.id.ll_about_app:// 关于app
			intent.setClass(mActivity, PersonalAboutAppActivity.class);
			startActivity(intent);
			break;
		}
	}
}
