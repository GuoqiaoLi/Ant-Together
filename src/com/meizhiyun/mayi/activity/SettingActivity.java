package com.meizhiyun.mayi.activity;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.meizhiyun.mayi.BaseApplication;
import com.meizhiyun.mayi.R;
import com.meizhiyun.mayi.bean.UrlConfig;
import com.meizhiyun.mayi.utils.AccessTokenKeeper;
import com.meizhiyun.mayi.utils.Constant;
import com.meizhiyun.mayi.utils.FolderSizeUtils;
import com.meizhiyun.mayi.utils.PreferenceUtil;
import com.meizhiyun.mayi.utils.SDUtils;
import com.meizhiyun.mayi.utils.SetScreenSizeUtils;
import com.tencent.tauth.Tencent;

/**
 * 
 * @类名称: SettingActivity
 * @类描述: 设置页面
 * @创建人：Guoqiao Li
 * @备注：
 * @version V1.0
 */
@ContentView(R.layout.setting_activity_layout)
public class SettingActivity extends BaseActivity {
	@ViewInject(R.id.iv_back)
	private ImageView ivBack;
	@ViewInject(R.id.tv_title)
	private TextView tvTitle;
	@ViewInject(R.id.ll_update)
	private LinearLayout llUpdate;
	@ViewInject(R.id.ll_clear)
	private LinearLayout llClear;
	@ViewInject(R.id.ll_suggest)
	private LinearLayout llSuggest;
	@ViewInject(R.id.ll_about)
	private LinearLayout llAbout;
	@ViewInject(R.id.tv_update)
	private TextView tvUpdate;
	@ViewInject(R.id.tv_clear)
	private TextView tvClear;
	@ViewInject(R.id.tv_suggest)
	private TextView tvSuggest;
	@ViewInject(R.id.tv_about)
	private TextView tvAbout;
	@ViewInject(R.id.iv_update)
	private ImageView ivUpdate;
	@ViewInject(R.id.iv_clear)
	private ImageView ivClear;
	@ViewInject(R.id.iv_suggest)
	private ImageView ivSuggest;
	@ViewInject(R.id.iv_about)
	private ImageView ivAbout;
	@ViewInject(R.id.iv_suggest_arrow)
	private ImageView ivSuggestArrow;
	@ViewInject(R.id.iv_about_arrow)
	private ImageView ivAboutArrow;
	@ViewInject(R.id.rl_title_bar)
	private RelativeLayout rlTitleBar;
	@ViewInject(R.id.btn_exit)
	private Button btnExit;
	@ViewInject(R.id.tv_space)
	private TextView tvSpace;
	@ViewInject(R.id.tv_version)
	private TextView tvVersion;
	@ViewInject(R.id.ll_loading)
	private LinearLayout llLoading;
	@ViewInject(R.id.iv_loading)
	private ImageView ivLoading;
	@ViewInject(R.id.tv_loading)
	private TextView tvLoading;
	private AnimationDrawable animationDrawable;
	private AlertDialog dialog;
	private String path;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化ViewUtils
		ViewUtils.inject(this);
		// 初始化布局
		initLayout();
	}

	// 初始化布局
	private void initLayout() {
		ivLoading.setBackgroundResource(R.drawable.loading_animation);
		animationDrawable = (AnimationDrawable) ivLoading.getBackground();
		String userid = PreferenceUtil.readString(this, "login", "userid");
		if (!TextUtils.isEmpty(userid)) {
			btnExit.setVisibility(View.VISIBLE);
		} else {
			btnExit.setVisibility(View.GONE);
		}

		ivBack.setBackgroundResource(R.drawable.back_selector);
		tvTitle.setText("设置");
		// 设置标题栏的高度
		LayoutParams params = rlTitleBar.getLayoutParams();
		int[] titleSizes = SetScreenSizeUtils.getTitleSize(this, 88);
		params.width = titleSizes[0];
		params.height = titleSizes[1];
		rlTitleBar.setLayoutParams(params);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivBack, 41, 66);
		SetScreenSizeUtils.setViewSizeFromWidth(this, llUpdate, 640, 100);
		SetScreenSizeUtils.setViewSizeFromWidth(this, llAbout, 640, 100);
		SetScreenSizeUtils.setViewSizeFromWidth(this, llClear, 640, 100);
		SetScreenSizeUtils.setViewSizeFromWidth(this, llSuggest, 640, 100);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivUpdate, 30, 35);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivClear, 28, 30);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivSuggest, 30, 25);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivAbout, 30, 30);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivSuggestArrow, 14, 24);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivAboutArrow, 14, 24);
		SetScreenSizeUtils.setViewSizeFromWidth(this, btnExit, 590, 81);

		path = SDUtils.getSDCardPath() + File.separator + "MaYi"
				+ File.separator + "icon" + File.separator;
		try {
			String folderSize = FolderSizeUtils.bytes2kb(FolderSizeUtils
					.getFolderSize(new File(path)));
			tvSpace.setText(folderSize);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 点击事件
	@OnClick({ R.id.btn_exit, R.id.iv_back, R.id.ll_clear, R.id.ll_update,
			R.id.ll_suggest, R.id.ll_about })
	public void click(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btn_exit:
			logoutMethod();
			break;
		case R.id.iv_back:
			finish();
			break;
		case R.id.ll_clear:
			clearFileMethod();
			break;
		case R.id.ll_update:
			checkVersionMehthod();
			break;
		case R.id.ll_suggest:
			intent = new Intent(this, SuggestActivity.class);
			startActivity(intent);
			break;
		case R.id.ll_about:
			intent = new Intent(this, AboutUsActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	// 检查更新的方法
	private void checkVersionMehthod() {
		tvLoading.setText("检查更新中...");
		llLoading.setVisibility(View.VISIBLE);
		animationDrawable.start();
		HttpUtils httpUtils = new HttpUtils(20000)
				.configCurrentHttpCacheExpiry(10000);
		RequestParams params = new RequestParams();
		params.addBodyParameter("action", "VersionControl");
		params.addBodyParameter("type", "android");
		httpUtils.send(HttpMethod.POST, UrlConfig.BASICURL, params,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						animationDrawable.stop();
						llLoading.setVisibility(View.GONE);
						if (!TextUtils.isEmpty(responseInfo.result)) {
							try {
								JSONObject jsonObject = new JSONObject(
										responseInfo.result);
								String version = jsonObject
										.getString("version");
								if (!TextUtils.isEmpty(version)) {
									float apkVersion = Float
											.parseFloat(version);
									try {
										String versionName = getPackageManager()
												.getPackageInfo(
														getPackageName(), 0).versionName;
										float localVersion = Float
												.parseFloat(versionName);
										if (apkVersion > localVersion) {
											String downloadUrl = jsonObject
													.getString("url");
											// 更新版本的对话框
											Intent it = new Intent(
													Intent.ACTION_VIEW, Uri
															.parse(downloadUrl));
											it.setClassName(
													"com.android.browser",
													"com.android.browser.BrowserActivity");
											startActivity(it);
										} else {
											BaseApplication.toastMethod(
													SettingActivity.this,
													"当前已是最新版本了哦!", 1);
										}
									} catch (NameNotFoundException e) {
										e.printStackTrace();
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
					@Override
					public void onFailure(HttpException error, String msg) {
					}
				});
	}

	// 清空文件的方法
	private void clearFileMethod() {
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("您确定要清空缓存吗?");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				File file = new File(path);
				if (!file.exists()) {
					return;
				}
				File[] listFiles = file.listFiles();
				for (int i = 0; i < listFiles.length; i++) {
					File file2 = listFiles[i];
					if (file2.exists() && file2 != null && file2.length() > 0
							&& !"icon.jpg".equals(file2.getName())) {
						file2.delete();
					}
					try {
						String folderSize = FolderSizeUtils
								.bytes2kb(FolderSizeUtils
										.getFolderSize(new File(path)));
						tvSpace.setText(folderSize);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				PreferenceUtil.clear(SettingActivity.this, "usersInfo");
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog = builder.create();
		dialog.show();
	}

	// 退出登录的方法
	private void logoutMethod() {
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("您确定要退出登录吗?");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				String logintype = PreferenceUtil.readString(
						SettingActivity.this, "login", "logintype");
				if ("1".equals(logintype)) {
					AccessTokenKeeper.clear(SettingActivity.this);
				} else if ("2".equals(logintype)) {
				} else if ("3".equals(logintype)) {
					Tencent mTencent = Tencent.createInstance(
							Constant.QQ_APP_ID, SettingActivity.this);
					mTencent.logout(SettingActivity.this);
				}
				PreferenceUtil.clear(SettingActivity.this, "login");
				PreferenceUtil.write(SettingActivity.this, "login", "isactive",
						"no");
				PreferenceUtil.write(SettingActivity.this, "login", "islogout",
						true);
				SettingActivity.this.finish();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog = builder.create();
		dialog.show();
	}

}
