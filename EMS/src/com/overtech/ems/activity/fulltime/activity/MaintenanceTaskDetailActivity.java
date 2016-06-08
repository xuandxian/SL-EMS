package com.overtech.ems.activity.fulltime.activity;

import android.os.Bundle;
import android.widget.ListView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;

public class MaintenanceTaskDetailActivity extends BaseActivity {
	private ListView listView;
	private String[][] faultComponent = { { "0", "其他" }, { "1", "不动车" },
			{ "2", "开门不良" }, { "3", "关门不良" }, { "4", "开关门不良" },
			{ "5", "急停开关" }, { "6", "平层不良" }, { "7", "冲顶" }, { "8", "沉底" },
			{ "9", "应答不良" }, { "10", "运行不良" }, { "11", "舒适性不佳" },
			{ "12", "振动、噪音" }, { "13", "照明、通风" }, { "20", "机房" },
			{ "21", "曳引机、电动机" }, { "22", "控制屏" }, { "23", "抱闸" },
			{ "24", "限速器" }, { "30", "轿厢" }, { "31", "轿门" }, { "32", "操纵箱" },
			{ "33", "轿内显示" }, { "34", "照明、通风" }, { "35", "轿底" },
			{ "36", "轿顶" }, { "37", "安全触板" }, { "40", "层站" }, { "41", "厅门门锁" },
			{ "42", "厅外召唤" }, { "43", "厅外显示" }, { "44", "厅门" }, { "50", "井道" },
			{ "60", "底坑" }, { "88", "故障部位不明" }, { "99", "到现场正常" } };
	private String[][] faultCause = { {} };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maintenance_task_detail);
		listView = (ListView) findViewById(R.id.lv_maintenance_task_detail);
	}
}
