package com.overtech.ems.activity.common.register;

import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

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
import com.overtech.ems.entity.bean.LoginBean;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.http.OkHttpClientManager;
import com.overtech.ems.http.OkHttpClientManager.ResultCallback;
import com.overtech.ems.utils.ImageUtils;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.dialogeffects.Effectstype;
import com.squareup.okhttp.Request;

public class RegisterActivity extends BaseActivity implements
		RegPriItemFrgBtnClickListener, RegFraBtnClickListener,
		RegAddPerInfoFrgClickListener, RegAddPerEduWorkFrgClickListener,
		RegAddIdCardFrgClickListener, RegAddWorkCerFrgClickListener,
		RegOthCerFrgListener {
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		activity = this;
		manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		if (mPrivacyItemFragment == null) {
			mPrivacyItemFragment = new RegisterPrivacyItemFragment();
		}
		mPrivacyItemFragment.setRegPriItemFrgBtnClickListener(this);
		transaction.add(R.id.fl_register_container, mPrivacyItemFragment);
		transaction.commit();
	}

	/**
	 * 上传用户信息
	 */
	protected void startUpLoading() {
		Logr.e(mIdCardFragment.idCardFrontPath);
		Logr.e(mIdCardFragment.idCardOppositePath);
		Logr.e(mWorkCertificateFragment.workCertificatePath);
		Logr.e(mOtherCertificateFragment.otherCertificatePath);
		startProgressDialog("上传中，请等待上传结果");
		Requester requester = new Requester();
		requester.cmd = 3;
		requester.body.put("phone", mPhoneNo);
		requester.body.put("name", mPersonInfoFragment.nameContent);
		requester.body.put("idcard", mPersonInfoFragment.idNumContent);
		requester.body.put("workNo", mPersonInfoFragment.workNumContent);
		requester.body.put("city", mPersonInfoFragment.cityContent);
		requester.body.put("zone", mPersonInfoFragment.zoneContent);
		requester.body.put("eduLevel", mPersonEduWorkFragment.eduLevel);
		requester.body.put("workUnit", mPersonEduWorkFragment.workUnit);
		requester.body.put("entryTime", mPersonEduWorkFragment.enterTime);
		requester.body.put("workYears", mPersonEduWorkFragment.workYears);
		requester.body.put("elevatorBrand",
				mPersonEduWorkFragment.elevatorBrand);

		HashMap<String, Object> front = new HashMap<String, Object>();// 身份证正面
		front.put("content",
				ImageUtils.bitmapToString(mIdCardFragment.idCardFrontPath));
		// front.put("content","123");
		front.put("attrName",
				ImageUtils.getBitmapAttrName(mIdCardFragment.idCardFrontPath));
		Logr.e(ImageUtils.getBitmapAttrName(mIdCardFragment.idCardFrontPath));
		requester.body.put("frontIdCard", front);

		HashMap<String, Object> opposite = new HashMap<String, Object>();// 身份证反面
		opposite.put("content",
				ImageUtils.bitmapToString(mIdCardFragment.idCardOppositePath));
		// opposite.put("content","123");
		opposite.put("attrName", ImageUtils
				.getBitmapAttrName(mIdCardFragment.idCardOppositePath));
		Logr.e(ImageUtils.getBitmapAttrName(mIdCardFragment.idCardOppositePath));
		requester.body.put("oppositeIdCard", opposite);

		HashMap<String, Object> workcertificate = new HashMap<String, Object>();// 工作证书
		workcertificate.put("content", ImageUtils
				.bitmapToString(mWorkCertificateFragment.workCertificatePath));
		// workcertificate.put("content", "123");
		workcertificate
				.put("attrName",
						ImageUtils
								.getBitmapAttrName(mWorkCertificateFragment.workCertificatePath));
		Logr.e(ImageUtils
				.getBitmapAttrName(mWorkCertificateFragment.workCertificatePath));
		requester.body.put("workCertificate", workcertificate);

		HashMap<String, Object> other = new HashMap<String, Object>();// 其他证书
		other.put("content", ImageUtils
				.bitmapToString(mOtherCertificateFragment.otherCertificatePath));
		// other.put("content","123");
		other.put(
				"attrName",
				ImageUtils
						.getBitmapAttrName(mOtherCertificateFragment.otherCertificatePath));
		Logr.e(ImageUtils
				.getBitmapAttrName(mOtherCertificateFragment.otherCertificatePath));
		requester.body.put("otherCertificate", other);

		// 工作证和 其他证书可有可无需要对其进行判断
		// if (mWorkCertificateFragment.workCertificatePath != null
		// && mOtherCertificateFragment.otherCertificatePath == null) {
		// files = new File[3];
		// files[0] = new File(mIdCardFragment.idCardFrontPath);
		// files[1] = new File(mIdCardFragment.idCardOppositePath);
		// files[2] = new File(mWorkCertificateFragment.workCertificatePath);
		//
		// fileKeys = new String[3];
		// fileKeys[0] = "frontIdCard";
		// fileKeys[1] = "oppositeIdCard";
		// fileKeys[2] = "workCertificate";
		//
		// } else if (mWorkCertificateFragment.workCertificatePath != null
		// && mOtherCertificateFragment.otherCertificatePath != null) {
		// files = new File[4];
		// files[0] = new File(mIdCardFragment.idCardFrontPath);
		// files[1] = new File(mIdCardFragment.idCardOppositePath);
		// files[2] = new File(mWorkCertificateFragment.workCertificatePath);
		// files[3] = new File(mOtherCertificateFragment.otherCertificatePath);
		//
		// fileKeys = new String[4];
		// fileKeys[0] = "frontIdCard";
		// fileKeys[1] = "oppositeIdCard";
		// fileKeys[2] = "workCertificate";
		// fileKeys[3] = "otherCertificate";
		// } else if (mWorkCertificateFragment.workCertificatePath == null
		// && mOtherCertificateFragment.otherCertificatePath != null) {
		// files = new File[3];
		// files[0] = new File(mIdCardFragment.idCardFrontPath);
		// files[1] = new File(mIdCardFragment.idCardOppositePath);
		// files[2] = new File(mOtherCertificateFragment.otherCertificatePath);
		//
		// fileKeys = new String[3];
		// fileKeys[0] = "frontIdCard";
		// fileKeys[1] = "oppositeIdCard";
		// fileKeys[2] = "otherCertificate";
		// } else {
		// files = new File[2];
		// files[0] = new File(mIdCardFragment.idCardFrontPath);
		// files[1] = new File(mIdCardFragment.idCardOppositePath);
		// fileKeys = new String[2];
		// fileKeys[0] = "frontIdCard";
		// fileKeys[1] = "oppositeIdCard";
		// }
		ResultCallback<LoginBean> callback = new ResultCallback<LoginBean>() {

			@Override
			public void onResponse(LoginBean response) {
				// TODO Auto-generated method stub
				if (response == null) {
					Utilities.showToast("上传失败，请重新尝试", activity);
					return;
				}
				int st = response.st;
				if (st == 0) {
					Utilities.showToast(response.msg, activity);
					finish();
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

	@Override
	public void onRegOthCerFrgClick() {
		Effectstype effect = Effectstype.Shake;
		dialogBuilder.withTitle("温馨提示").withTitleColor(R.color.main_primary)
				.withDividerColor("#11000000").withMessage("您确认提交以上信息？")
				.withMessageColor(R.color.main_primary)
				.withDialogColor("#FFFFFFFF").isCancelableOnTouchOutside(true)
				.withDuration(700).withEffect(effect)
				.withButtonDrawable(R.color.main_white).withButton1Text("取消")
				.withButton1Color("#DD47BEE9").withButton2Text("确认")
				.withButton2Color("#DD47BEE9")
				.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
					}
				}).setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
						startProgressDialog("正在上传...");
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
		transaction.add(R.id.fl_register_container, mOtherCertificateFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onRegAddIdCardFrgClick() {
		FragmentTransaction transaction = manager.beginTransaction();
		if (mWorkCertificateFragment == null) {
			mWorkCertificateFragment = new RegisterAddWorkCertificateFragment();
		}
		mWorkCertificateFragment.setRegAddWorkCerFrgClickListener(this);
		transaction.add(R.id.fl_register_container, mWorkCertificateFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onRegAddPerEduWorkFrgClick() {
		FragmentTransaction transaction = manager.beginTransaction();
		if (mIdCardFragment == null) {
			mIdCardFragment = new RegisterAddIdCardFragment();
		}
		mIdCardFragment.setRegAddIdCardClickListener(this);
		transaction.add(R.id.fl_register_container, mIdCardFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onRegAddPerInfoFrgClick() {
		FragmentTransaction transaction = manager.beginTransaction();
		if (mPersonEduWorkFragment == null) {
			mPersonEduWorkFragment = new RegisterAddPersonEduAndWorkFragment();
		}
		mPersonEduWorkFragment.setRegAddPerEduWorkFrgClickListener(this);
		transaction.add(R.id.fl_register_container, mPersonEduWorkFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onRegFraBtnClick() {
		mPhoneNo = mRegisterFragment.mPhoneNo;
		FragmentTransaction transaction = manager.beginTransaction();
		if (mPersonInfoFragment == null) {
			mPersonInfoFragment = new RegisterAddPersonInfoFragment();
		}
		mPersonInfoFragment.setRegAddPerInfoFrgClickListener(this);
		transaction.add(R.id.fl_register_container, mPersonInfoFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onRegPriClick() {
		// TODO Auto-generated method stub
		FragmentTransaction transaction = manager.beginTransaction();
		if (mRegisterFragment == null) {
			mRegisterFragment = new RegisterFragment();
		}
		mRegisterFragment.setRegFraBtnClickListener(this);
		transaction.add(R.id.fl_register_container, mRegisterFragment);
		transaction.addToBackStack(null);
		transaction.commit();

	}

}
