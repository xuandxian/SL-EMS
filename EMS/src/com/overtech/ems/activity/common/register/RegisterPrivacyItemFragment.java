package com.overtech.ems.activity.common.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;

public class RegisterPrivacyItemFragment extends BaseFragment implements
		OnClickListener {
	private TextView mTitle;
	private ImageView mBack;
	private CheckBox privacyCheckBox;
	private Button mNext;
	private RegPriItemFrgBtnClickListener listener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_register_privacy_item,
				null);
		findViewById(view);
		init();
		return view;
	}

	private void init() {
		// TODO Auto-generated method stub
		mTitle.setText("用户协议");
		mBack.setVisibility(View.VISIBLE);
		mBack.setOnClickListener(this);
		mNext.setOnClickListener(this);
		privacyCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean arg1) {
						// TODO Auto-generated method stub
						if (arg1) {
							mNext.setBackgroundResource(R.drawable.shape_button_visiable);
						} else {
							mNext.setBackgroundResource(R.drawable.shape_button_disable);
						}
					}
				});
	}

	private void findViewById(View view) {
		// TODO Auto-generated method stub
		mTitle = (TextView) view.findViewById(R.id.tv_headTitle);
		mBack = (ImageView) view.findViewById(R.id.iv_headBack);
		privacyCheckBox = (CheckBox) view.findViewById(R.id.cb_item_privacy);
		mNext = (Button) view.findViewById(R.id.btn_next_fragment);
	}

	public void setRegPriItemFrgBtnClickListener(
			RegPriItemFrgBtnClickListener listener) {
		this.listener = listener;
	}

	public interface RegPriItemFrgBtnClickListener {
		void onRegPriClick();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.iv_headBack:
			getActivity().onBackPressed();
			break;
		case R.id.btn_next_fragment:
			if (privacyCheckBox.isChecked()) {
				if (listener != null) {
					listener.onRegPriClick();
				}
			} else {

			}
			break;

		default:
			break;
		}
	}
}
