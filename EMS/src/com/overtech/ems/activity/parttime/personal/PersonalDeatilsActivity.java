package com.overtech.ems.activity.parttime.personal;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.activity.parttime.personal.phoneno.ChangePhoneNoValidatePasswordActivity;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.http.HttpConnector;
import com.overtech.ems.picasso.Transformation;
import com.overtech.ems.utils.ImageUtils;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.bitmap.ImageLoader;

public class PersonalDeatilsActivity extends BaseActivity implements
		OnClickListener {
	private final int STUB_ID = R.drawable.icon_personal_my;// 此处为了将ImageLoader里面的方法抽出来单独使用，而将里面的字段提出来
	private final Config DEFAULT_CONFIG = Config.RGB_565;// 同上
	private TextView mHeadContent;
	private ImageView mDoBack;
	private RelativeLayout mChangePhoneNo;
	private RelativeLayout mChangePassword;
	private RelativeLayout mWorkLicense;
	private Button mDoExit;
	private TextView mPhone;
	private ImageView avator;
	private TextView mId;
	private TextView mName;
	private TextView mCertificateNo;
	private TextView mRegisterDate;
	private RatingBar mRatingBar;
	private AlertDialog.Builder builder;// 上岗证dialog
	private View workLicenseView;// 上岗证view
	private AppCompatEditText etWorkLicenseNo;// 上岗证编号
	private DatePicker dpWorkLicenseDue;// 上岗证到期时间
	private GridLayout glWorkLicenseImg;// 上岗证图片
	private Button btAddNewImg;
	private PopupMenu popupMenu;// 拍照或者选择图片
	private File outFile;// 拍照时指定路径
	private Uri cameraUri;// 拍照时指定的图片路径
	private String uid;
	private String certificate;
	private String mWorkNo;
	private String allowUpdateWorkLicense;// 允许更新上岗证信息
	private PersonalDeatilsActivity activity;
	private final String CHANGEPHONE = "0";
	private final String RESETPASSWORD = "1";
	private final int SELECT_PHOTO = 0x00012;
	private final int SELECT_CAMERA = 0x00013;
	private String[] paths = new String[2];

	@Override
	protected int getLayoutResIds() {
		// TODO Auto-generated method stub
		return R.layout.activity_personal_details;
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		stackInstance.pushActivity(this);
		initViews();
		initEvents();
		startLoading();
	}

	private void startLoading() {
		startProgressDialog(getResources().getString(
				R.string.loading_public_default));
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20071, uid,
				certificate, null) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				String avatorUrl = response.body.get("avator").toString();
				float rate = Float.parseFloat(response.body.get("employeeRate")
						.toString());
				String id = response.body.get("id").toString();
				String name = response.body.get("name").toString();
				String phone = response.body.get("phone").toString();
				String registerTime = response.body.get("registerTime")
						.toString();
				allowUpdateWorkLicense = response.body.get(
						"allowUpdateWorkLicense").toString();
				mWorkNo = response.body.get("workNo").toString();
				if (avatorUrl == null || "".equals(avatorUrl)) {
					avator.setScaleType(ScaleType.CENTER_CROP);
					avator.setImageResource(STUB_ID);
				} else {
					ImageLoader.getInstance().displayImage(avatorUrl, avator,
							STUB_ID, STUB_ID, DEFAULT_CONFIG,
							new Transformation() {

								@Override
								public Bitmap transform(Bitmap source) {
									// TODO Auto-generated method stub
									return ImageUtils.toRoundBitmap(source);
								}

								@Override
								public String key() {
									// TODO Auto-generated method stub
									return "avator";
								}
							});
				}
				mPhone.setText(phone);
				mId.setText(id);
				mName.setText(name);
				mCertificateNo.setText(mWorkNo);
				mRegisterDate.setText(registerTime);
				mRatingBar.setRating(rate);
			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void bizStIs1Deal() {
				// TODO Auto-generated method stub

			}

			@Override
			public void stopDialog() {
				// TODO Auto-generated method stub
				stopProgressDialog();
			}
		};
		conn.sendRequest();
	}

	private void initViews() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mDoBack = (ImageView) findViewById(R.id.iv_headBack);
		mChangePhoneNo = (RelativeLayout) findViewById(R.id.rl_change_phoneNo);
		mDoExit = (Button) findViewById(R.id.btn_exit);
		mPhone = (TextView) findViewById(R.id.tv_personal_phone);
		mChangePassword = (RelativeLayout) findViewById(R.id.rl_change_password);
		mWorkLicense = (RelativeLayout) findViewById(R.id.rl_worklicense);
		avator = (ImageView) findViewById(R.id.iv_avator);
		mId = (TextView) findViewById(R.id.tv_id);
		mName = (TextView) findViewById(R.id.tv_username);
		mCertificateNo = (TextView) findViewById(R.id.tv_certificate_no);
		mRegisterDate = (TextView) findViewById(R.id.tv_register_time);
		mRatingBar = (RatingBar) findViewById(R.id.ratingBar1);
		mHeadContent.setText("账号信息");
		activity = PersonalDeatilsActivity.this;
		uid = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.UID, "");
		certificate = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.CERTIFICATED, "");
	}

	private void initEvents() {
		// 返回键
		mDoBack.setVisibility(View.VISIBLE);
		mDoBack.setOnClickListener(this);
		mChangePhoneNo.setOnClickListener(this);
		mChangePassword.setOnClickListener(this);
		mWorkLicense.setOnClickListener(this);
		mDoExit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_add_img:
			if (glWorkLicenseImg.getChildCount() >= 2) {
				Utilities.showToast("最多可选择两张照片", activity);
			} else {
				showPopupMenu();
			}
			break;
		case R.id.iv_headBack:
			stackInstance.popActivity(activity);
			break;
		case R.id.rl_change_phoneNo:
			Intent intent = new Intent(PersonalDeatilsActivity.this,
					ChangePhoneNoValidatePasswordActivity.class);
			String phone = mPhone.getText().toString();
			intent.putExtra("flag", CHANGEPHONE);
			intent.putExtra("phone", phone);
			startActivity(intent);
			break;
		case R.id.rl_change_password:
			Intent intent2 = new Intent(PersonalDeatilsActivity.this,
					ChangePhoneNoValidatePasswordActivity.class);
			String phone2 = mPhone.getText().toString();
			intent2.putExtra("flag", RESETPASSWORD);
			intent2.putExtra("phone", phone2);
			startActivity(intent2);
			break;
		case R.id.rl_worklicense:
			if (TextUtils.equals("0", allowUpdateWorkLicense)) {
				Utilities.showToast("您的上岗证尚未过期", activity);
			} else if (TextUtils.equals("1", allowUpdateWorkLicense)) {
				showWorklicenseDialog();
			}
			break;
		case R.id.btn_exit:
			exitDialog();
			break;
		}
	}

	private void showPopupMenu() {
		if (popupMenu == null) {
			popupMenu = new PopupMenu(activity, btAddNewImg, Gravity.END);
			popupMenu.getMenuInflater().inflate(
					R.menu.menu_popup_select_photo_camara, popupMenu.getMenu());
			popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					// TODO Auto-generated method stub
					switch (item.getItemId()) {
					case R.id.menu_select_photo:
						openPhoto();
						break;
					case R.id.menu_select_camera:
						openCamera();
						break;
					default:
						break;
					}
					return true;
				}
			});
		}
		popupMenu.show();
	}

	private void openCamera() {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		File dir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		outFile = new File(dir, "workLicense" + (int) (Math.random() * 100)
				+ ".jpg");
		cameraUri = Uri.fromFile(outFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri); // 这样就将文件的存储方式和uri指定到了Camera应用中
		startActivityForResult(intent, SELECT_CAMERA);
	}

	private void openPhoto() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/jpeg");
		startActivityForResult(intent, SELECT_PHOTO);
	}

	private void showWorklicenseDialog() {
		// TODO Auto-generated method stub
		if (workLicenseView == null) {
			workLicenseView = LayoutInflater.from(activity).inflate(
					R.layout.layout_worklicense, null);
			etWorkLicenseNo = (AppCompatEditText) workLicenseView
					.findViewById(R.id.et_worklicense_no);
			dpWorkLicenseDue = (DatePicker) workLicenseView
					.findViewById(R.id.datepicker_worklicense_due);
			glWorkLicenseImg = (GridLayout) workLicenseView
					.findViewById(R.id.gridLayout);
			btAddNewImg = (Button) workLicenseView
					.findViewById(R.id.bt_add_img);
			btAddNewImg.setOnClickListener(this);
		}
		etWorkLicenseNo.setText(mWorkNo);
		if (builder == null) {
			builder = new AlertDialog.Builder(activity)
					.setTitle("更新上岗证书及到期时间")
					.setView(workLicenseView)
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
								}
							})
					.setPositiveButton("确认",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									String worklicenseNo = etWorkLicenseNo
											.getText().toString().trim();
									int selectYear = dpWorkLicenseDue.getYear();
									int selectMonth = dpWorkLicenseDue
											.getMonth() + 1;// java month start
															// from 0
									int selectDay = dpWorkLicenseDue
											.getDayOfMonth();
									Logr.e(selectYear + "年" + selectMonth + "月"
											+ selectDay + "日");
									if (TextUtils.isEmpty(worklicenseNo)) {
										Utilities
												.showToast("上岗证不能为空", activity);
										return;
									}
									startUpload(worklicenseNo, selectYear + "-"
											+ selectMonth + "-" + selectDay);
								}

							}).setOnDismissListener(new OnDismissListener() {

						@Override
						public void onDismiss(DialogInterface dialog) {
							// TODO Auto-generated method stub
							((ViewGroup) workLicenseView.getParent())
									.removeAllViews();// 去除会报异常
						}
					});
		}
		builder.show();

	}

	private void startUpload(final String worklicenseNo, String date) {
		// TODO Auto-generated method stub
		startProgressDialog(getResources().getString(
				R.string.loading_public_default));
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put("workLicenseNo", worklicenseNo);
		body.put("workLicenseDueDate", date);
		List<Map<String, Object>> workLicenseImgs = new ArrayList<Map<String, Object>>();
		for (String path : paths) {
			if (path != null) {
				HashMap<String, Object> workLicenseImg = new HashMap<String, Object>();
				workLicenseImg.put("content", ImageUtils.bitmapToString(path));
				workLicenseImg.put("attrName",
						ImageUtils.getBitmapAttrName(path));
				workLicenseImgs.add(workLicenseImg);
			}
		}
		body.put("workLicenseImg", workLicenseImgs);
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20080, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				mCertificateNo.setText(worklicenseNo);
				Utilities.showToast(response.msg, activity);
			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void bizStIs1Deal() {
				// TODO Auto-generated method stub

			}

			@Override
			public void stopDialog() {
				// TODO Auto-generated method stub

			}
		};
		conn.sendRequest();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case SELECT_PHOTO:
			if (resultCode == Activity.RESULT_OK) {
				ImageView newImg = new ImageView(activity);
				paths[glWorkLicenseImg.getChildCount()] = ImageUtils.getPath(
						activity, data.getData());
				newImg.setScaleType(ScaleType.CENTER_CROP);
				newImg.setTag(glWorkLicenseImg.getChildCount());
				newImg.setImageBitmap(ImageUtils.getSmallBitmap(ImageUtils
						.getPath(activity, data.getData())));
				newImg.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						int index = (Integer) v.getTag();
						paths[index] = null;
						for (int i = index; i < paths.length; i++) {
							if (i != paths.length - 1) {
								paths[i] = paths[i + 1];
							} else {
								paths[i] = null;
							}
						}
						glWorkLicenseImg.removeView(v);
						for (int i = 0; i < glWorkLicenseImg.getChildCount(); i++) {
							glWorkLicenseImg.getChildAt(i).setTag(i);
						}
					}
				});
				glWorkLicenseImg.addView(newImg,
						Utilities.dp2px(activity, 100),
						Utilities.dp2px(activity, 100));
			}
			break;
		case SELECT_CAMERA:
			if (resultCode == Activity.RESULT_OK) {
				ImageView newImg = new ImageView(activity);
				newImg.setScaleType(ScaleType.CENTER_CROP);
				newImg.setTag(glWorkLicenseImg.getChildCount());
				newImg.setImageBitmap(ImageUtils.getSmallBitmap(ImageUtils
						.getPath(activity, cameraUri)));
				paths[glWorkLicenseImg.getChildCount()] = ImageUtils.getPath(
						activity, cameraUri);
				newImg.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						int index = (Integer) v.getTag();
						File delF = new File(paths[index]);
						delF.delete();
						for (int i = index; i < paths.length; i++) {
							if (i != paths.length - 1) {
								paths[i] = paths[i + 1];
							} else {
								paths[i] = null;
							}
						}
						glWorkLicenseImg.removeView(v);
						for (int i = 0; i < glWorkLicenseImg.getChildCount(); i++) {
							glWorkLicenseImg.getChildAt(i).setTag(i);
						}
					}
				});
				glWorkLicenseImg.addView(newImg,
						Utilities.dp2px(activity, 100),
						Utilities.dp2px(activity, 100));
			}
			break;
		default:
			break;
		}
	}

	private void exitDialog() {
		// TODO Auto-generated method stub

		new AlertDialog.Builder(activity).setTitle("退出?")
				.setPositiveButton("退出", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						exit();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				}).show();
	}

	private void exit() {
		// TODO Auto-generated method stub
		HttpConnector<Bean> conn = new HttpConnector<Bean>(2, uid, certificate,
				null) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				Utilities.showToast(response.msg, activity);
				SharePreferencesUtils.put(activity, SharedPreferencesKeys.UID,
						"");
				SharePreferencesUtils.put(activity,
						SharedPreferencesKeys.CERTIFICATED, "");
				Intent intent = new Intent(activity, LoginActivity.class);
				startActivity(intent);
			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void bizStIs1Deal() {
				// TODO Auto-generated method stub

			}

			@Override
			public void stopDialog() {
				// TODO Auto-generated method stub

			}
		};
		conn.sendRequest();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		stackInstance.popActivity(activity);
	}

}
