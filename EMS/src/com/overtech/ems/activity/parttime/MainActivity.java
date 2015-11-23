package com.overtech.ems.activity.parttime;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.baidu.mapapi.SDKInitializer;
import com.overtech.ems.R;
import com.overtech.ems.activity.parttime.fragment.GrabTaskFragment;
import com.overtech.ems.activity.parttime.fragment.NearByFragment;
import com.overtech.ems.activity.parttime.fragment.PersonalZoneFragment;
import com.overtech.ems.activity.parttime.fragment.TaskListFragment;
import com.overtech.ems.widget.dialogeffects.Effectstype;
import com.overtech.ems.widget.dialogeffects.NiftyDialogBuilder;

/**
 * @author Tony
 * @description 主界面
 * @date 2015-10-05
 */
public class MainActivity extends Activity implements OnClickListener {

    private Context mContext;
    private Effectstype effect;
    private NiftyDialogBuilder dialogBuilder;
    //	private TextView mHeadContent;
//	private TextView mHeadRightContent;
    private Fragment mGrabTaskFragment;
    private Fragment mTaskListFragment;
    private Fragment mPersonalZoneFragment;
    private Fragment mNearByFragment;
    private RelativeLayout mRelGrabTask;
    private RelativeLayout mRelNearBy;
    private RelativeLayout mRelTaskList;
    private RelativeLayout mRelPersonalZone;
    private TextView mTabGrabTaskContent;
    private TextView mTabNearByContent;
    private TextView mTabTaskListContent;
    private TextView mTabPersonalZoneContent;
    private ImageView mTabGrabTaskIv;
    private ImageView mTabNearByIv;
    private ImageView mTabTaskListIv;
    private ImageView mTabPersonalZoneIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        findViewById();
        init();
    }

    private void findViewById() {
        mRelGrabTask = (RelativeLayout) findViewById(R.id.rl_parttime_grab_task);
        mRelNearBy = (RelativeLayout) findViewById(R.id.rl_parttime_nearby);
        mRelTaskList = (RelativeLayout) findViewById(R.id.rl_parttime_task_list);
        mRelPersonalZone = (RelativeLayout) findViewById(R.id.rl_parttime_personal_zone);
        mTabGrabTaskContent = (TextView) findViewById(R.id.tv_grab_taskn);
        mTabNearByContent = (TextView) findViewById(R.id.tv_nearby);
        mTabTaskListContent = (TextView) findViewById(R.id.tv_task_list);
        mTabPersonalZoneContent = (TextView) findViewById(R.id.tv_personal_zone);
        mTabGrabTaskIv = (ImageView) findViewById(R.id.iv_grab_task);
        mTabNearByIv = (ImageView) findViewById(R.id.iv_nearby);
        mTabTaskListIv = (ImageView) findViewById(R.id.iv_task_list);
        mTabPersonalZoneIv = (ImageView) findViewById(R.id.iv_personal_zone);
    }

    private void init() {
        mContext = MainActivity.this;
//		mHeadContent.setText("抢单");
        dialogBuilder = NiftyDialogBuilder.getInstance(this);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        mGrabTaskFragment = new GrabTaskFragment();
        transaction.replace(R.id.fragment_content, mGrabTaskFragment);
        transaction.commit();
        mRelGrabTask.setOnClickListener(this);
        mRelNearBy.setOnClickListener(this);
        mRelTaskList.setOnClickListener(this);
        mRelPersonalZone.setOnClickListener(this);
//		mHeadRightContent.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        switch (v.getId()) {
            case R.id.rl_parttime_grab_task:
//			mHeadContent.setText("抢单");
//			mHeadRightContent.setText("");
                mTabGrabTaskIv
                        .setBackgroundResource(R.drawable.shopping_home_tab_take_out_selected);
                mTabGrabTaskContent.setTextColor(Color.argb(255, 49, 144, 232));
                mTabNearByIv
                        .setBackgroundResource(R.drawable.shopping_home_tab_found);
                mTabNearByContent.setTextColor(Color.argb(255, 153, 153, 153));
                mTabTaskListIv
                        .setBackgroundResource(R.drawable.shopping_home_tab_order);
                mTabTaskListContent.setTextColor(Color.argb(255, 153, 153, 153));
                mTabPersonalZoneIv
                        .setBackgroundResource(R.drawable.shopping_home_tab_personal);
                mTabPersonalZoneContent
                        .setTextColor(Color.argb(255, 153, 153, 153));

                mGrabTaskFragment = new GrabTaskFragment();
                transaction.replace(R.id.fragment_content, mGrabTaskFragment);
                transaction.commit();
                break;
            case R.id.rl_parttime_nearby:
//			mHeadContent.setText("附近");
//			mHeadRightContent.setText("");
                mTabNearByIv
                        .setBackgroundResource(R.drawable.shopping_home_tab_found_selected);
                mTabNearByContent.setTextColor(Color.argb(255, 49, 144, 232));
                mTabGrabTaskIv
                        .setBackgroundResource(R.drawable.shopping_home_tab_take_out);
                mTabGrabTaskContent.setTextColor(Color.argb(255, 153, 153, 153));
                mTabTaskListIv
                        .setBackgroundResource(R.drawable.shopping_home_tab_order);
                mTabTaskListContent.setTextColor(Color.argb(255, 153, 153, 153));
                mTabPersonalZoneIv
                        .setBackgroundResource(R.drawable.shopping_home_tab_personal);
                mTabPersonalZoneContent
                        .setTextColor(Color.argb(255, 153, 153, 153));
                mNearByFragment = new NearByFragment();
                transaction.replace(R.id.fragment_content, mNearByFragment);
                transaction.commit();
                break;
            case R.id.rl_parttime_task_list:
//			mHeadContent.setText("");
//			mHeadRightContent.setText("开始");
                mTabTaskListIv
                        .setBackgroundResource(R.drawable.shopping_home_tab_order_selected);
                mTabTaskListContent.setTextColor(Color.argb(255, 49, 144, 232));
                mTabNearByIv
                        .setBackgroundResource(R.drawable.shopping_home_tab_found);
                mTabNearByContent.setTextColor(Color.argb(255, 153, 153, 153));
                mTabGrabTaskIv
                        .setBackgroundResource(R.drawable.shopping_home_tab_take_out);
                mTabGrabTaskContent.setTextColor(Color.argb(255, 153, 153, 153));
                mTabPersonalZoneIv
                        .setBackgroundResource(R.drawable.shopping_home_tab_personal);
                mTabPersonalZoneContent
                        .setTextColor(Color.argb(255, 153, 153, 153));
                mTaskListFragment = new TaskListFragment();
                transaction.replace(R.id.fragment_content, mTaskListFragment);
                transaction.commit();
                break;
            case R.id.rl_parttime_personal_zone:
//			mHeadContent.setText("我的");
//			mHeadRightContent.setText("");
                mTabPersonalZoneIv
                        .setBackgroundResource(R.drawable.shopping_home_tab_personal_selected);
                mTabPersonalZoneContent.setTextColor(Color.argb(255, 49, 144, 232));
                mTabTaskListIv
                        .setBackgroundResource(R.drawable.shopping_home_tab_order);
                mTabTaskListContent.setTextColor(Color.argb(255, 153, 153, 153));
                mTabNearByIv
                        .setBackgroundResource(R.drawable.shopping_home_tab_found);
                mTabNearByContent.setTextColor(Color.argb(255, 153, 153, 153));
                mTabGrabTaskIv
                        .setBackgroundResource(R.drawable.shopping_home_tab_take_out);
                mTabGrabTaskContent.setTextColor(Color.argb(255, 153, 153, 153));
                mPersonalZoneFragment = new PersonalZoneFragment();
//			mPersonalZoneFragment = new PersonalZoneFragment2();
                transaction.replace(R.id.fragment_content, mPersonalZoneFragment);
                transaction.commit();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            effect = Effectstype.Shake;
            dialogBuilder
                    .withTitle("温馨提示")
                    .withTitleColor("#FFFFFF")
                    .withDividerColor("#11000000")
                    .withMessage("您是否要退出?")
                    .withMessageColor("#FFFFFFFF")
                    .withDialogColor("#FF009BEE")
                    .withIcon(
                            getResources().getDrawable(R.drawable.icon_dialog))
                    .isCancelableOnTouchOutside(true).withDuration(700)
                    .withEffect(effect).withButtonDrawable(R.color.bg_title)
                    .withButton1Text("确定").withButton1Color("#FFFFFFFF")
                    .withButton2Text("取消").withButton2Color("#FFFFFFFF")
                    .setButton1Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                            System.exit(0);
                        }
                    }).setButton2Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogBuilder.dismiss();
                }
            }).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
