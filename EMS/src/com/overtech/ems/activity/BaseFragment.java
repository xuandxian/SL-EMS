package com.overtech.ems.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.overtech.ems.R;
import com.overtech.ems.http.HttpEngine;
import com.overtech.ems.http.OkHttpClientManager;
import com.overtech.ems.widget.CustomProgressDialog;
import com.overtech.ems.widget.bitmap.ImageLoader;
import com.overtech.ems.widget.dialogeffects.NiftyDialogBuilder;

/**
 * Created by Tony1213 on 15/12/21.
 */
public class BaseFragment extends Fragment {

    public ImageLoader imageLoader;

    public Activity activity;

    public Context context;

    public FragmentManager fragmentManager;

    public OkHttpClientManager okHttpClientManager;

    public HttpEngine httpEngine;

    public CustomProgressDialog progressDialog;

    public NiftyDialogBuilder dialogBuilder;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
        context =getActivity();
        fragmentManager = getFragmentManager();
        dialogBuilder = NiftyDialogBuilder.getInstance(activity);
        httpEngine = HttpEngine.getInstance();
        httpEngine.initContext(context);
        imageLoader = ImageLoader.getInstance();
        imageLoader.initContext(context);
        progressDialog = CustomProgressDialog.createDialog(context);
        progressDialog.setMessage(context.getString(R.string.loading_public_default));
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
            progressDialog = CustomProgressDialog.createDialog(context);
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
}
