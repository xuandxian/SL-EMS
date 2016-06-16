package com.overtech.ems.http;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.google.gson.Gson;
import com.overtech.ems.http.OkHttpClientManager.Param;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.CacheControl;
import android.text.TextUtils;

public class HttpEngine {

	private static HttpEngine mInstance;

	private OkHttpClient mOkHttpClient;

	private Handler mDelivery;

	private Gson mGson;

	public static final MediaType JSON = MediaType
			.parse("application/json;charset=utf-8");

	private static int timeOut = 40000;

	private HttpEngine() {
		mOkHttpClient = new OkHttpClient();
		mOkHttpClient.setConnectTimeout(timeOut,
				java.util.concurrent.TimeUnit.MILLISECONDS);
		mOkHttpClient.setReadTimeout(timeOut,
				java.util.concurrent.TimeUnit.MILLISECONDS);
		mOkHttpClient.setFollowRedirects(true);
		mDelivery = new Handler(Looper.getMainLooper());
		mGson = new Gson();
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
	 * @param url
	 *            传入完整的url链接
	 * @param jsonData
	 *            请求数据类型，json格式
	 * @return
	 */
	public Request createRequest(String url, String jsonData) {
		if (TextUtils.isEmpty(jsonData)) {
			return null;
		}
		Request request;
		RequestBody body = RequestBody.create(JSON, jsonData);
		Request.Builder builder = new Request.Builder()
				.cacheControl(CacheControl.FORCE_NETWORK).url(url).post(body);
		request = builder.build();
		return request;
	}

	/**
	 * create request
	 * 
	 * @param url
	 *            传入完整的url链接
	 * @return
	 */
	public Request createRequest(String url) {
		Request request = new Request.Builder().url(url).build();
		return request;
	}

	/**
	 * create request
	 * 
	 * @param url
	 *            传入完整的url链接
	 * @param params
	 *            参数
	 * @return
	 */
	public Request createRequest(String url, Param... params) {
		if (params == null) {
			params = new Param[0];
		}
		FormEncodingBuilder builder = new FormEncodingBuilder();
		for (Param param : params) {
			builder.add(param.key, param.value);
		}
		RequestBody requestBody = builder.build();
		return new Request.Builder().url(url).post(requestBody).build();
	}

	public Call createRequestCall(Request request) {
		if (null == mOkHttpClient || null == request) {
			return null;
		}
		return mOkHttpClient.newCall(request);
	}

	/**
	 * 
	 * @param url
	 * @param files
	 * @param fileKeys
	 * @param params
	 * @return
	 * @throws IOException
	 */
	public Request createRequest(String url, File[] files, String[] fileKeys,
			Param... params) throws IOException {
		return buildMultipartFormRequest(url, files, fileKeys, params);
	}

	private Request buildMultipartFormRequest(String url, File[] files,
			String[] fileKeys, Param[] params) {
		params = validateParam(params);

		MultipartBuilder builder = new MultipartBuilder()
				.type(MultipartBuilder.FORM);

		for (Param param : params) {
			builder.addPart(
					Headers.of("Content-Disposition", "form-data; name=\""
							+ param.key + "\""),
					RequestBody.create(null, param.value));
		}
		if (files != null) {
			RequestBody fileBody = null;
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				String fileName = file.getName();
				fileBody = RequestBody.create(
						MediaType.parse(guessMimeType(fileName)), file);
				// 根据文件名设置contentType
				builder.addPart(
						Headers.of("Content-Disposition", "form-data; name=\""
								+ fileKeys[i] + "\"; filename=\"" + fileName
								+ "\""), fileBody);
			}
		}

		RequestBody requestBody = builder.build();
		return new Request.Builder().url(url).post(requestBody).build();
	}

	private String guessMimeType(String path) {
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		String contentTypeFor = fileNameMap.getContentTypeFor(path);
		if (contentTypeFor == null) {
			contentTypeFor = "application/octet-stream";
		}
		return contentTypeFor;
	}

	private Param[] validateParam(Param[] params) {
		if (params == null)
			return new Param[0];
		else
			return params;
	}

	public static class Param {
		public Param() {
		}

		public Param(String key, String value) {
			this.key = key;
			this.value = value;
		}

		String key;
		String value;
	}
}
