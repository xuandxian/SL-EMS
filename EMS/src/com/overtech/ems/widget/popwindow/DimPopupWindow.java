package com.overtech.ems.widget.popwindow;

import com.overtech.ems.R;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
/**
 * 用以解决PopupWindow区域外背景变淡，以及Show，Hide时的动画效果
 * 暂时只支持ShowAtLocation的区域外背景变淡，ShowAsDrowDown对应的View以下的区域背景变淡
 * 不要用Dismiss，用Hide
 *
 */
public class DimPopupWindow extends PopupWindow {
    
    private Context mContext;
    
    private RelativeLayout mInternalView;
    private View mExternalView;
    
    private int mDimColor = R.color.bg_mask;
    private Animation mInAnimation;
    private Animation mOutAnimation;
    
    /**
     * Dismiss在Handler.post中执行，如果直接调dismiss，logcat中会有Attempting to destroy the window while drawing!（不是crash）
     */
    private Handler mHanlder = new Handler();
    
    /**
     * 默认开启动画
     */
    private boolean isAnimationable = true;
    
    public DimPopupWindow(Context context) {
        //2.3手机上要用该super的构造函数
        super(context);
        setWidth(LayoutParams.FILL_PARENT);
        setHeight(LayoutParams.FILL_PARENT);
        mContext = context;
        setBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(mDimColor)));
        mInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.common_pop_in);
        mOutAnimation = AnimationUtils.loadAnimation(mContext, R.anim.common_pop_out);
        mInternalView = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.layout_dim_pop, null);
        mInternalView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                hide();
            }
        });
        mOutAnimation.setAnimationListener(new AnimationListener() {
            
            @Override
            public void onAnimationStart(Animation animation) {
                
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {
                
            }
            
            @Override
            public void onAnimationEnd(Animation animation) {
                mHanlder.post(new Runnable() {
                    
                    @Override
                    public void run() {
                        dismiss();
                    }
                });
               
            }
        });
        setAnimationStyle(0);
        //否则有些手机上ListView的OnItemClick无法响应
        super.setFocusable(true);
    }
    
    public void setDimColor(int color) {
        mDimColor = color;
        setBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(mDimColor)));
    }
    
    public void setInAnimation(int anim) {
        mInAnimation = AnimationUtils.loadAnimation(mContext, anim);
    }
    
    public void setInAnimation(Animation anim) {
        if(anim != null) {
            mInAnimation = anim;
        }
    }
    
    public void setAnimationable(boolean value) {
        isAnimationable = value;
    }
    
    public void setOutAnimation(int anim) {
        mOutAnimation = AnimationUtils.loadAnimation(mContext, anim);
    }
    
    public void setOutAnimation(Animation anim) {
        if(anim != null) {
            mOutAnimation = anim;
        }
    }
    
    @Override
    public void setContentView(View contentView) {
        if(mInternalView != null) {
            mExternalView = contentView;
            mInternalView.addView(mExternalView);
        }
        super.setContentView(mInternalView);
    }

	public void setOutGravity(int gravity) {
		mInternalView.setGravity(gravity);
	}
    
    @Override
    public View getContentView() {
        return mExternalView;
    }
    
    @Override
    public void showAsDropDown(View anchor) {
        super.showAsDropDown(anchor);
        if (isAnimationable) {
            mExternalView.clearAnimation();
            mExternalView.startAnimation(mInAnimation);
        }
    }
    
    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        super.showAsDropDown(anchor, xoff, yoff);
        if (isAnimationable) {
            mExternalView.clearAnimation();
            mExternalView.startAnimation(mInAnimation);
        }
    }
    
    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        if (isAnimationable) {
            mExternalView.clearAnimation();
            mExternalView.startAnimation(mInAnimation);
        }
    }
    
    public void hide() {
        if(isAnimationable) {
            mExternalView.clearAnimation();
            mExternalView.startAnimation(mOutAnimation);
        } else {
            dismiss();
        }
    }
}

