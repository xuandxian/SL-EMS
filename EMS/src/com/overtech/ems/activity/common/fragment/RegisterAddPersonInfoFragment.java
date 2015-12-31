package com.overtech.ems.activity.common.fragment;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.overtech.ems.R;
import com.overtech.ems.activity.common.RegisterActivity;
import com.overtech.ems.activity.common.ResetPasswordActivity;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.EditTextWithDelete;
import com.overtech.ems.widget.TimeButton;

import static cn.smssdk.framework.utils.R.getStringRes;

public class RegisterAddPersonInfoFragment extends Fragment {
	private View view;
	private Context mContext;
	private TextView mRegisterPhone;
	private EditTextWithDelete mValicateCode;
	private TimeButton mGetValicateCode;
	private Button mSubmitCode;
	private EditTextWithDelete mName;
	private EditTextWithDelete mIdNum;
	private EditTextWithDelete mWorkNum;
	private Spinner mCity;
	private Spinner mZone;
	private String codeContent=null;
	private String nameContent=null;
	private String idNumContent=null;
	private String workNumContent=null;
	private String cityContent=null;
	private String zoneContent=null;
	private Bundle bundle;
	private EventHandler eh;
	/**
	 * 判断验证码是否正确
	 */
	private boolean isCorrect=false;
	/**
	 * 是否开始验证验证码
	 */
	private boolean isStartValidate=false;
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mContext=activity;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view=inflater.inflate(R.layout.fragment_register_add_person_info, null);
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
		bundle=((RegisterActivity)this.getActivity()).getBundle();//获得activity公共的临时存储对象
		mRegisterPhone.setText("验证码已发送到手机" + (CharSequence) bundle.get("phone"));
		mGetValicateCode.performClick();//启动时默认执行一次‘60s后获取验证码’点击事件，让后台发送验证码
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	};
	/*@Override//将此处注释，因为之前的设计是考虑用户输入信息后又回退了，需要再次请求后台发送验证码，但现在认为如果在第一次用户已经验证正确后就不要再验证了
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if(hidden){
			
		}else{
			bundle=((RegisterActivity)this.getActivity()).getBundle();
			mRegisterPhone.setText("验证码已发送到手机"+(CharSequence) bundle.get("phone"));
			SMSSDK.getVerificationCode("86", (String)bundle.get("phone"));//当再次显示时请求发送验证码
			isCorrect=false;//再次请求验证码时，将结果置为false;
		}
	}*/
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
			if(isStartValidate){
				Utilities.showToast("正在验证验证码或验证码错误", mContext);
			}else{
				Utilities.showToast("您还没有提交验证码", mContext);
			}
			return false;

		}
	}
	public HashMap getPersonInfo(){
		HashMap<String,String> map=new HashMap<String, String>();
		map.put("nameContent", nameContent);
		map.put("idNumContent", idNumContent);
		map.put("workNumContent", workNumContent);
		map.put("cityContent", cityContent);
		map.put("zoneContent", zoneContent);
		return map;
	}
	
	private void findViewById(View v) {
		mRegisterPhone=(TextView) v.findViewById(R.id.register_send_phone);
		mValicateCode=(EditTextWithDelete) v.findViewById(R.id.et_valicate_code);
		mGetValicateCode=(TimeButton) v.findViewById(R.id.btn_get_valicate_code);
		mSubmitCode=(Button) v.findViewById(R.id.btn_valicate_code);
		mName=(EditTextWithDelete) v.findViewById(R.id.et_register_add_name);
		mIdNum=(EditTextWithDelete) v.findViewById(R.id.et_register_add_id_card);
		mWorkNum=(EditTextWithDelete) v.findViewById(R.id.et_register_add_workno);
		mCity=(Spinner) v.findViewById(R.id.sp_add_city);
		mZone=(Spinner) v.findViewById(R.id.sp_add_zone);
		mGetValicateCode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mGetValicateCode.setTextAfter("秒后重新获取").setTextBefore("重新获取验证码").setLenght(60*1000);//为TimeButton设置点击前后的显示以及时间
				SMSSDK.getVerificationCode("86", (String)bundle.get("phone"));//获取短信验证码
				isCorrect=false;//将验证结果置为false，需要再次判断
				isStartValidate=false;
			}
		});
		mSubmitCode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				codeContent=mValicateCode.getText().toString().trim();
				SMSSDK.submitVerificationCode("86", (String) bundle.get("phone"), codeContent);
				Utilities.showToast("验证码有三次有效次数", mContext);
				isStartValidate=true;
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
}
