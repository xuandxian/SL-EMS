package com.overtech.ems.mapdialog;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.baidu.mapapi.utils.route.RouteParaOption.EBusStrategyType;
import com.overtech.ems.R;
import com.overtech.ems.activity.MyApplication;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.Utilities;

public class InstallerMapDialog implements OnClickListener {
	private final String[] paks = new String[] { "com.baidu.BaiduMap", // 百度
			"com.autonavi.minimap" };// 高德
	private Context ctx;
	private double latitude;
	private double longitude;
	private double curLatitude;
	private double curLongitude;
	private List<AppInfo> installLists = new ArrayList<AppInfo>();
	private LinearLayout contentView;
	private AlertDialog.Builder builder;

	public InstallerMapDialog(Context ctx) {
		this.ctx = ctx;
		curLatitude = ((MyApplication) ctx.getApplicationContext()).latitude;
		curLongitude = ((MyApplication) ctx.getApplicationContext()).longitude;

	}

	private List<AppInfo> getMapApps(Context ctx) {
		List<AppInfo> tempList = new ArrayList<AppInfo>();
		for (String packName : paks) {
			AppInfo a = getInstallApp(ctx, packName);
			if (a != null) {
				tempList.add(a);
			}
		}
		return tempList;
	}

	private AppInfo getInstallApp(Context ctx, String packageName) {
		PackageManager pm = ctx.getPackageManager();
		List<PackageInfo> tempLists = pm
				.getInstalledPackages(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
		for (PackageInfo info : tempLists) {
			if (packageName.equals(info.packageName)) {
				AppInfo a = new AppInfo();
				a.appName = info.applicationInfo.loadLabel(
						ctx.getPackageManager()).toString();
				a.packageName = info.packageName;
				a.versionCode = info.versionCode;
				a.versionName = info.versionName;
				a.appIcon = info.applicationInfo.loadIcon(pm);
				return a;
			}
		}
		return null;
	}

	private void initView() {
		if (installLists.size() != 0) {
			Logr.e("此时手机上安装的地图应用数量==="+installLists.size());
			contentView = new LinearLayout(ctx);
			contentView.setOrientation(LinearLayout.HORIZONTAL);
			for (int i = 0; i < installLists.size(); i++) {
				AppInfo a = installLists.get(i);
				LinearLayout.LayoutParams params = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.setMargins(Utilities.dp2px(ctx, 16), 0,
						Utilities.dp2px(ctx, 16), 0);
				params.gravity = Gravity.CENTER;
				AppCompatTextView tv = new AppCompatTextView(ctx);
				a.appIcon.setBounds(0, 0, Utilities.dp2px(ctx, 56),
						Utilities.dp2px(ctx, 56));
				tv.setCompoundDrawablePadding(Utilities.dp2px(ctx, 16));
				tv.setCompoundDrawables(null, a.appIcon, null, null);
				tv.setLayoutParams(params);
				tv.setGravity(Gravity.CENTER_HORIZONTAL);
				tv.setTextAppearance(ctx,
						R.style.TextAppearance_AppCompat_Medium);
				tv.setText(a.appName);
				tv.setSingleLine();
				tv.setMaxEms(6);
				tv.setEllipsize(TruncateAt.END);
				tv.setTag(a.packageName);
				tv.setOnClickListener(this);
				contentView.addView(tv);
			}
		} else {
			contentView = null;
		}
	}

	public void showDialog(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		installLists.clear();
		installLists.addAll(getMapApps(ctx));
		initView();
		if (builder == null) {
			builder = new AlertDialog.Builder(ctx,
					R.style.MyCustomAppCompatAlertDialg).setTitle("请选择导航工具");
			if (contentView != null) {
				builder.setView(contentView).setMessage(null)
						.setNegativeButton("", null)
						.setPositiveButton("", null).show();
			} else {
				builder.setView(null)
						.setMessage("您的手机没有安装地图导航工具，我们将打开浏览器进行web导航")
						.setPositiveButton("确认",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										baiduNavicate();
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub

									}
								}).show();
			}
		} else {
			if (contentView != null) {
				builder.setView(contentView).setMessage(null)
						.setNegativeButton("", null)
						.setPositiveButton("", null).show();
			} else {
				builder.setView(null)
						.setMessage("您的手机没有安装地图导航工具，我么将打开浏览器进行web导航")
						.setNegativeButton("取消", null)
						.setPositiveButton("确认",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										baiduMapWeb();
									}
								}).show();
			}
		}
	}

	private void baiduNavicate() {
		try {
			Intent intent;
			intent = Intent.parseUri("intent://map/direction?origin=latlng:"+latitude+","+longitude+"|name:起点&destination=latlng:"+curLatitude+","+curLongitude+"|name=终点&mode=transit&src=thirdapp.navi.overtech.24梯#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end", 0);
			ctx.startActivity(intent);
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//		RouteParaOption para = new RouteParaOption().startName("我的位置")
//				.startPoint(new LatLng(curLatitude, curLongitude))
//				.endPoint(new LatLng(latitude, longitude)).endName("终点")
//				.busStrategyType(EBusStrategyType.bus_recommend_way);
//		try {
//			BaiduMapRoutePlan.setSupportWebRoute(true);
//			boolean suc = BaiduMapRoutePlan.openBaiduMapTransitRoute(para, ctx);
//			Logr.e("百度地图调用结果===" + suc);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	private void baiduMapWeb(){
		RouteParaOption para = new RouteParaOption().startName("我的位置")
				.startPoint(new LatLng(curLatitude, curLongitude))
				.endPoint(new LatLng(latitude, longitude)).endName("终点")
				.busStrategyType(EBusStrategyType.bus_recommend_way);
		try {
			BaiduMapRoutePlan.setSupportWebRoute(true);
			boolean suc = BaiduMapRoutePlan.openBaiduMapTransitRoute(para, ctx);
			Logr.e("百度地图调用结果===" + suc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String tag = v.getTag().toString();
		if (tag.equals(paks[0])) {
			baiduNavicate();
		} else if (tag.equals(paks[1])) {
			Intent intent = new Intent(
					"android.intent.action.VIEW",
					android.net.Uri
							.parse("androidamap://navi?sourceApplication=24梯&poiname=目的地&lat="
									+ latitude
									+ "&lon="
									+ longitude
									+ "&dev=1&style=2"));
			intent.setPackage("com.autonavi.minimap");
			ctx.startActivity(intent);
		}
	}

}
