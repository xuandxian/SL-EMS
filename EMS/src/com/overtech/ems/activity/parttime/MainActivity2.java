package com.overtech.ems.activity.parttime;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Window;
import android.widget.FrameLayout;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.parttime.fragment.GrabTaskFragment;
import com.overtech.ems.activity.parttime.fragment.NearByFragment;
import com.overtech.ems.activity.parttime.fragment.TaskListFragment;
import com.overtech.ems.activity.parttime.fragment.PersonalZoneFragment;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * @author Tony
 * @description 主界面
 * @date 2015-10-05
 */
public class MainActivity2 extends BaseActivity {

    private FrameLayout mHomeContent;
    private RadioGroup mHomeRadioGroup;
    private RadioButton mHomeHomeRb;
    private RadioButton mHomeFindRb;
    private RadioButton mHomeSearchRb;
    private RadioButton mHomeProfileRb;
    private Fragment mGrabTaskFragment;
    private Fragment mTaskListFragment;
    private Fragment mPersonalZoneFragment;
    private Fragment mNearByFragment;

    static final int NUM_ITEMS = 4;//一共四个fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main2);
        initView();
    }

    protected void initView() {
        mHomeRadioGroup = (RadioGroup) findViewById(R.id.mHomeRadioGroup);  //底部的四个tab
        mHomeHomeRb = (RadioButton) findViewById(R.id.mHomeHomeRb);
        mHomeFindRb = (RadioButton) findViewById(R.id.mHomeFindRb);
        mHomeSearchRb = (RadioButton) findViewById(R.id.mHomeSearchRb);
        mHomeProfileRb = (RadioButton) findViewById(R.id.mHomeProfileRb);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        mGrabTaskFragment = new GrabTaskFragment();
        transaction.replace(R.id.mHomeContent, mGrabTaskFragment);
        transaction.commit();
        //监听事件：为底部的RadioGroup绑定状态改变的监听事件
        mHomeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switch (checkedId) {
                    case R.id.mHomeHomeRb:
                        mGrabTaskFragment = new GrabTaskFragment();
                        transaction.replace(R.id.mHomeContent, mGrabTaskFragment);
                        transaction.commit();
                        break;
                    case R.id.mHomeFindRb:
                        mNearByFragment = new NearByFragment();
                        transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.mHomeContent, mNearByFragment);
                        transaction.commit();
                        break;
                    case R.id.mHomeSearchRb:
                        mTaskListFragment = new TaskListFragment();
                        transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.mHomeContent, mTaskListFragment);
                        transaction.commit();
                        break;
                    case R.id.mHomeProfileRb:
                        mPersonalZoneFragment = new PersonalZoneFragment();
                        transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.mHomeContent, mPersonalZoneFragment);
                        transaction.commit();
                        break;
                }
            }
        });
    }
}
