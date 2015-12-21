package com.overtech.ems.activity.parttime.grabtask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.adapter.GridViewAdapter;
import com.overtech.ems.activity.parttime.fragment.GrabTaskFragment;
import com.overtech.ems.http.OkHttpClientManager.Param;
import com.overtech.ems.utils.Utilities;

import java.util.ArrayList;

/**
 * Created by Tony1213 on 15/12/05.
 * Change on 15/12/17
 *
 * @author Tony1213
 *         抢单条件筛选
 */
public class GrabTaskDoFilterActivity extends BaseActivity implements OnClickListener {
    private ImageView mHeadBack;
    private TextView mHeadContent;
    private TextView mHeadContentRight;
    private GridView gridView;
    private GridViewAdapter adapter;
    private GridViewAdapter adapter2;
    private Button mZone;
    private Button mTime;
    int[] image = {R.drawable.filter_zone_baoshan, R.drawable.filter_zone_changning, R.drawable.filter_zone_chongming, R.drawable.filter_zone_fengxian, R.drawable.filter_zone_hongkou, R.drawable.filter_zone_huangpu, R.drawable.filter_zone_jiading, R.drawable.filter_zone_jingan, R.drawable.filter_zone_jinshan, R.drawable.filter_zone_minghang, R.drawable.filter_zone_putuo, R.drawable.filter_zone_qingpu, R.drawable.filter_zone_qingpu, R.drawable.filter_zone_xuhui, R.drawable.filter_zone_yangpu, R.drawable.filter_zone_zhabei};
    int[] image2 = {R.drawable.filter_time_fifteen_in, R.drawable.filter_time_fifteen_out};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_grab_task_filter);
        findViewById();
        init();
        flushContent();
    }

    private void flushContent() {
        mZone.setOnClickListener(this);
        mTime.setOnClickListener(this);
        mHeadBack.setOnClickListener(this);
        mHeadContentRight.setOnClickListener(this);
    }

    private void findViewById() {
        mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
        mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
        mHeadContentRight = (TextView) findViewById(R.id.tv_headTitleRight);
        mZone = (Button) findViewById(R.id.button1);
        mTime = (Button) findViewById(R.id.button2);
        gridView = (GridView) findViewById(R.id.gridView1);
    }

    private void init() {
        mHeadContent.setText("筛 选");
        mHeadContentRight.setText("确定");
        mHeadBack.setVisibility(View.VISIBLE);
        adapter = new GridViewAdapter(image, context);
        adapter2 = new GridViewAdapter(image2, context);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.choiceStatus(position);
            }
        });
        mZone.setBackgroundResource(R.drawable.selector);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                gridView.setAdapter(adapter);
                gridView.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        adapter.choiceStatus(position);
                    }
                });
                mZone.setBackgroundResource(R.drawable.selector);
                mTime.setBackgroundDrawable(null);
                break;
            case R.id.button2:
                if (adapter2 != null) {
                    adapter2.notifyDataSetChanged();
                }
                gridView.setAdapter(adapter2);
                gridView.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        adapter2.choiceStatus(position);
                    }
                });
                mTime.setBackgroundResource(R.drawable.selector);
                mZone.setBackgroundDrawable(null);
                break;
            case R.id.tv_headTitleRight:
                String mZone = "";
                String mTime = "";
                ArrayList list = adapter.getTypePositon(image);
                ArrayList list2 = adapter2.getTypePositon(image2);
                for (int i = 0; i < list.size(); i++) {
                    int temp = Integer.valueOf(list.get(i).toString());
                    switch (temp) {
                        case 0:
                            mZone += "宝山区|";
                        break;
                        case 1:
                            mZone += "长宁区|";
                            break;
                        case 2:
                            mZone += "崇明县|";
                            break;
                        case 3:
                            mZone += "奉贤区|";
                            break;
                        case 4:
                            mZone += "虹口区|";
                            break;
                        case 5:
                            mZone += "黄浦区|";
                            break;
                        case 6:
                            mZone += "嘉定区|";
                            break;
                        case 7:
                            mZone += "静安区|";
                            break;
                        case 8:
                            mZone += "金山区|";
                            break;
                        case 9:
                            mZone += "闵行区|";
                            break;
                        case 10:
                            mZone += "普陀区|";
                            break;
                        case 11:
                            mZone += "青浦区|";
                            break;
                        case 12:
                            mZone += "青浦区|";
                            break;
                        case 13:
                            mZone += "徐汇区|";
                            break;
                        case 14:
                            mZone += "杨浦区|";
                            break;
                        case 15:
                            mZone += "闸北区|";
                            break;
                    }
                }
                Utilities.showToast("区域："+mZone,context);
                for (int i = 0; i < list2.size(); i++) {
                    int temp2=Integer.valueOf(list2.get(i).toString());
                    switch (temp2) {
                        case 0:
                            mTime += "0";
                            break;
                        case 1:
                            mTime += "1";
                            break;
                    }
                }
                Utilities.showToast("时间："+mTime,context);
                Intent intent=new Intent();
                intent.putExtra("mZone", mZone);
                intent.putExtra("mTime", mTime);
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
            case R.id.iv_headBack:
                finish();
                break;
        }
    }
}
