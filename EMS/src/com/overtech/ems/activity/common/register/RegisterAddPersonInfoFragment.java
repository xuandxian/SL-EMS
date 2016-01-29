package com.overtech.ems.activity.common.register;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.overtech.ems.R;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.EditTextWithDelete;
import com.overtech.ems.widget.TimeButton;

public class RegisterAddPersonInfoFragment extends Fragment implements OnClickListener {
	private View view;
	private Context mContext;
	private TextView mRegisterPhone;
	private TextView mHeadTitle;
	private ImageView mDoBack;
	private EditTextWithDelete mValicateCode;
	private TimeButton mGetValicateCode;
	private Button mSubmitCode;
	private Button mNext;
	private EditTextWithDelete mName;
	private EditTextWithDelete mIdNum;
	private EditTextWithDelete mWorkNum;
	private Spinner mCity;
	private Spinner mZone;
	private String codeContent=null;
	private String mPhoneNo=null;
	
	public String nameContent=null;
	public String idNumContent=null;
	public String workNumContent=null;
	public String cityContent=null;
	public String zoneContent=null;
	
	private EventHandler eh;
	private RegAddPerInfoFrgClickListener listener;
	/**
	 * 判断验证码是否正确
	 */
	private boolean isCorrect=false;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext=activity;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view=inflater.inflate(R.layout.fragment_register_add_person_info, null);
		mPhoneNo=((RegisterActivity)getActivity()).mPhoneNo;
		eh= new EventHandler() {
			@Override
			public void afterEvent(int event, int result, Object data) {

				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}
		};
		SMSSDK.registerEventHandler(eh);//注册短信回调
		findViewById(view);
		
		return view;
	}
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int event = msg.arg1;
			int result = msg.arg2;
			Object data = msg.obj;
			if (result == SMSSDK.RESULT_COMPLETE) {
				if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
					mRegisterPhone.setText("验证码已发送到手机" + mPhoneNo);
					Utilities.showToast("验证码已发送", mContext);
				}else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
					isCorrect=true;
					Utilities.showToast("验证码正确", mContext);
				}
			} else {
				try {
					Throwable throwable=(Throwable)data;
					throwable.printStackTrace();
					JSONObject object=new JSONObject(throwable.getMessage());
					int status=object.optInt("status");
					Utilities.showToast("验证码错误："+status, mContext);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
		}
	};
	/**
	 * 对所有输入的信息进行检查
	 * @return
	 */

	public boolean isAllNotNull(){
		codeContent=mValicateCode.getText().toString().trim();
		nameContent=mName.getText().toString().trim();
		idNumContent=mIdNum.getText().toString().trim();
		workNumContent=mWorkNum.getText().toString().trim();
		
		if (isCorrect){
			if(!TextUtils.isEmpty(nameContent)&&!TextUtils.isEmpty(idNumContent)&&!TextUtils.isEmpty(workNumContent)&&!cityContent.equals("城市选择")&&!zoneContent.equals("区域选择")){
				return true;
			}else{
				Utilities.showToast("您还有信息没有输入，请检查后再试!", mContext);
				return false;
			}
		}else{
			Utilities.showToast("验证码错误", mContext);
			return false;

		}
	}
	
	private void findViewById(View v) {
		mHeadTitle=(TextView)v.findViewById(R.id.tv_headTitle);
		mDoBack=(ImageView)v.findViewById(R.id.iv_headBack);
		mNext=(Button)v.findViewById(R.id.btn_next_fragment);
		mRegisterPhone=(TextView) v.findViewById(R.id.register_send_phone);
		mValicateCode=(EditTextWithDelete) v.findViewById(R.id.et_valicate_code);
		mGetValicateCode=(TimeButton) v.findViewById(R.id.btn_get_valicate_code);
		mSubmitCode=(Button) v.findViewById(R.id.btn_valicate_code);
		mName=(EditTextWithDelete) v.findViewById(R.id.et_register_add_name);
		mIdNum=(EditTextWithDelete) v.findViewById(R.id.et_register_add_id_card);
		mWorkNum=(EditTextWithDelete) v.findViewById(R.id.et_register_add_workno);
		mCity=(Spinner) v.findViewById(R.id.sp_add_city);
		mZone=(Spinner) v.findViewById(R.id.sp_add_zone);
		
		mHeadTitle.setText("基本信息");
		mDoBack.setVisibility(View.VISIBLE);
		mDoBack.setOnClickListener(this);
		mNext.setOnClickListener(this);
		mGetValicateCode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mGetValicateCode.setTextAfter("秒后重新获取").setTextBefore("重新获取验证码").setLenght(60*1000);//为TimeButton设置点击前后的显示以及时间
				SMSSDK.getVerificationCode("86", mPhoneNo);//获取短信验证码
				isCorrect=false;//将验证结果置为false，需要再次判断
			}
		});
		mSubmitCode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				codeContent=mValicateCode.getText().toString().trim();
				SMSSDK.submitVerificationCode("86", mPhoneNo, codeContent);
				Utilities.showToast("验证码有三次有效次数", mContext);
			}
		});
		mCity.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				cityContent=(String) mCity.getSelectedItem();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				cityContent=null;
			}
			
		});
		mZone.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				zoneContent=(String) mZone.getSelectedItem();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				zoneContent=null;
			}
		});
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.iv_headBack:
			getActivity().onBackPressed();
			break;
		case R.id.btn_next_fragment:
			if(listener!=null){
				listener.onRegAddPerInfoFrgClick();
			}
			break;

		default:
			break;
		}
	}
	public void setRegAddPerInfoFrgClickListener(RegAddPerInfoFrgClickListener listener){
		this.listener=listener;
	}
	public interface RegAddPerInfoFrgClickListener{
		void onRegAddPerInfoFrgClick();
	}
}
