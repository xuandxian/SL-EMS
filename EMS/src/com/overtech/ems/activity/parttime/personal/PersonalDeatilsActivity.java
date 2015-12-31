package com.overtech.ems.activity.parttime.personal;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.MyApplication;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.http.HttpEngine.Param;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.picasso.Picasso;
import com.overtech.ems.picasso.Transformation;
import com.overtech.ems.utils.ImageCacheUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.widget.dialogeffects.Effectstype;
import com.overtech.ems.widget.dialogeffects.NiftyDialogBuilder;
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
	private Button mDoExit;
	private Effectstype effect;
	private NiftyDialogBuilder dialogBuilder;
	private TextView mPhone;
	private ImageView avator;
	private TextView mId;
	private TextView mName;
	private TextView mCertificateNo;
	private TextView mRegisterDate;
	private RatingBar mRatingBar;
	private SharedPreferences sp;
	private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			String json=(String) msg.obj;
			try {
				JSONObject jsonObject=new JSONObject(json);
				JSONObject model=(JSONObject) jsonObject.get("model");
				String avatorUrl= model.getString("avatorUrl");
				float rate=(float) model.getDouble("employeeRate");
				String id= model.getString("id");
				String name= model.getString("name");
				String phone= model.getString("phoneNo");
				long registerTime=model.getLong("registerTime");
				String workNo=model.getString("workNo");
				
				if (avatorUrl == null || "".equals(avatorUrl)) {
					avator.setScaleType(ScaleType.FIT_XY);
					avator.setImageResource(STUB_ID);
				} else {
					//调用从网络中加载过来的图片
					Picasso.with(context).load(avatorUrl).placeholder(STUB_ID)
							.error(STUB_ID).config(DEFAULT_CONFIG)
							.transform(new Transformation() {
								//圆角图片的实现
								@Override
								public Bitmap transform(Bitmap source) {
									return ImageCacheUtils.toRoundBitmap(source);
								}

								@Override
								public String key() {
									// TODO Auto-generated method stub
									return null;
								}
							}).into(avator);

				}
				mPhone.setText(phone);
				mId.setText(id);
				mName.setText(name);
				mCertificateNo.setText(workNo);
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");
				mRegisterDate.setText(sdf.format(new Date(registerTime)));
				mRatingBar.setRating(rate);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stopProgressDialog();
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_personal_details);
		initViews();
		initEvents();
		startLoading();
	}

	private void startLoading() {
		startProgressDialog("正在加载...");
		Param param=new Param(Constant.LOGINNAME,sp.getString(SharedPreferencesKeys.CURRENT_LOGIN_NAME, null));
		Request request = httpEngine.createRequest(ServicesConfig.PERSONAL_ACCOUNT, param);
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
		dialogBuilder = NiftyDialogBuilder.getInstance(this);
		sp=((MyApplication)getApplication()).getSharePreference();
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mDoBack = (ImageView) findViewById(R.id.iv_headBack);
		mChangePhoneNo = (RelativeLayout) findViewById(R.id.rl_change_phoneNo);
		mDoExit = (Button) findViewById(R.id.btn_exit);
		mPhone=(TextView) findViewById(R.id.textView3);
		avator=(ImageView)findViewById(R.id.iv_avator);
		mId=(TextView)findViewById(R.id.tv_id);
		mName=(TextView)findViewById(R.id.tv_username);
		mCertificateNo=(TextView)findViewById(R.id.tv_certificate_no);
		mRegisterDate=(TextView)findViewById(R.id.tv_register_time);
		mRatingBar=(RatingBar)findViewById(R.id.ratingBar1);
		mHeadContent.setText("账号信息");
	}

	private void initEvents() {
		// 返回键
		mDoBack.setVisibility(View.VISIBLE);
		mDoBack.setOnClickListener(this);
		mChangePhoneNo.setOnClickListener(this);
		mDoExit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_headBack:
			this.finish();
			break;
		case R.id.rl_change_phoneNo:
			Intent intent = new Intent(PersonalDeatilsActivity.this,ChangePhoneNoVCActivity.class);
			String phone=mPhone.getText().toString();
			intent.putExtra("phone", phone);
			startActivity(intent);
			break;
		case R.id.btn_exit:
			Intent intent2 = new Intent(PersonalDeatilsActivity.this,LoginActivity.class);
			startActivity(intent2);
			finish();
			break;
		}
	}
}
