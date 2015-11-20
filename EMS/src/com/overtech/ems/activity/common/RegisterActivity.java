package com.overtech.ems.activity.common;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.common.fragment.RegisterAddIdCardFragment;
import com.overtech.ems.activity.common.fragment.RegisterAddPersonEduAndWorkFragment;
import com.overtech.ems.activity.common.fragment.RegisterAddPersonInfoFragment;
import com.overtech.ems.activity.common.fragment.RegisterAddWorkCertificateFragment;
import com.overtech.ems.activity.common.fragment.RegisterFragment;
import com.overtech.ems.activity.common.fragment.RegisterOtherCertificateFragment;
import com.overtech.ems.utils.Utilities;

public class RegisterActivity extends BaseActivity implements OnClickListener {
	private Context mContext;
	private TextView mHeadContent;
	private ImageView mHeadBack;
	private FrameLayout mContainer;
	private Button mNext;
	private FragmentManager manager;

	private Fragment mCurrentFragment;
	private RegisterFragment mRegisterFragment;
	private RegisterAddPersonInfoFragment mPersonInfoFragment;
	private RegisterAddPersonEduAndWorkFragment mPersonEduWorkFragment;
	private RegisterAddIdCardFragment mIdCardFragment;
	private RegisterAddWorkCertificateFragment mWorkCertificateFragment;
	private RegisterOtherCertificateFragment mOtherCertificateFragment;
	private final String TAG = "Fragment";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		findViewById();
		init(savedInstanceState);

	}

	private void findViewById() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
		mContainer = (FrameLayout) findViewById(R.id.fl_register_container);
		mNext = (Button) findViewById(R.id.btn_next_fragment);
	}

	/**
	 * 为依赖于自身的fragment提供可以操作的对象
	 * 
	 * @return
	 */
	public Button getButton() {
		return mNext;
	}

	private void init(Bundle savedInstanceState) {
		mContext = RegisterActivity.this;
		mHeadContent.setText("注 册");
		mNext.setText("获取短信验证码");
		mHeadBack.setVisibility(View.VISIBLE);
		mHeadBack.setOnClickListener(this);
		mNext.setOnClickListener(this);

		if (savedInstanceState == null) {
			manager = getFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			if (mRegisterFragment == null) {
				mCurrentFragment = mRegisterFragment = new RegisterFragment();
			}
			if (!mRegisterFragment.isAdded()) {
				transaction.add(R.id.fl_register_container, mRegisterFragment,
						"REGISTER").commit();
			} else {
				transaction.show(mRegisterFragment).commit();
			}
		}
	}

	@Override
	public void onBackPressed() {

		if (mCurrentFragment instanceof RegisterFragment) {
			mCurrentFragment = null;
			super.onBackPressed();
		} else if (mCurrentFragment instanceof RegisterAddPersonInfoFragment) {
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.hide(mPersonInfoFragment).show(mRegisterFragment)
					.commit();
			mCurrentFragment = mRegisterFragment;
			mHeadContent.setText("注册");
			mNext.setText("获取短信验证码");
		} else if (mCurrentFragment instanceof RegisterAddPersonEduAndWorkFragment) {
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.hide(mPersonEduWorkFragment).show(mPersonInfoFragment)
					.commit();
			mCurrentFragment = mPersonInfoFragment;
			mHeadContent.setText("基本信息");
		} else if (mCurrentFragment instanceof RegisterAddIdCardFragment) {
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.hide(mIdCardFragment).show(mPersonEduWorkFragment)
					.commit();
			mCurrentFragment = mPersonEduWorkFragment;
			mHeadContent.setText("学历/工作信息");
		} else if (mCurrentFragment instanceof RegisterAddWorkCertificateFragment) {
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.hide(mWorkCertificateFragment).show(mIdCardFragment)
					.commit();
			mCurrentFragment = mIdCardFragment;
			mHeadContent.setText("身份证确认");
		} else if (mCurrentFragment instanceof RegisterOtherCertificateFragment) {
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.hide(mOtherCertificateFragment)
					.show(mWorkCertificateFragment).commit();
			mCurrentFragment = mWorkCertificateFragment;
			mHeadContent.setText("上岗证确认");
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.iv_headBack:
			onBackPressed();
			break;
		case R.id.btn_next_fragment:
			if (mCurrentFragment instanceof RegisterFragment) {

				if (mRegisterFragment.isCorrect()) {
					Log.e(TAG, "======RegisterAddPersonInfo=====");
					if (mPersonInfoFragment == null) {
						mPersonInfoFragment = new RegisterAddPersonInfoFragment();
					}
					mCurrentFragment = mPersonInfoFragment;
					FragmentTransaction transaction = manager
							.beginTransaction();
					if (!mPersonInfoFragment.isAdded()) {
						transaction
								.hide(mRegisterFragment)
								.add(R.id.fl_register_container,
										mPersonInfoFragment, "PersonInfo")
								.commit();
					} else {
						transaction.hide(mRegisterFragment)
								.show(mPersonInfoFragment).commit();
					}

					mNext.setText("下一步");
					mHeadContent.setText("基本信息");
				} else {
				}

			} else if (mCurrentFragment instanceof RegisterAddPersonInfoFragment) {

				if (mPersonInfoFragment.isAllNotNull()) {
					Log.e(TAG, "======RegisterAddPersonEduAndWork=====");
					if (mPersonEduWorkFragment == null) {
						mPersonEduWorkFragment = new RegisterAddPersonEduAndWorkFragment();
					}
					mCurrentFragment = mPersonEduWorkFragment;
					FragmentTransaction transaction = manager
							.beginTransaction();
					if (!mPersonEduWorkFragment.isAdded()) {
						transaction
								.hide(mPersonInfoFragment)
								.add(R.id.fl_register_container,
										mPersonEduWorkFragment,
										"PersonEduAndWork").commit();
					} else {
						transaction.hide(mPersonInfoFragment)
								.show(mPersonEduWorkFragment).commit();
					}

					mNext.setText("下一步");
					mHeadContent.setText("学历/工作信息");
				}

			} else if (mCurrentFragment instanceof RegisterAddPersonEduAndWorkFragment) {

				if (mPersonEduWorkFragment.isAllNotNull()) {
					Log.e(TAG, "======RegisterAddIdCard=====");
					if (mIdCardFragment == null) {
						mIdCardFragment = new RegisterAddIdCardFragment();
					}
					mCurrentFragment = mIdCardFragment;
					FragmentTransaction transaction = manager
							.beginTransaction();
					if (!mIdCardFragment.isAdded()) {
						transaction
								.hide(mPersonEduWorkFragment)
								.add(R.id.fl_register_container,
										mIdCardFragment, "IdCard").commit();
					} else {
						transaction.hide(mPersonEduWorkFragment)
								.show(mIdCardFragment).commit();
					}

					mNext.setText("下一步");
					mHeadContent.setText("身份证确认");
				}

			} else if (mCurrentFragment instanceof RegisterAddIdCardFragment) {

				if (mIdCardFragment.isAllNotNull()) {
					Log.e(TAG, "======RegisterAddWorkCertificate=====");
					if (mWorkCertificateFragment == null) {
						mWorkCertificateFragment = new RegisterAddWorkCertificateFragment();
					}
					mCurrentFragment = mWorkCertificateFragment;
					FragmentTransaction transaction = manager
							.beginTransaction();
					if (!mWorkCertificateFragment.isAdded()) {
						transaction
								.hide(mIdCardFragment)
								.add(R.id.fl_register_container,
										mWorkCertificateFragment,
										"WorkCertificate").commit();
					} else {
						transaction.hide(mIdCardFragment)
								.show(mWorkCertificateFragment).commit();
					}

					mNext.setText("下一步");
					mHeadContent.setText("上岗证确认");
				}

			} else if (mCurrentFragment instanceof RegisterAddWorkCertificateFragment) {

				if (mWorkCertificateFragment.isAllNotNull()) {
					Log.e(TAG, "======RegisterOtherCertificate=====");
					if (mOtherCertificateFragment == null) {
						mOtherCertificateFragment = new RegisterOtherCertificateFragment();
					}
					mCurrentFragment = mOtherCertificateFragment;
					FragmentTransaction transaction = manager
							.beginTransaction();
					if (!mOtherCertificateFragment.isAdded()) {
						transaction
								.hide(mWorkCertificateFragment)
								.add(R.id.fl_register_container,
										mOtherCertificateFragment,
										"OtherCertificate").commit();
					} else {
						transaction.hide(mWorkCertificateFragment)
								.show(mOtherCertificateFragment).commit();
					}
					mNext.setText("下一步");
					mHeadContent.setText("其他证书");
				}

			} else if (mCurrentFragment instanceof RegisterOtherCertificateFragment) {

				if (mOtherCertificateFragment.isAllNotNull()) {
					new AlertDialog.Builder(mContext)
							.setTitle("提醒")
							.setMessage("确认提交以上信息")
							.setPositiveButton("确认",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											Utilities.showToast("开始提交",
													mContext);
										}
									})
							.setNegativeButton("取消",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									}).show();

				}
			}
			break;
		default:
			break;
		}
	}
}
