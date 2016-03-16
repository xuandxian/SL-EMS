package com.overtech.ems.activity.parttime.tasklist;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.effect.Effect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
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
import com.overtech.ems.widget.dialogeffects.Effectstype;
import com.overtech.ems.widget.zxing.camera.CameraManager;
import com.overtech.ems.widget.zxing.decoding.CaptureActivityHandler;
import com.overtech.ems.widget.zxing.decoding.InactivityTimer;
import com.overtech.ems.widget.zxing.view.ViewfinderView;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * 维保人员通过扫描二维码开启工作，将用户名和电梯编号发送到后台进行校验，如果该电梯是当天的维保任务，并且也有维保搭档，则可以开始工作，
 * 然后为防止维保人员没有到维保地点就扫描开始工作，再加上维保地点经纬度的判断，500米范围之内 需要做的事情：和搭档之间是不是第一次开始该电梯的维保工作
 * 点击的时候该电梯是否已经完成
 * 
 * @author Overtech Will
 * 
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
	private double mLatitude;
	private double mLongitude;
	private LatLng mCurrentLocation;

	private Handler handler2 = new Handler() {
		Gson gson = new Gson();

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.QUERY_TASK_PACKAGE_ELEVATOR_SUCCESS:
				String json = (String) msg.obj;
				Log.e("hahahah", json);
				ScanResultBean bean = gson.fromJson(json, ScanResultBean.class);
				BeginWorkResult currentElevator = null;
				boolean isTrue = bean.isSuccess();//是否满足维保要求
				if (isTrue) {
					int count = 0;// 记录包中完成电梯的数量
					List<BeginWorkResult> result = bean.getModel();
					for (int i = 0; i < result.size(); i++) {
						if (result.get(i).getIsFinish().equals("2")) {
							count++;
						}
					}
					if (count == result.size()) {// 当任务包中所有的电梯都完成了，就可以进入到问题反馈中去
						/*Intent intent = new Intent(ScanCodeActivity.this,//此处暂时不需要进入到问题反馈中
								QuestionResponseActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString(Constant.TASKNO, result.get(0)
								.getTaskNo());
						intent.putExtras(bundle);
						startActivity(intent);
						ScanCodeActivity.this.finish();
						break;*/
					}
					for (int i = 0; i < result.size(); i++) {//遍历电梯编号，得到当前电梯正在维保的电梯
						if (mElevatorNo.equals(result.get(i).getElevatorNo())) {
							currentElevator = result.get(i);
						}
					}

					double latitude = Double.parseDouble(currentElevator
							.getLatitude());
					double longitude = Double.parseDouble(currentElevator
							.getLongitude());
					LatLng latlng = new LatLng(latitude, longitude);
					double distance = DistanceUtil.getDistance(
							mCurrentLocation, latlng);
					if (distance > 500.0) {
						Utilities.showToast("您距离维保电梯的距离超出范围", mContext);
						ScanCodeActivity.this.finish();
						break;
					}
					if(currentElevator.getIsFinish().equals("2")){
						Utilities.showToast("你已经为完成了该电梯", mContext);
						ScanCodeActivity.this.finish();
						break;
					}
					// 业务调整，该部分注释
					// String isStart=result.getIsStart();
					// if(isStart.equals("0")){
					// Utilities.showToast("请将电梯监测设备按钮调至维保状态后开始进行维保工作",
					// mContext);
					// }else{
					// Utilities.showToast("请将电梯监测设备按钮调至维保状态后开始进行维保工作",
					// mContext);
					// }
					final String taskNo = currentElevator.getTaskNo();
					final String workType = currentElevator.getWorkType();
					final String zonePhone = currentElevator.getZonePhone();
					showNiffyDialog2(taskNo,workType,zonePhone);

				} else {
					Utilities.showToast("您尚未满足维保要求", context);// 维保要求包括，维保时间正确，有维保搭档，维保电梯正确
					ScanCodeActivity.this.finish();
				}
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
		mLatitude = application.latitude;
		mLongitude = application.longitude;
		mCurrentLocation = new LatLng(mLatitude, mLongitude);
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadContent.setText("二维码扫描");
		mDoBack = (ImageView) findViewById(R.id.iv_headBack);
		mDoBack.setVisibility(View.VISIBLE);
		mDoBack.setOnClickListener(new View.OnClickListener() {
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

	private void showNiffyDialog(final String taskNo,final String workType,final String zonePhone){
		new AlertDialog.Builder(context)
				.setTitle("温馨提示")
				.setMessage("请将电梯监测设备按钮调至维保状态后开始进行维保工作")
				.setPositiveButton("确认",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(
									DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(
										ScanCodeActivity.this,
										QueryTaskListActivity.class);
								Bundle bundle = new Bundle();
								bundle.putString(Constant.TASKNO,
										taskNo);
								bundle.putString(Constant.WORKTYPE,
										workType);
								bundle.putString(
										Constant.ZONEPHONE,
										zonePhone);
								bundle.putString(
										Constant.ELEVATORNO,
										mElevatorNo);
								intent.putExtras(bundle);
								startActivity(intent);
								dialog.dismiss();
								ScanCodeActivity.this.finish();
							}
						})
				.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(
									DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
								ScanCodeActivity.this.finish();
							}
						}).show();
	}
	private void showNiffyDialog2(final String taskNo,final String workType,final String zonePhone){
		Effectstype effect = Effectstype.Slideright;
		dialogBuilder.withTitle("温馨提示").withTitleColor(R.color.main_primary)
				.withDividerColor("#11000000").withMessage("请将电梯监测设备按钮调至维保状态后开始进行维保工作")
				.withMessageColor(R.color.main_primary)
				.withDialogColor("#FFFFFFFF").isCancelableOnTouchOutside(true)
				.withDuration(700).withEffect(effect)
				.withButtonDrawable(R.color.main_white).withButton1Text("取消")
				.withButton1Color("#DD47BEE9").withButton2Text("确认")
				.withButton2Color("#DD47BEE9")
				.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
					}
				}).setButton2Click(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(
						ScanCodeActivity.this,
						QueryTaskListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(Constant.TASKNO,
						taskNo);
				bundle.putString(Constant.WORKTYPE,
						workType);
				bundle.putString(
						Constant.ZONEPHONE,
						zonePhone);
				bundle.putString(
						Constant.ELEVATORNO,
						mElevatorNo);
				intent.putExtras(bundle);
				startActivity(intent);
				dialogBuilder.dismiss();
				ScanCodeActivity.this.finish();
			}
		}).show();
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