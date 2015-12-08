package com.overtech.ems.activity.common.fragment;

import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.adapter.GridViewAdapter2;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.EditTextWithDelete;
import com.overtech.ems.widget.popwindow.DimPopupWindow;

public class RegisterAddPersonEduAndWorkFragment extends Fragment {
	private View view;
	private Context mContext;
	private ImageView mCalendar;
	private RelativeLayout mElevatorBrand;
	/**
	 * 入行时间
	 * */
	private EditText mEnterWorkTime;
	private TextView mElevator;
	private Spinner mEdu;
	private EditTextWithDelete mCurrWork;
	private DimPopupWindow mPopupWindow;
	private GridViewAdapter2 adapter;
	private GridView mGridView;
	private Button mConfirm;
	private Button mCancle;
	private TextView mWorkTime;
	private HashMap<Integer, Boolean> isSelected;
	private StringBuilder mCheckedMessage;
	private String[] data = { "日立", "广日", "上海三菱", "日本三菱", "通力", "巨人通力", "奥的斯",
			"西子奥的斯", "西奥", "迅达", "许昌西继", "东芝", "蒂森克虏伯", "富士达", "西尼", "上海富士",
			"华升富士达", "浦东开灵", "长江斯迈普", "三荣", "永大", "现代", "华特", "爱登堡", "新时达",
			"崇友", "德胜米高", "阿尔法", "上海房屋设备", "席尔诺", "森赫（原莱茵）", "大连星玛", "博林特",
			"三洋", "江南嘉捷", "扬州三星", "东南", "康力", "宏大", "京都", "曼隆", "巨立", "帝奥",
			"德奥", "霍普曼", "怡达", "快速", "沃克斯", "恒达富士", "西屋", "铃木", "菱王", " 其他" };
	private Dialog dialog;
	private Calendar c = null;

	private String eduContent = null;
	private String currWorkContent = null;
	private String enterWorkTimeContent = null;
	private String elevatorContent = null;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(
				R.layout.fragment_register_add_person_edu_and_work, null);
		findViewById(view);
		init();
		dealElevator();
		return view;
	}

	private void dealElevator() {
		if (adapter != null) {
			isSelected = adapter.getCheckBox();
			mCheckedMessage = new StringBuilder();
			for (int i = 0; i < data.length; i++) {
				if (isSelected.containsKey(i)) {
					mCheckedMessage.append(data[i] + "  ");
				}
			}
			if (TextUtils.isEmpty(mCheckedMessage)) {
				mElevator.setText("电梯品牌");
			} else {
				mElevator.setText(mCheckedMessage.toString());
			}
		}
	}

	/**
	 * 用于依赖的activity进行当前fragment进行内容判断
	 * 
	 * @return
	 */
	public boolean isAllNotNull() {
		currWorkContent = mCurrWork.getText().toString().trim();
		enterWorkTimeContent = mEnterWorkTime.getText().toString().trim();
		elevatorContent = mElevator.getText().toString().trim();

		Log.e("Fragment", currWorkContent + ":" + enterWorkTimeContent + ":"
				+ elevatorContent);
		if (!eduContent.equals("学历") && !TextUtils.isEmpty(currWorkContent)
				&& !TextUtils.isEmpty(enterWorkTimeContent)
				&& !elevatorContent.equals("电梯品牌")) {
			return true;
		} else {
			Utilities.showToast("您还有信息没有输入", mContext);
			return false;
		}
	}

	private void init() {
		mElevator.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showPopupWindow();
			}
		});

		mCalendar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 打开日历选择器
				showDialog();

			}
		});
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
							mEnterWorkTime.setText(year + "-" + (month + 1)
									+ "-" + dayOfMonth);
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

	protected void showPopupWindow() {
		mPopupWindow = new DimPopupWindow(mContext);
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.register_gridview_elevator_brand, null);
		mGridView = (GridView) view.findViewById(R.id.gridViewElevator);
		mConfirm = (Button) view.findViewById(R.id.bt_elevator_confirm);
		mCancle = (Button) view.findViewById(R.id.bt_elevator_cancle);
		adapter = new GridViewAdapter2(data, mContext);
		mGridView.setAdapter(adapter);
		mPopupWindow.setOutsideTouchable(false);
		mPopupWindow.setContentView(view);
		mPopupWindow.showAtLocation(mElevatorBrand, Gravity.RIGHT
				| Gravity.BOTTOM, 0, 0);

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

	private void findViewById(View v) {
		c = Calendar.getInstance();
		mEdu = (Spinner) v.findViewById(R.id.sp_add_education);
		mCurrWork = (EditTextWithDelete) v
				.findViewById(R.id.et_register_add_name);
		mCalendar = (ImageView) v.findViewById(R.id.imageView1);
		mEnterWorkTime = (EditText) v
				.findViewById(R.id.et_register_add_id_card);
		mElevatorBrand = (RelativeLayout) v
				.findViewById(R.id.rl_register_add_info_4);
		mElevator = (TextView) v.findViewById(R.id.tv_elevator_message);
		mWorkTime = (TextView) v.findViewById(R.id.et_register_add_workno);

		mEdu.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				eduContent = (String) mEdu.getSelectedItem();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				eduContent = null;
			}
		});
	}
}
