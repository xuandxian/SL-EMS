package com.overtech.ems.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.CacheControl;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import android.text.TextUtils;

public class HttpEngine {

	private static HttpEngine mInstance;

	private OkHttpClient mOkHttpClient;

	private Context mContext;

	private Handler mDelivery;

	private Gson mGson;

	public static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");

	private static int timeOut=40000;

	private HttpEngine() {
		mOkHttpClient = new OkHttpClient();
		mOkHttpClient.setConnectTimeout(timeOut, java.util.concurrent.TimeUnit.MILLISECONDS);
		mOkHttpClient.setReadTimeout(timeOut, java.util.concurrent.TimeUnit.MILLISECONDS);
		mOkHttpClient.setFollowRedirects(true);
		mDelivery = new Handler(Looper.getMainLooper());
		mGson = new Gson();
	}

	public void initContext(Context context){
		if (null==mContext){
			mContext=context;
		}

	}

	public static HttpEngine getInstance() {
		if (mInstance == null) {
			synchronized (HttpEngine.class) {
				if (mInstance == null) {
					mInstance = new HttpEngine();
				}
			}
		}
		return mInstance;
	}

	/**
	 * create request
	 *
	 * @param url      传入完整的url链接
	 * @param jsonData 请求数据类型，json格式
	 * @return
	 */
	public Request createRequest(String url, String jsonData) {
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		Request request;
		RequestBody body = RequestBody.create(JSON, jsonData);
		Request.Builder builder = new Request.Builder().cacheControl(CacheControl.FORCE_NETWORK).url(url).post(body);
		request = builder.build();
		return request;
	}
}
