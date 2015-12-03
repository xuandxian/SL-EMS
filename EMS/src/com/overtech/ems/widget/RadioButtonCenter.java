package com.overtech.ems.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RadioButton;

public class RadioButtonCenter extends RadioButton {
	
	private Drawable buttonDrawable;
	
	public RadioButtonCenter(Context context) {
		this(context, null);
	}

	public RadioButtonCenter(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public RadioButtonCenter(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		buttonDrawable=getCompoundDrawables()[1];
		if (null!=buttonDrawable) {
			buttonDrawable.setBounds(0,0,buttonDrawable.getIntrinsicWidth()/5,buttonDrawable.getIntrinsicHeight());
		}
	}
}
