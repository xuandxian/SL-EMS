package com.overtech.ems.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.util.TypedValue;

import com.google.gson.Gson;
import com.overtech.ems.R;
import com.overtech.ems.http.HttpEngine;
import com.overtech.ems.widget.CustomProgressDialog;
import com.overtech.ems.widget.bitmap.ImageLoader;
import com.overtech.ems.widget.dialogeffects.NiftyDialogBuilder;

/**
 * Created by Tony1213 on 15/12/21.
 */
public class BaseFragment extends Fragment {

	public ImageLoader imageLoader;

	public Activity activity;

	public FragmentManager fragmentManager;

	public HttpEngine httpEngine;

	public CustomProgressDialog progressDialog;

	public ProgressDialog dialog;
	
	public NiftyDialogBuilder dialogBuilder;
	
	public AlertDialog.Builder alertBuilder;
	public Gson gson;
	public final static String TAG="ems data from server==";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = getActivity();
		fragmentManager = getFragmentManager();
		dialogBuilder = NiftyDialogBuilder.getInstance(activity);
		if(alertBuilder==null){
			alertBuilder=new Builder(activity);
			alertBuilder.setCancelable(false);
		}
		httpEngine = HttpEngine.getInstance();
		imageLoader = ImageLoader.getInstance();
		imageLoader.initContext(getActivity().getApplicationContext());
		if(dialog==null&&Build.VERSION.SDK_INT>=VERSION_CODES.LOLLIPOP){
			dialog=new ProgressDialog(activity);
			dialog.setMessage(getResources().getString(R.string.loading_public_default));
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setIndeterminate(true);
		}else{
			progressDialog = CustomProgressDialog.createDialog(activity);
			progressDialog.setMessage(activity
					.getString(R.string.loading_public_default));
			progressDialog.setCanceledOnTouchOutside(false);
		}
		if (gson == null) {
			gson = new Gson();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	public void startProgressDialog(String content) {
		if(Build.VERSION.SDK_INT>=VERSION_CODES.LOLLIPOP){
			dialog.setMessage(content);
			dialog.show();
		}else{
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(activity);
			}
			progressDialog.setMessage(content);
			progressDialog.show();
		}
	}

	public void stopProgressDialog() {
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
			if(dialog!=null){
				dialog.dismiss();
			}
		}else{
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	}

	public int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
}
