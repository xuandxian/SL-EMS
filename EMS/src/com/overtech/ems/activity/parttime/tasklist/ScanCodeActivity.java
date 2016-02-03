package com.overtech.ems.activity.parttime.tasklist;

import java.io.IOException;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.bean.BeginWorkResult;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.entity.parttime.ScanResultBean;
import com.overtech.ems.http.HttpEngine.Param;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.zxing.camera.CameraManager;
import com.overtech.ems.widget.zxing.decoding.CaptureActivityHandler;
import com.overtech.ems.widget.zxing.decoding.InactivityTimer;
import com.overtech.ems.widget.zxing.view.ViewfinderView;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * Initial the camera
 * 
 * @author Ryan.Tang
 */
public class ScanCodeActivity extends BaseActivity implements Callback {

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private ImageView mDoBack;
	private TextView mHeadContent;
	private Context mContext;
	private String mElevatorNo;

	private Handler handler2 = new Handler() {
		Gson gson = new Gson();

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.QUERY_TASK_PACKAGE_ELEVATOR_SUCCESS:
				String json = (String) msg.obj;
				Log.e("==扫描结果==", json);
				ScanResultBean bean = gson.fromJson(json, ScanResultBean.class);
				boolean isTrue = bean.isSuccess();
				if (isTrue) {
					BeginWorkResult result = bean.getModel();
					Intent intent = new Intent(ScanCodeActivity.this,
							QueryTaskListActivity.class);
					String isStart=result.getIsStart();
					if(isStart.equals("0")){
						Utilities.showToast("维保计时开始", mContext);
					}else{
						Utilities.showToast("维保已经开始", mContext);
					}
					Bundle bundle = new Bundle();
					bundle.putString(Constant.TASKNO, result.getTaskNo());
					bundle.putString(Constant.WORKTYPE, result.getWorkType());
					bundle.putString(Constant.ZONEPHONE, result.getZonePhone());
					bundle.putString(Constant.ELEVATORNO, mElevatorNo);
					intent.putExtras(bundle);
					startActivity(intent);
				} else {
					BeginWorkResult result=bean.getModel();
					if(result.getIsFinish()!=null&&result.getIsFinish().equals("2")){
						Utilities.showToast("您已经完成了该电梯", context);
					}else{
						Utilities.showToast("您尚未满足维保要求", context);//维保要求包括，维保时间正确，有维保搭档，维保电梯正确
					}
				}
				ScanCodeActivity.this.finish();
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast("服务端异常", context);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast("网络异常", context);
				break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_task_list_capture);
		mContext = ScanCodeActivity.this;
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadContent.setText("二维码扫描");
		mDoBack = (ImageView) findViewById(R.id.iv_headBack);
		mDoBack.setVisibility(View.VISIBLE);
		mDoBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ScanCodeActivity.this.finish();
			}
		});
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * 
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		mElevatorNo = result.getText();
		if (mElevatorNo.equals("")) {
			Utilities.showToast("扫描失败", mContext);
		} else {
			Param param = new Param(Constant.ELEVATORNO, mElevatorNo);
			Param param2 = new Param(Constant.LOGINNAME,
					mSharedPreferences.getString(
							SharedPreferencesKeys.CURRENT_LOGIN_NAME, ""));
			Request request = httpEngine.createRequest(
					ServicesConfig.QUERY_TASK_PACKAGE_ELEVATOR, param, param2);
			Call call = httpEngine.createRequestCall(request);
			call.enqueue(new com.squareup.okhttp.Callback() {

				@Override
				public void onResponse(Response response) throws IOException {
					Message msg = new Message();
					if (response.isSuccessful()) {
						msg.obj = response.body().string();
						msg.what = StatusCode.QUERY_TASK_PACKAGE_ELEVATOR_SUCCESS;
					} else {
						msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
					}
					handler2.sendMessage(msg);
				}

				@Override
				public void onFailure(Request request, IOException exception) {
					Message msg = new Message();
					msg.what = StatusCode.RESPONSE_NET_FAILED;
					handler2.sendMessage(msg);
				}
			});
		}

	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};
}