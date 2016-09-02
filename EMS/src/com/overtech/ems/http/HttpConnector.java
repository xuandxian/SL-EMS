package com.overtech.ems.http;

import java.util.Map;

import android.content.Context;
import android.content.Intent;

import com.overtech.ems.R;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.http.OkHttpClientManager.ResultCallback;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.utils.GsonUtils;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Request;
/**
 * 回调封装
 * @author Overtech Will
 *
 */
public abstract class HttpConnector<T> extends ResultCallback<Bean> {

	private Requester requester;
	public abstract Context getContext();

	public abstract void bizSuccess(Bean response);
	public abstract void bizFailed();
	public abstract void bizStIs1Deal();
	public abstract void stopDialog();
	public HttpConnector(int cmd,String uid,String certificate,Map<String,Object> body){
		requester=new Requester();
		requester.cmd=cmd;
		requester.certificate=certificate;
		requester.uid=uid;
		requester.body=body;
	}
	public HttpConnector(int cmd,String pwd,Map<String,Object> body){
		requester=new Requester();
		requester.cmd=cmd;
		requester.pwd=pwd;
		requester.body=body;
	}
	@Override
	public void onError(Request request, Exception e) {
		// TODO Auto-generated method stub
		Logr.e(request.toString());
		stopDialog();
		bizFailed();
	}

	@Override
	public void onResponse(Bean response) {
		// TODO Auto-generated method stub
		if (response == null) {
			Utilities.showToast(R.string.response_no_object, getContext());
			return;
		}
		stopDialog();
		int st = response.st;
		String msg = response.msg;
		if (st != 0) {
			if (st == -1 || st == -2) {
//				Utilities.showToast(msg, getContext());
				Utilities.showCToast(getContext(), msg, 5000);
				SharePreferencesUtils.put(getContext(),
						SharedPreferencesKeys.UID, "");
				SharePreferencesUtils.put(getContext(),
						SharedPreferencesKeys.CERTIFICATED, "");
				Intent intent = new Intent(getContext(),
						LoginActivity.class);
				getContext().startActivity(intent);
			} else if (st == 1) {
				Utilities.showToast(msg, getContext());
				bizStIs1Deal();
			} else {
				Utilities.showToast(msg, getContext());
			}
		} else {
			bizSuccess(response);
		}
	}
	public void sendRequest(){
		OkHttpClientManager.postAsyn(SystemConfig.NEWIP, this,
				GsonUtils.getInstance().toJson(requester));
	}
}
