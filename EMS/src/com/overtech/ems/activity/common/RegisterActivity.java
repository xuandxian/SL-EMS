package com.overtech.ems.activity.common;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.MyApplication;
import com.overtech.ems.activity.common.fragment.RegisterAddIdCardFragment;
import com.overtech.ems.activity.common.fragment.RegisterAddIdCardFragment.RegAddIdCardFrgClickListener;
import com.overtech.ems.activity.common.fragment.RegisterAddPersonEduAndWorkFragment;
import com.overtech.ems.activity.common.fragment.RegisterAddPersonEduAndWorkFragment.RegAddPerEduWorkFrgClickListener;
import com.overtech.ems.activity.common.fragment.RegisterAddPersonInfoFragment;
import com.overtech.ems.activity.common.fragment.RegisterAddPersonInfoFragment.RegAddPerInfoFrgClickListener;
import com.overtech.ems.activity.common.fragment.RegisterAddWorkCertificateFragment;
import com.overtech.ems.activity.common.fragment.RegisterAddWorkCertificateFragment.RegAddWorkCerFrgClickListener;
import com.overtech.ems.activity.common.fragment.RegisterFragment;
import com.overtech.ems.activity.common.fragment.RegisterFragment.RegFraBtnClickListener;
import com.overtech.ems.activity.common.fragment.RegisterOtherCertificateFragment;
import com.overtech.ems.activity.common.fragment.RegisterOtherCertificateFragment.RegOthCerFrgListener;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.entity.parttime.Employee;
import com.overtech.ems.http.HttpEngine.Param;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class RegisterActivity extends BaseActivity implements
		RegFraBtnClickListener, RegAddPerInfoFrgClickListener,
		RegAddPerEduWorkFrgClickListener, RegAddIdCardFrgClickListener,
		RegAddWorkCerFrgClickListener, RegOthCerFrgListener {
	private FragmentManager manager;
	private RegisterFragment mRegisterFragment;
	private RegisterAddPersonInfoFragment mPersonInfoFragment;
	private RegisterAddPersonEduAndWorkFragment mPersonEduWorkFragment;
	private RegisterAddIdCardFragment mIdCardFragment;
	private RegisterAddWorkCertificateFragment mWorkCertificateFragment;
	private RegisterOtherCertificateFragment mOtherCertificateFragment;
	private final String TAG = "Fragment";

	public String mPhoneNo;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.REGISTER_SUCCESS:
				String json = (String) msg.obj;
				Log.e("==上传与服务器交互的结果==", json);
				try {
					JSONObject jsonObject = new JSONObject(json);
					String success = jsonObject.getString("success");
					if (success.equals("true")) {
						Utilities.showToast("恭喜你注册成功，请等待公司为你分配账户和密码！！！",
								context);
						finish();
					} else if (success.equals("false")) {
						Utilities.showToast(jsonObject.getString("msg"),
								context);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				stopProgressDialog();
				finish();
				break;
			case StatusCode.REGISTER_FAILED:
				Utilities.showToast((String) msg.obj, context);
				stopProgressDialog();
				break;

			default:
				break;
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		if (mRegisterFragment == null) {
			mRegisterFragment = new RegisterFragment();
		}
		mRegisterFragment.setRegFraBtnClickListener(this);
		transaction.add(R.id.fl_register_container, mRegisterFragment,
				"REGISTER");// 将当前fragment添加进栈
		transaction.commit();
	}

	/**
	 * 上传用户信息
	 */
	protected void startUpLoading(String json) {
		File[] files;
		String[] fileKeys;
		// 其他证书可有可无需要对其进行判断
		if (mOtherCertificateFragment.otherCertificatePath == null) {
			files = new File[3];
			files[0] = new File(mIdCardFragment.idCardFrontPath);
			files[1] = new File(mIdCardFragment.idCardOppositePath);
			files[2] = new File(mWorkCertificateFragment.workCertificatePath);
			fileKeys = new String[3];
			fileKeys[0] = "frontIdCard";
			fileKeys[1] = "oppositeIdCard";
			fileKeys[2] = "workCertificate";
		} else {
			files = new File[4];
			files[0] = new File(mIdCardFragment.idCardFrontPath);
			files[1] = new File(mIdCardFragment.idCardOppositePath);
			files[2] = new File(mWorkCertificateFragment.workCertificatePath);
			files[3] = new File(mOtherCertificateFragment.otherCertificatePath);
			fileKeys = new String[4];
			fileKeys[0] = "frontIdCard";
			fileKeys[1] = "oppositeIdCard";
			fileKeys[2] = "workCertificate";
			fileKeys[3] = "otherCertificate";
		}
		Param param = new Param(Constant.PERSONINFO, json);
		try {
			Request request = httpEngine.createRequest(ServicesConfig.REGISTER,
					files, fileKeys, param);
			Call call = httpEngine.createRequestCall(request);
			call.enqueue(new Callback() {

				@Override
				public void onResponse(Response response) throws IOException {
					Message msg = new Message();
					if (response.isSuccessful()) {
						msg.what = StatusCode.REGISTER_SUCCESS;
						msg.obj = response.body().string();
					} else {
						msg.what = StatusCode.REGISTER_FAILED;
						msg.obj = "服务器异常，请重新上传";
					}
					handler.sendMessage(msg);

				}

				@Override
				public void onFailure(Request arg0, IOException arg1) {
					Message msg = new Message();
					msg.what = StatusCode.REGISTER_FAILED;
					msg.obj = "网络异常，请重新上传";
					handler.sendMessage(msg);
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 获取用户的注册信息
	 */

	protected String getAllMessage() {
		Employee employee = new Employee();
		// 获取个人信息页面的信息
		employee.setPhoneNo(mPhoneNo);
		employee.setName(mPersonInfoFragment.nameContent);
		employee.setIdcardNo(mPersonInfoFragment.idNumContent);
		employee.setWorkNo(mPersonInfoFragment.workNumContent);
		employee.setCity(mPersonInfoFragment.cityContent);
		employee.setZone(mPersonInfoFragment.zoneContent);

		employee.setEduLevel(mPersonEduWorkFragment.eduLevel);
		employee.setWorkUnit(mPersonEduWorkFragment.workUnit);
		employee.setEntryTime(mPersonEduWorkFragment.enterTime);
		employee.setWorkYears(mPersonEduWorkFragment.workYears);
		employee.setElevatorBrand(mPersonEduWorkFragment.elevatorBrand);
		Gson gson = new Gson();
		String employeeJson = gson.toJson(employee);
		Log.e("++++++测试实体类的json+++++++", employeeJson);
		return employeeJson;
	}

	@Override
	public void onRegOthCerFrgClick() {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(this).setTitle("提醒").setMessage("确认提交以上信息")
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						startProgressDialog("正在上传...");
						String personJson = getAllMessage();
						startUpLoading(personJson);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	@Override
	public void onRegAddWorkCerFrgClick() {
		// TODO Auto-generated method stub
		if (mWorkCertificateFragment.isAllNotNull()) {
			// if(true){
			FragmentTransaction transaction = manager.beginTransaction();
			if (mOtherCertificateFragment == null) {
				mOtherCertificateFragment = new RegisterOtherCertificateFragment();
			}
			mOtherCertificateFragment.setRegOthFrgListener(this);
			transaction.add(R.id.fl_register_container,
					mOtherCertificateFragment);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	@Override
	public void onRegAddIdCardFrgClick() {
		// TODO Auto-generated method stub
		if (mIdCardFragment.isAllNotNull()) {
			// if(true){
			FragmentTransaction transaction = manager.beginTransaction();
			if (mWorkCertificateFragment == null) {
				mWorkCertificateFragment = new RegisterAddWorkCertificateFragment();
			}
			mWorkCertificateFragment.setRegAddWorkCerFrgClickListener(this);
			transaction.add(R.id.fl_register_container,
					mWorkCertificateFragment);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	@Override
	public void onRegAddPerEduWorkFrgClick() {
		// TODO Auto-generated method stub
		if (mPersonEduWorkFragment.isAllNotNull()) {
			// if(true){
			FragmentTransaction transaction = manager.beginTransaction();
			if (mIdCardFragment == null) {
				mIdCardFragment = new RegisterAddIdCardFragment();
			}
			mIdCardFragment.setRegAddIdCardClickListener(this);
			transaction.add(R.id.fl_register_container, mIdCardFragment);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	@Override
	public void onRegAddPerInfoFrgClick() {
		// TODO Auto-generated method stub
		if (mPersonInfoFragment.isAllNotNull()) {
			// if(true){
			FragmentTransaction transaction = manager.beginTransaction();
			if (mPersonEduWorkFragment == null) {
				mPersonEduWorkFragment = new RegisterAddPersonEduAndWorkFragment();
			}
			mPersonEduWorkFragment.setRegAddPerEduWorkFrgClickListener(this);
			transaction.add(R.id.fl_register_container, mPersonEduWorkFragment);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	@Override
	public void onRegFraBtnClick() {
		// TODO Auto-generated method stub
		if (mRegisterFragment.isCorrect()) {
			// if(true){
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
	}

}
