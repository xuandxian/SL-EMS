package com.overtech.ems.activity.common.register;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
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

import com.overtech.ems.R;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.EditTextWithDelete;

public class RegisterAddPersonInfoFragment extends Fragment implements
		OnClickListener {
	private View view;
	private Context mContext;
	private TextView mHeadTitle;
	private ImageView mDoBack;
	private Button mNext;
	private EditTextWithDelete mName;
	private EditTextWithDelete mIdNum;
	private EditTextWithDelete mWorkNum;
	private Spinner mCity;
	private Spinner mZone;
	public String nameContent = null;
	public String idNumContent = null;
	public String workNumContent = null;
	public String cityContent = null;
	public String zoneContent = null;
	private RegAddPerInfoFrgClickListener listener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_register_add_person_info,
				null);
		findViewById(view);
		return view;
	}

	/**
	 * 对所有输入的信息进行检查
	 * 
	 * @return
	 */

	public boolean isAllNotNull() {
		nameContent = mName.getText().toString().trim();
		idNumContent = mIdNum.getText().toString().trim();
		workNumContent = mWorkNum.getText().toString().trim();

		if (!TextUtils.isEmpty(nameContent) && !TextUtils.isEmpty(idNumContent)
				&& !TextUtils.isEmpty(workNumContent)
				&& !cityContent.equals("城市选择") && !zoneContent.equals("区域选择")) {
			return true;
		} else {
			Utilities.showToast("您还有信息没有输入，请检查后再试!", mContext);
			return false;
		}
	}

	private void findViewById(View v) {
		mHeadTitle = (TextView) v.findViewById(R.id.tv_headTitle);
		mDoBack = (ImageView) v.findViewById(R.id.iv_headBack);
		mNext = (Button) v.findViewById(R.id.btn_next_fragment);
		mName = (EditTextWithDelete) v.findViewById(R.id.et_register_add_name);
		mIdNum = (EditTextWithDelete) v
				.findViewById(R.id.et_register_add_id_card);
		mWorkNum = (EditTextWithDelete) v
				.findViewById(R.id.et_register_add_workno);
		mCity = (Spinner) v.findViewById(R.id.sp_add_city);
		mZone = (Spinner) v.findViewById(R.id.sp_add_zone);

		mHeadTitle.setText("基本信息");
		mDoBack.setVisibility(View.VISIBLE);
		mDoBack.setOnClickListener(this);
		mNext.setOnClickListener(this);

		mCity.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				cityContent = (String) mCity.getSelectedItem();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				cityContent = null;
			}

		});
		mZone.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				zoneContent = (String) mZone.getSelectedItem();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				zoneContent = null;
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
			if (listener != null) {
				listener.onRegAddPerInfoFrgClick();
			}
			break;

		default:
			break;
		}
	}

	public void setRegAddPerInfoFrgClickListener(
			RegAddPerInfoFrgClickListener listener) {
		this.listener = listener;
	}

	public interface RegAddPerInfoFrgClickListener {
		void onRegAddPerInfoFrgClick();
	}
}
