package com.overtech.ems.activity.common.register;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.common.register.RegisterAddIdCardFragment.RegAddIdCardFrgClickListener;
import com.overtech.ems.activity.common.register.RegisterAddPersonEduAndWorkFragment.RegAddPerEduWorkFrgClickListener;
import com.overtech.ems.activity.common.register.RegisterAddPersonInfoFragment.RegAddPerInfoFrgClickListener;
import com.overtech.ems.activity.common.register.RegisterAddWorkCertificateFragment.RegAddWorkCerFrgClickListener;
import com.overtech.ems.activity.common.register.RegisterFragment.RegFraBtnClickListener;
import com.overtech.ems.activity.common.register.RegisterOtherCertificateFragment.RegOthCerFrgListener;
import com.overtech.ems.activity.common.register.RegisterPrivacyItemFragment.RegPriItemFrgBtnClickListener;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.entity.bean.LoginBean;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.http.OkHttpClientManager;
import com.overtech.ems.http.OkHttpClientManager.ResultCallback;
import com.overtech.ems.utils.ImageUtils;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Request;

public class RegisterActivity extends BaseActivity implements
		RegPriItemFrgBtnClickListener, RegFraBtnClickListener,
		RegAddPerInfoFrgClickListener, RegAddPerEduWorkFrgClickListener,
		RegAddIdCardFrgClickListener, RegAddWorkCerFrgClickListener,
		RegOthCerFrgListener {
	private Toolbar toolbar;
	private ActionBar actionBar;
	private AppCompatTextView tvTitle;
	private FragmentManager manager;
	private RegisterPrivacyItemFragment mPrivacyItemFragment;
	private RegisterFragment mRegisterFragment;
	private RegisterAddPersonInfoFragment mPersonInfoFragment;
	private RegisterAddPersonEduAndWorkFragment mPersonEduWorkFragment;
	private RegisterAddIdCardFragment mIdCardFragment;
	private RegisterAddWorkCertificateFragment mWorkCertificateFragment;
	private RegisterOtherCertificateFragment mOtherCertificateFragment;
	public String mPhoneNo;
	private RegisterActivity activity;
	private ArrayList<String> imgs = new ArrayList<String>();
	private final String PRIVACY = "privacy";
	private final String PHONE = "phone";
	private final String INFO = "info";
	private final String EDU = "educate";
	private final String IDCARD = "idCard";
	private final String WORK = "work";
	private final String OTHER = "other";
	/**
	 * 记录上传的图片顺序 默认从身份证开始 0,1
	 */
	public static int imgIndex = 0;
	/**
	 * 记录已经选择的上岗证张数
	 */
	private int workCounts = 0;

	@Override
	protected int getLayoutResIds() {
		// TODO Auto-generated method stub
		return R.layout.activity_register;
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		activity = this;
		initView();
		if (savedInstanceState == null) {
			manager = getSupportFragmentManager();
			manager.addOnBackStackChangedListener(new OnBackStackChangedListener() {

				@Override
				public void onBackStackChanged() {
					// TODO Auto-generated method stub
					Logr.e("fragment 又改变了");
					if (manager.getBackStackEntryCount() > 0) {
						BackStackEntry entry = manager
								.getBackStackEntryAt(manager
										.getBackStackEntryCount() - 1);
						if (entry != null) {
							Logr.e("当前fragment==stack名称==" + entry.getName());
							if (TextUtils.equals(PHONE, entry.getName())) {
								tvTitle.setText("注册");
							} else if (TextUtils.equals(INFO, entry.getName())) {
								tvTitle.setText("基本信息");
							} else if (TextUtils.equals(EDU, entry.getName())) {
								tvTitle.setText("学历/工作信息");
							} else if (TextUtils.equals(IDCARD, entry.getName())) {
								tvTitle.setText("身份证确认");
							} else if (TextUtils.equals(WORK, entry.getName())) {
								tvTitle.setText("上岗证确认");
							} else if (TextUtils.equals(OTHER, entry.getName())) {
								tvTitle.setText("其他信息");
							}
						}

					} else {
						tvTitle.setText("用户协议");
					}
				}
			});
			FragmentTransaction transaction = manager.beginTransaction();
			if (mPrivacyItemFragment == null) {
				mPrivacyItemFragment = new RegisterPrivacyItemFragment();
			}
			mPrivacyItemFragment.setRegPriItemFrgBtnClickListener(this);
			if (!mPrivacyItemFragment.isAdded()) {
				transaction.add(R.id.fl_register_container,
						mPrivacyItemFragment, "RegisterPrivacyItemFragment");
				transaction.commit();
			} else {
				transaction.show(mPrivacyItemFragment);
				transaction.commit();
			}
		} else {
			manager = getSupportFragmentManager();
			manager.addOnBackStackChangedListener(new OnBackStackChangedListener() {

				@Override
				public void onBackStackChanged() {
					// TODO Auto-generated method stub
					if (manager.getBackStackEntryCount() > 0) {
						BackStackEntry entry = manager
								.getBackStackEntryAt(manager
										.getBackStackEntryCount() - 1);
						if (entry != null) {
							Logr.e("当前fragment==stack名称==" + entry.getName());
							if (TextUtils.equals(PHONE, entry.getName())) {
								tvTitle.setText("注册");
							} else if (TextUtils.equals(INFO, entry.getName())) {
								tvTitle.setText("基本信息");
							} else if (TextUtils.equals(EDU, entry.getName())) {
								tvTitle.setText("学历/工作信息");
							} else if (TextUtils.equals(IDCARD, entry.getName())) {
								tvTitle.setText("身份证确认");
							} else if (TextUtils.equals(WORK, entry.getName())) {
								tvTitle.setText("上岗证确认");
							} else if (TextUtils.equals(OTHER, entry.getName())) {
								tvTitle.setText("其他信息");
							}
						}

					} else {
						tvTitle.setText("用户协议");
					}
				}
			});
			mPrivacyItemFragment = (RegisterPrivacyItemFragment) manager
					.findFragmentByTag("RegisterPrivacyItemFragment");
			mPrivacyItemFragment.setRegPriItemFrgBtnClickListener(this);

			mRegisterFragment = (RegisterFragment) manager
					.findFragmentByTag("RegisterFragment");
			mRegisterFragment.setRegFraBtnClickListener(this);

			mPersonInfoFragment = (RegisterAddPersonInfoFragment) manager
					.findFragmentByTag("RegisterAddPersonInfoFragment");
			mPersonInfoFragment.setRegAddPerInfoFrgClickListener(this);

			mPersonEduWorkFragment = (RegisterAddPersonEduAndWorkFragment) manager
					.findFragmentByTag("RegisterAddPersonEduAndWorkFragment");
			mPersonEduWorkFragment.setRegAddPerEduWorkFrgClickListener(this);

			mIdCardFragment = (RegisterAddIdCardFragment) manager
					.findFragmentByTag("RegisterAddIdCardFragment");
			mIdCardFragment.setRegAddIdCardClickListener(this);

			mWorkCertificateFragment = (RegisterAddWorkCertificateFragment) manager
					.findFragmentByTag("RegisterAddWorkCertificateFragment");
			mWorkCertificateFragment.setRegAddWorkCerFrgClickListener(this);

			mOtherCertificateFragment = (RegisterOtherCertificateFragment) manager
					.findFragmentByTag("RegisterOtherCertificateFragment");
			mOtherCertificateFragment.setRegOthFrgListener(this);
		}
		initEvent();
	}

	private void initEvent() {
		// TODO Auto-generated method stub
		toolbar.setNavigationOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		tvTitle.setText("用户协议");
	}

	private void initView() {
		// TODO Auto-generated method stub
		toolbar = (Toolbar) findViewById(R.id.toolBar);
		setSupportActionBar(toolbar);
		actionBar = getSupportActionBar();
		tvTitle = (AppCompatTextView) findViewById(R.id.tvTitle);
	}

	/**
	 * 上传用户基本信息以及上传身份证正反面
	 */
	protected void startUpLoading() {
		initAllImgPath();
		startProgressDialog(getResources().getString(
				R.string.loading_public_upload));
		Requester requester = new Requester();
		requester.cmd = 3;
		requester.body.put("phone", mPhoneNo);
		requester.body.put("name", mPersonInfoFragment.nameContent);
		requester.body.put("idcard", mPersonInfoFragment.idNumContent);
		requester.body.put("workNo", mPersonInfoFragment.workNumContent);
		requester.body.put("cityCode", mPersonInfoFragment.cityCode);
		requester.body.put("cityName", mPersonInfoFragment.cityName);
		requester.body.put("zoneCode", mPersonInfoFragment.zoneCode);
		requester.body.put("zoneName", mPersonInfoFragment.zoneName);
		requester.body.put("eduLevel", mPersonEduWorkFragment.eduLevel);
		requester.body.put("workUnit", mPersonEduWorkFragment.workUnit);
		requester.body.put("entryTime", mPersonEduWorkFragment.enterTime);
		requester.body.put("workYears", mPersonEduWorkFragment.workYears);
		requester.body.put("elevatorBrand",
				mPersonEduWorkFragment.elevatorBrand);

		HashMap<String, Object> front = new HashMap<String, Object>();// 身份证正面
		front.put("content",
				ImageUtils.bitmapToString(mIdCardFragment.idCardFrontPath));
		// front.put("content","123");//test
		front.put("attrName",
				ImageUtils.getBitmapAttrName(mIdCardFragment.idCardFrontPath));
		Logr.e(ImageUtils.getBitmapAttrName(mIdCardFragment.idCardFrontPath));
		requester.body.put("frontIdCard", front);

		HashMap<String, Object> opposite = new HashMap<String, Object>();// 身份证反面
		opposite.put("content",
				ImageUtils.bitmapToString(mIdCardFragment.idCardOppositePath));

		opposite.put("attrName", ImageUtils
				.getBitmapAttrName(mIdCardFragment.idCardOppositePath));
		Logr.e(ImageUtils.getBitmapAttrName(mIdCardFragment.idCardOppositePath));
		requester.body.put("oppositeIdCard", opposite);
		ResultCallback<LoginBean> callback = new ResultCallback<LoginBean>() {

			@Override
			public void onResponse(LoginBean response) {
				// TODO Auto-generated method stub
				stopProgressDialog();
				if (response == null) {
					Utilities.showToast(R.string.response_no_object, activity);
					return;
				}
				int st = response.st;
				if (st == 0) {
					Utilities.showToast(response.msg, activity);
					imgIndex = 2;// 下一步开始加载角标2位置的图片
					if (workCounts == 2) {// 选择了两张上岗证
						String content = ImageUtils
								.bitmapToString(mWorkCertificateFragment.workCertificatePath1);
						String attrName = ImageUtils
								.getBitmapAttrName(mWorkCertificateFragment.workCertificatePath1);
						uploadImg(mPhoneNo, "certificate", content, attrName,
								imgIndex);
					} else if (workCounts == 1) {// 选择了一站上岗证
						if (mWorkCertificateFragment.workCertificatePath1 != null) {
							String content = ImageUtils
									.bitmapToString(mWorkCertificateFragment.workCertificatePath1);
							String attrName = ImageUtils
									.getBitmapAttrName(mWorkCertificateFragment.workCertificatePath1);
							uploadImg(mPhoneNo, "certificate", content,
									attrName, imgIndex);
						} else {
							String content = ImageUtils
									.bitmapToString(mWorkCertificateFragment.workCertificatePath1);
							String attrName = ImageUtils
									.getBitmapAttrName(mWorkCertificateFragment.workCertificatePath1);
							uploadImg(mPhoneNo, "certificate", content,
									attrName, imgIndex);
						}
					} else {// 没有选择上岗证
						if (imgs.size() > 2) {// 选择了其他图片
							String content = ImageUtils
									.bitmapToString(mOtherCertificateFragment.paths[0]);
							String attrName = ImageUtils
									.getBitmapAttrName(mOtherCertificateFragment.paths[0]);
							uploadImg(mPhoneNo, "other", content, attrName,
									imgIndex);
						} else {// 没有选择其他图片
							Utilities.showToast("注册成功了,\n请等待后台分配用户名和密码",
									activity);
						}

					}
				} else {
					Utilities.showToast(response.msg, activity);
				}
			}

			@Override
			public void onError(Request request, Exception e) {
				// TODO Auto-generated method stub
				Logr.e(request.toString());
				stopProgressDialog();
			}
		};
		OkHttpClientManager.postAsyn(SystemConfig.NEWIP, callback,
				gson.toJson(requester));
	}

	private void initAllImgPath() {
		// TODO Auto-generated method stub
		imgs.clear();// 以防出现重复添加的情况
		if (mIdCardFragment.idCardFrontPath != null) {
			imgs.add(mIdCardFragment.idCardFrontPath);
		}
		if (mIdCardFragment.idCardOppositePath != null) {
			imgs.add(mIdCardFragment.idCardOppositePath);
		}
		if (mWorkCertificateFragment.workCertificatePath1 != null) {
			imgs.add(mWorkCertificateFragment.workCertificatePath1);
			workCounts++;
		}
		if (mWorkCertificateFragment.workCertificatePath2 != null) {
			imgs.add(mWorkCertificateFragment.workCertificatePath2);
			workCounts++;
		}
		for (String path : mOtherCertificateFragment.paths) {
			if (path != null) {
				imgs.add(path);
			}
		}
		Logr.e("集合中的图片数量====" + imgs.size());
	}

	protected void uploadImg(String phone, String type, String content,
			String attrName, int index) {
		startProgressDialog("正在上传第" + (imgIndex + 1) + "张图片");
		Requester requester = new Requester();
		requester.cmd = 5;
		requester.body.put("phone", phone);
		requester.body.put("type", type);
		requester.body.put("content", content);
		requester.body.put("attrName", attrName);
		requester.body.put("index", index + "");
		ResultCallback<Bean> callback = new ResultCallback<Bean>() {

			@Override
			public void onError(Request request, Exception e) {
				// TODO Auto-generated method stub
				Logr.e(e.getMessage() + "\n" + request.toString());
			}

			@Override
			public void onResponse(Bean response) {
				// TODO Auto-generated method stub
				int st = response.st;
				if (st == 0) {
					stopProgressDialog();
					imgIndex++;
					Logr.e("当前图片角标==" + imgIndex + "==当前图片集合大小==" + imgs.size());
					if (imgIndex < imgs.size()) {// 图片尚未上传完
						if (workCounts == 0) {// 继续上传其他照片
							String content = ImageUtils.bitmapToString(imgs
									.get(imgIndex));
							String attrName = ImageUtils.getBitmapAttrName(imgs
									.get(imgIndex));
							uploadImg(mPhoneNo, "other", content, attrName,
									imgIndex);
						} else if (workCounts == 1) {// 开始上传其他照片
							String content = ImageUtils.bitmapToString(imgs
									.get(imgIndex));
							String attrName = ImageUtils.getBitmapAttrName(imgs
									.get(imgIndex));
							uploadImg(mPhoneNo, "other", content, attrName,
									imgIndex);
						} else {// 先上传上岗证
							if (imgIndex < 4) {// 4代表身份证和上岗证的数量 这里上岗证还没有上传完
								String content = ImageUtils.bitmapToString(imgs
										.get(imgIndex));
								String attrName = ImageUtils
										.getBitmapAttrName(imgs.get(imgIndex));
								uploadImg(mPhoneNo, "certificate", content,
										attrName, imgIndex);
							} else {
								String content = ImageUtils.bitmapToString(imgs
										.get(imgIndex));
								String attrName = ImageUtils
										.getBitmapAttrName(imgs.get(imgIndex));
								uploadImg(mPhoneNo, "other", content, attrName,
										imgIndex);
							}

						}
					} else {
						Utilities.showToast("图片上传完成", activity);
					}
				} else {
					// Utilities.showToast("", activity);
					Logr.e("上传图片出问题了==");
				}
			}
		};
		OkHttpClientManager.postAsyn(SystemConfig.NEWIP, callback,
				gson.toJson(requester));
	}

	@Override
	public void onRegOthCerFrgClick() {
		alertBuilder.setTitle("温馨提示").setMessage("您确认提交以上信息？")
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				})
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						startUpLoading();
					}
				}).show();

	}

	@Override
	public void onRegAddWorkCerFrgClick() {
		FragmentTransaction transaction = manager.beginTransaction();
		if (mOtherCertificateFragment == null) {
			mOtherCertificateFragment = new RegisterOtherCertificateFragment();
		}
		mOtherCertificateFragment.setRegOthFrgListener(this);
		if (!mOtherCertificateFragment.isAdded()) {
			transaction.add(R.id.fl_register_container,
					mOtherCertificateFragment,
					"RegisterOtherCertificateFragment");
			transaction.addToBackStack(OTHER);
			transaction.commit();
		} else {
			transaction.show(mOtherCertificateFragment);
			transaction.commit();
		}
	}

	@Override
	public void onRegAddIdCardFrgClick() {
		FragmentTransaction transaction = manager.beginTransaction();
		if (mWorkCertificateFragment == null) {
			mWorkCertificateFragment = new RegisterAddWorkCertificateFragment();
		}
		mWorkCertificateFragment.setRegAddWorkCerFrgClickListener(this);
		if (!mWorkCertificateFragment.isAdded()) {
			transaction.add(R.id.fl_register_container,
					mWorkCertificateFragment,
					"RegisterAddWorkCertificateFragment");
			transaction.addToBackStack(WORK);
			transaction.commit();
		} else {
			transaction.show(mWorkCertificateFragment);
			transaction.commit();
		}
	}

	@Override
	public void onRegAddPerEduWorkFrgClick() {
		FragmentTransaction transaction = manager.beginTransaction();
		if (mIdCardFragment == null) {
			mIdCardFragment = new RegisterAddIdCardFragment();
		}
		mIdCardFragment.setRegAddIdCardClickListener(this);
		if (!mIdCardFragment.isAdded()) {
			transaction.add(R.id.fl_register_container, mIdCardFragment,
					"RegisterAddIdCardFragment");
			transaction.addToBackStack(IDCARD);
			transaction.commit();
		} else {
			transaction.show(mIdCardFragment);
			transaction.commit();
		}
	}

	@Override
	public void onRegAddPerInfoFrgClick() {
		FragmentTransaction transaction = manager.beginTransaction();
		if (mPersonEduWorkFragment == null) {
			mPersonEduWorkFragment = new RegisterAddPersonEduAndWorkFragment();
		}
		mPersonEduWorkFragment.setRegAddPerEduWorkFrgClickListener(this);
		if (!mPersonEduWorkFragment.isAdded()) {
			transaction.add(R.id.fl_register_container, mPersonEduWorkFragment,
					"RegisterAddPersonEduAndWorkFragment");
			transaction.addToBackStack(EDU);
			transaction.commit();
		} else {
			transaction.show(mPersonEduWorkFragment);
			transaction.commit();
		}
	}

	@Override
	public void onRegFraBtnClick() {
		mPhoneNo = mRegisterFragment.mPhoneNo;
		FragmentTransaction transaction = manager.beginTransaction();
		if (mPersonInfoFragment == null) {
			mPersonInfoFragment = new RegisterAddPersonInfoFragment();
		}
		mPersonInfoFragment.setRegAddPerInfoFrgClickListener(this);
		if (!mPersonInfoFragment.isAdded()) {
			transaction.add(R.id.fl_register_container, mPersonInfoFragment,
					"RegisterAddPersonInfoFragment");
			transaction.addToBackStack(INFO);
			transaction.commit();
		} else {
			transaction.show(mPersonInfoFragment);
			transaction.commit();
		}
	}

	@Override
	public void onRegPriClick() {
		// TODO Auto-generated method stub
		FragmentTransaction transaction = manager.beginTransaction();
		if (mRegisterFragment == null) {
			mRegisterFragment = new RegisterFragment();
		}
		mRegisterFragment.setRegFraBtnClickListener(this);
		if (!mRegisterFragment.isAdded()) {
			transaction.add(R.id.fl_register_container, mRegisterFragment,
					"RegisterFragment");
			transaction.addToBackStack(PHONE);
			transaction.commit();
		} else {
			transaction.show(mRegisterFragment);
			transaction.commit();
		}

		// FragmentTransaction transaction = manager.beginTransaction();
		// if (mIdCardFragment == null) {
		// mIdCardFragment = new RegisterAddIdCardFragment();
		// Logr.e("IdCardFragment==实例化了");
		// }
		// mIdCardFragment.setRegAddIdCardClickListener(this);
		// if (!mIdCardFragment.isAdded()) {
		// transaction.add(R.id.fl_register_container,
		// mIdCardFragment,"RegisterAddIdCardFragment");
		// transaction.addToBackStack(null);
		// transaction.commit();
		// Logr.e("==RegPriClick=add=");
		// }else{
		// transaction.show(mIdCardFragment);
		// transaction.commit();
		// Logr.e("==RegPriClick=show=");
		// }
//		 FragmentTransaction transaction = manager.beginTransaction();
//		 if (mPersonInfoFragment == null) {
//		 mPersonInfoFragment = new RegisterAddPersonInfoFragment();
//		 }
//		 mPersonInfoFragment.setRegAddPerInfoFrgClickListener(this);
//		 if(!mPersonInfoFragment.isAdded()){
//		 transaction.add(R.id.fl_register_container,
//		 mPersonInfoFragment,"RegisterAddPersonInfoFragment");
//		 transaction.addToBackStack(null);
//		 transaction.commit();
//		 }else{
//		 transaction.show(mPersonInfoFragment);
//		 transaction.commit();
//		 }
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		mPhoneNo = savedInstanceState.getString("phone");
		Logr.e("==activity==onRestoreInstanceState===");
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Logr.e("===activity==onStart====");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Logr.e("==activity===onResume====");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Logr.e("==activity===onPause====");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Logr.e("===activity==onStop====");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Logr.e("==activity===onDestroy====");
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Logr.e("==activity==onRestory==");
	}

	@Override
	protected void onSaveInstanceState(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(arg0);
		arg0.putString("phone", mPhoneNo);
		Logr.e("==activity==onSaveInstanceState==");
	}

}
