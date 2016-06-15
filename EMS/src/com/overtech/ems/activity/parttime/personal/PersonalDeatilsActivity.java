package com.overtech.ems.activity.parttime.personal;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.MyApplication;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.activity.parttime.personal.phoneno.ChangePhoneNoValidatePasswordActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.bean.CommonBean;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.http.HttpEngine.Param;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.picasso.Picasso;
import com.overtech.ems.picasso.Transformation;
import com.overtech.ems.utils.ImageUtils;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.bitmap.ImageLoader;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class PersonalDeatilsActivity extends BaseActivity implements
		OnClickListener {
	private final int STUB_ID = R.drawable.icon_personal_my;// 此处为了将ImageLoader里面的方法抽出来单独使用，而将里面的字段提出来
	private final Config DEFAULT_CONFIG = Config.RGB_565;// 同上
	private TextView mHeadContent;
	private ImageView mDoBack;
	private RelativeLayout mChangePhoneNo;
	private RelativeLayout mChangePassword;
	private Button mDoExit;
	private TextView mPhone;
	private ImageView avator;
	private TextView mId;
	private TextView mName;
	private TextView mCertificateNo;
	private TextView mRegisterDate;
	private RatingBar mRatingBar;
	private SharedPreferences sp;
	private String uid;
	private String certificate;
	private PersonalDeatilsActivity activity;
	private final String CHANGEPHONE = "0";
	private final String RESETPASSWORD = "1";
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.PERSONAL_DETAIL_SUCCESS:
				String json = (String) msg.obj;
				CommonBean bean = gson.fromJson(json, CommonBean.class);
				String avatorUrl = bean.body.avator;
				float rate = Float.parseFloat(bean.body.employeeRate);
				String id = bean.body.id;
				String name = bean.body.name;
				String phone = bean.body.phone;
				String registerTime = bean.body.registerTime;
				String workNo = bean.body.workNo;
				if (avatorUrl == null || "none".equals(avatorUrl)) {
					avator.setScaleType(ScaleType.FIT_XY);
					avator.setImageResource(STUB_ID);
				} else {
					ImageLoader.getInstance().displayImage(avatorUrl, avator,
							STUB_ID, STUB_ID, DEFAULT_CONFIG,
							new Transformation() {

								@Override
								public Bitmap transform(Bitmap source) {
									// TODO Auto-generated method stub
									return ImageUtils.toRoundBitmap(source);
								}

								@Override
								public String key() {
									// TODO Auto-generated method stub
									return "avator";
								}
							});
				}
				mPhone.setText(phone);
				mId.setText(id);
				mName.setText(name);
				mCertificateNo.setText(workNo);
				mRegisterDate.setText(registerTime);
				mRatingBar.setRating(rate);
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast((String) msg.obj, context);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast((String) msg.obj, context);
				break;
			default:
				break;
			}
			stopProgressDialog();
		}

		;
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		stackInstance.pushActivity(this);
		setContentView(R.layout.activity_personal_details);
		initViews();
		initEvents();
		startLoading();
	}

	private void startLoading() {
		startProgressDialog("正在加载...");
		Requester requester = new Requester();
		requester.uid = uid;
		requester.certificate = certificate;
		requester.cmd = 20071;
		Request request = httpEngine.createRequest(SystemConfig.NEWIP,
				gson.toJson(requester));
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Response arg0) throws IOException {
				Message msg = new Message();
				if (arg0.isSuccessful()) {
					msg.obj = arg0.body().string();
					msg.what = StatusCode.PERSONAL_DETAIL_SUCCESS;
					handler.sendMessage(msg);
				} else {
					msg.obj = "服务器异常";
					msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
					handler.sendMessage(msg);
				}
			}

			@Override
			public void onFailure(Request arg0, IOException arg1) {
				Message msg = new Message();
				msg.what = StatusCode.RESPONSE_NET_FAILED;
				msg.obj = "网络异常";
				handler.sendMessage(msg);
			}
		});
	}

	private void initViews() {
		sp = ((MyApplication) getApplication()).getSharePreference();
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mDoBack = (ImageView) findViewById(R.id.iv_headBack);
		mChangePhoneNo = (RelativeLayout) findViewById(R.id.rl_change_phoneNo);
		mDoExit = (Button) findViewById(R.id.btn_exit);
		mPhone = (TextView) findViewById(R.id.tv_personal_phone);
		mChangePassword = (RelativeLayout) findViewById(R.id.rl_change_password);
		avator = (ImageView) findViewById(R.id.iv_avator);
		mId = (TextView) findViewById(R.id.tv_id);
		mName = (TextView) findViewById(R.id.tv_username);
		mCertificateNo = (TextView) findViewById(R.id.tv_certificate_no);
		mRegisterDate = (TextView) findViewById(R.id.tv_register_time);
		mRatingBar = (RatingBar) findViewById(R.id.ratingBar1);
		mHeadContent.setText("账号信息");
		activity = PersonalDeatilsActivity.this;
		uid = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.UID, "");
		certificate = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.CERTIFICATED, "");
	}

	private void initEvents() {
		// 返回键
		mDoBack.setVisibility(View.VISIBLE);
		mDoBack.setOnClickListener(this);
		mChangePhoneNo.setOnClickListener(this);
		mChangePassword.setOnClickListener(this);
		mDoExit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_headBack:
			this.finish();
			break;
		case R.id.rl_change_phoneNo:
			Intent intent = new Intent(PersonalDeatilsActivity.this,
					ChangePhoneNoValidatePasswordActivity.class);
			String phone = mPhone.getText().toString();
			intent.putExtra("flag", CHANGEPHONE);
			intent.putExtra("phone", phone);
			startActivity(intent);
			break;
		case R.id.rl_change_password:
			Intent intent2 = new Intent(PersonalDeatilsActivity.this,
					ChangePhoneNoValidatePasswordActivity.class);
			String phone2 = mPhone.getText().toString();
			intent2.putExtra("flag", RESETPASSWORD);
			intent2.putExtra("phone", phone2);
			startActivity(intent2);
			break;

		case R.id.btn_exit:
			Intent intent3 = new Intent(PersonalDeatilsActivity.this,
					LoginActivity.class);
			startActivity(intent3);
			stackInstance.popAllActivitys();

			break;
		}
	}
}
