package com.overtech.ems.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

	public NiftyDialogBuilder dialogBuilder;

	public Gson gson;
	public final static String TAG="ems data from server==";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = getActivity();
		fragmentManager = getFragmentManager();
		dialogBuilder = NiftyDialogBuilder.getInstance(activity);
		httpEngine = HttpEngine.getInstance();
		imageLoader = ImageLoader.getInstance();
		imageLoader.initContext(getActivity().getApplicationContext());
		progressDialog = CustomProgressDialog.createDialog(activity);
		progressDialog.setMessage(activity
				.getString(R.string.loading_public_default));
		progressDialog.setCanceledOnTouchOutside(false);
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
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createDialog(activity);
		}
		progressDialog.setMessage(content);
		progressDialog.show();
	}

	public void stopProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	public int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
}
