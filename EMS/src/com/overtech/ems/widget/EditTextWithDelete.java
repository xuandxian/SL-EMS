package com.overtech.ems.widget;

import com.overtech.ems.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

public class EditTextWithDelete extends AppCompatEditText implements
		OnFocusChangeListener, TextWatcher {
	private Drawable mClearDrawable;

	public EditTextWithDelete(Context context) {
		this(context, null);
	}

	public EditTextWithDelete(Context context, AttributeSet attrs) {
		// 这里构�?方法也很重要，不加这个很多属性不能再XML里面定义
		this(context, attrs, android.R.attr.editTextStyle);
	}

	public EditTextWithDelete(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		// 获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
		mClearDrawable = getCompoundDrawables()[2];
		if (mClearDrawable == null) {
			mClearDrawable = getResources().getDrawable(
					R.drawable.ic_search_delete_common);
		}
		mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(),
				mClearDrawable.getIntrinsicHeight());
		setClearIconVisible(false);
		setOnFocusChangeListener(this);
		addTextChangedListener(this);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (getCompoundDrawables()[2] != null) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				boolean touchable = event.getX() > (getWidth()
						- getPaddingRight() - mClearDrawable
							.getIntrinsicWidth())
						&& (event.getX() < ((getWidth() - getPaddingRight())));
				if (touchable) {
					this.setText("");
				}
			}
		}
		return super.onTouchEvent(event);
	}

	/* 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐�?*/
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			setClearIconVisible(getText().length() > 0);
		} else {
			setClearIconVisible(false);
		}
	}

	/*
	 * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
	 * 
	 * @param visible
	 */
	protected void setClearIconVisible(boolean visible) {
		Drawable right = visible ? mClearDrawable : null;
		setCompoundDrawables(getCompoundDrawables()[0],
				getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
	}

	/* 当输入框里面内容发生变化的时候回调的方法 */

	@Override
	public void onTextChanged(CharSequence s, int start, int count, int after) {
		setClearIconVisible(s.length() > 0);
		if (s instanceof Spannable) {
			Spannable spanText = (Spannable) s;
			Selection.setSelection(spanText, s.length());
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void afterTextChanged(Editable s) {
		
	}
	
	/*
	 *   设置晃动动画 
	 */
	public void setShakeAnimation() {
		this.setAnimation(shakeAnimation(5));
	}

	/* 
	 * 晃动动画 
	 * @param counts 1秒钟晃动多少�?* @return
	 */
	public static Animation shakeAnimation(int counts) {
		Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
		translateAnimation.setInterpolator(new CycleInterpolator(counts));
		translateAnimation.setDuration(1000);
		return translateAnimation;
	}
}
