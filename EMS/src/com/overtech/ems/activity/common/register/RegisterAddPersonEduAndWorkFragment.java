package com.overtech.ems.activity.common.register;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.adapter.ElevatorBrandAdapter;
import com.overtech.ems.entity.bean.ZoneBean.Zone;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.EditTextWithDelete;
import com.overtech.ems.widget.popwindow.DimPopupWindow;

public class RegisterAddPersonEduAndWorkFragment extends Fragment implements
		OnClickListener {
	private View view;
	private Context mContext;
	private Button mNext;
	private AppCompatTextView tvElevatorBrand;
	private AppCompatTextView tvEnterTime;
	private Spinner mEdu;
	private EditTextWithDelete mCurrWork;
	private DimPopupWindow mPopupWindow;
	private ElevatorBrandAdapter adapter;
	private GridView mGridView;
	private Button mConfirm;
	private Button mCancle;
	/**
	 * 记录选择的电梯品牌
	 */
	private HashMap<Integer, Boolean> isSelected;
	private StringBuilder mCheckedMessage;
	private TextView mWorkTime;
	private RegAddPerEduWorkFrgClickListener listener;
	private String[] data = { "日立", "广日", "上海三菱", "日本三菱", "通力", "巨人通力", "奥的斯",
			"西子奥的斯", "西奥", "迅达", "许昌西继", "东芝", "蒂森克虏伯", "富士达", "西尼", "上海富士",
			"华升富士达", "浦东开灵", "长江斯迈普", "三荣", "永大", "现代", "华特", "爱登堡", "新时达",
			"崇友", "德胜米高", "阿尔法", "上海房屋设备", "席尔诺", "森赫（原莱茵）", "大连星玛", "博林特",
			"三洋", "江南嘉捷", "扬州三星", "东南", "康力", "宏大", "京都", "曼隆", "巨立", "帝奥",
			"德奥", "霍普曼", "怡达", "快速", "沃克斯", "恒达富士", "西屋", "铃木", "菱王", " 其他" };
	private Dialog dialog;
	private Calendar c = null;

	public String eduLevel = null;
	public String workUnit = null;
	public String enterTime = null;
	public String elevatorBrand = null;
	public String workYears = null;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			eduLevel = savedInstanceState.getString("eduLevel");
			workUnit = savedInstanceState.getString("workUnit");
			enterTime = savedInstanceState.getString("enterTime");
			elevatorBrand = savedInstanceState.getString("elevatorBrand");
			workYears = savedInstanceState.getString("workYears");
		}
		view = inflater.inflate(
				R.layout.fragment_register_add_person_edu_and_work, null);
		findViewById(view);
		init();
		dealElevator();
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putString("eduLevel", eduLevel);
		outState.putString("workUnit", workUnit);
		outState.putString("enterTime", enterTime);
		outState.putString("elevatorBrand", elevatorBrand);
		outState.putString("workYears", workYears);
	}

	private void init() {
		mNext.setOnClickListener(this);
		tvEnterTime.setOnClickListener(this);
		tvElevatorBrand.setOnClickListener(this);
	}

	// 记录选择的时间
	private int selYear;
	private int selMonth;
	private int selDay;

	protected void showDialog() {
		if (dialog == null) {
			dialog = new DatePickerDialog(mContext,
					new DatePickerDialog.OnDateSetListener() {
						public void onDateSet(DatePicker dp, int year,
								int month, int dayOfMonth) {
							tvEnterTime.setText(year + "-" + (month + 1) + "-"
									+ dayOfMonth);
							selYear = year;
							selMonth = month + 1;
							selDay = dayOfMonth;
						}
					}, c.get(Calendar.YEAR), // 传入年份
					c.get(Calendar.MONTH), // 传入月份
					c.get(Calendar.DAY_OF_MONTH) // 传入天数
			);
		}
		dialog.show();
		// 此处的监听是为了当选择时间是未来的时间时候，就得重新选择
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				int info = dealTime();
				if (info != -1) {
					mWorkTime.setText(info + "年");
					workYears = info + "";// 工作年限 用于提交给后台
				} else {
					mWorkTime.setText("工作年限");
					showDialog();
				}
			}

			private int dealTime() {

				// 当前的年月日
				int year = Calendar.getInstance().get(Calendar.YEAR);
				int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
				int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

				int workYear = year - selYear;
				int workMonth = month - selMonth;
				int workDay = day - selDay;
				if (workYear < 0) {
					Utilities.showToast("你确定不是穿越回来的？请重选", mContext);
					return -1;
				} else if (workYear == 0) {
					if (workMonth < 0) {
						Utilities.showToast("你确定不是穿越回来的？请重选", mContext);
						return -1;
					}
					if (workMonth == 0) {
						if (workDay < 0) {
							Utilities.showToast("你确定不是穿越回来的?请重选", mContext);
							return -1;
						}
					}
					return 1;
				} else {
					return workYear;
				}
			}
		});
	}

	private void dealElevator() {
		if (adapter != null) {
			mCheckedMessage = new StringBuilder();
			if (!isSelected.isEmpty()) {
				Iterator<Map.Entry<Integer, Boolean>> iterator = isSelected
						.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<Integer, Boolean> brand = iterator.next();
					mCheckedMessage.append(data[brand.getKey()] + " ");
				}
			}
			if (TextUtils.isEmpty(mCheckedMessage)) {
				tvElevatorBrand.setText("电梯品牌");
			} else {
				tvElevatorBrand.setText(mCheckedMessage.toString());
			}
		}
	}

	// 选择电梯品牌
	protected void showPopupWindow() {
		if (mPopupWindow == null) {
			mPopupWindow = new DimPopupWindow(mContext);
			View view = LayoutInflater.from(mContext).inflate(
					R.layout.register_gridview_elevator_brand, null);
			mPopupWindow.setContentView(view);
			mGridView = (GridView) view.findViewById(R.id.gridViewElevator);
			mConfirm = (Button) view.findViewById(R.id.bt_elevator_confirm);
			mCancle = (Button) view.findViewById(R.id.bt_elevator_cancle);
			adapter = new ElevatorBrandAdapter(data, mContext);
			mGridView.setAdapter(adapter);
			isSelected = adapter.isSelected;
			mPopupWindow.setOutsideTouchable(false);
			mGridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					Log.e("注册", position + "======");
					CheckBox elevator = (CheckBox) view;
					if (elevator.isChecked()) {
						elevator.setChecked(false);
						isSelected.remove(position);
					} else {
						elevator.setChecked(true);
						isSelected.put(position, true);
					}
				}
			});

			mConfirm.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dealElevator();
					mPopupWindow.dismiss();
				}
			});
			mCancle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mPopupWindow.dismiss();
				}
			});
		}
		mPopupWindow.showAtLocation(tvElevatorBrand, Gravity.CENTER, 0, 0);

	}

	private void findViewById(View v) {
		c = Calendar.getInstance();
		mNext = (Button) v.findViewById(R.id.btn_next_fragment);
		mEdu = (Spinner) v.findViewById(R.id.sp_add_education);
		mCurrWork = (EditTextWithDelete) v
				.findViewById(R.id.et_register_add_name);
		tvEnterTime = (AppCompatTextView) v.findViewById(R.id.tv_enter_time);
		tvElevatorBrand = (AppCompatTextView) v
				.findViewById(R.id.tv_elevator_message);
		mWorkTime = (TextView) v.findViewById(R.id.et_register_add_workno);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.education));
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mEdu.setAdapter(adapter);
		mEdu.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				eduLevel = (String) mEdu.getSelectedItem();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				eduLevel = null;
			}
		});
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.tv_elevator_message:
			showPopupWindow();
			break;
		case R.id.tv_enter_time:
			showDialog();
			break;

		case R.id.btn_next_fragment:
			if (eduLevel.equals("学历")) {
				Utilities.showToast("你还没有选择学历", mContext);
				return;
			}
			workUnit = mCurrWork.getText().toString().trim();
			if (TextUtils.isEmpty(workUnit)) {
				Utilities.showToast("工作单位不能为空", mContext);
				return;
			}
			enterTime = tvEnterTime.getText().toString().trim();
			if (TextUtils.isEmpty(enterTime)) {
				Utilities.showToast("入行时间不能为空", mContext);
				return;
			}
			elevatorBrand = tvElevatorBrand.getText().toString().trim();
			if (elevatorBrand.equals("电梯品牌")) {
				Utilities.showToast("您还没有选择电梯品牌", mContext);
				return;
			}

			if (listener != null) {
				listener.onRegAddPerEduWorkFrgClick();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewStateRestored(savedInstanceState);
		if (workYears != null) {
			mWorkTime.setText(workYears + "年");
		}
	}

	public void setRegAddPerEduWorkFrgClickListener(
			RegAddPerEduWorkFrgClickListener listener) {
		this.listener = listener;
	}

	public interface RegAddPerEduWorkFrgClickListener {
		void onRegAddPerEduWorkFrgClick();
	}
}
