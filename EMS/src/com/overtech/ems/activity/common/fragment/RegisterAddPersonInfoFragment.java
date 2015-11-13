package com.overtech.ems.activity.common.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;

import com.overtech.ems.R;
import com.overtech.ems.activity.common.RegisterActivity;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.EditTextWithDelete;

public class RegisterAddPersonInfoFragment extends Fragment {
	private View view;
	private Context mContext;
	private EditTextWithDelete mValicateCode;
	private Button mGetValicateCode;
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
		findViewById(view);
		
		return view;
	}
	/**
	 * 对所有输入的信息进行检查
	 * @return
	 */
	public boolean isAllNotNull(){
		codeContent=mValicateCode.getText().toString().trim();
		nameContent=mName.getText().toString().trim();
		idNumContent=mIdNum.getText().toString().trim();
		workNumContent=mWorkNum.getText().toString().trim();
		
		Log.e("Fragment", codeContent+":"+nameContent+":"+idNumContent+":"+workNumContent+":"+cityContent+":"+zoneContent);
		if(!TextUtils.isEmpty(codeContent)&&!TextUtils.isEmpty(nameContent)&&!TextUtils.isEmpty(idNumContent)&&!TextUtils.isEmpty(workNumContent)&&!cityContent.equals("城市选择")&&!zoneContent.equals("区域选择")){
			return true;
		}else{
			Utilities.showToast("您还有信息没有输入，请检查后再试!", mContext);
			return false;
		}
	}
	
	private void findViewById(View v) {
		mValicateCode=(EditTextWithDelete) v.findViewById(R.id.et_valicate_code);
		mGetValicateCode=(Button) v.findViewById(R.id.btn_get_valicate_code);
		mName=(EditTextWithDelete) v.findViewById(R.id.et_register_add_name);
		mIdNum=(EditTextWithDelete) v.findViewById(R.id.et_register_add_id_card);
		mWorkNum=(EditTextWithDelete) v.findViewById(R.id.et_register_add_workno);
		mCity=(Spinner) v.findViewById(R.id.sp_add_city);
		mZone=(Spinner) v.findViewById(R.id.sp_add_zone);
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
