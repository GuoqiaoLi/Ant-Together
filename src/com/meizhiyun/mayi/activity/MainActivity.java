package com.meizhiyun.mayi.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMapTouchListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
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
import com.meizhiyun.mayi.adapter.TeammatesListAdapter;
import com.meizhiyun.mayi.bean.LatBean;
import com.meizhiyun.mayi.bean.LocAndMarker;
import com.meizhiyun.mayi.bean.ShareLatResult;
import com.meizhiyun.mayi.bean.UrlConfig;
import com.meizhiyun.mayi.bean.UserLatInfo;
import com.meizhiyun.mayi.listener.OnGetJsonDataListener;
import com.meizhiyun.mayi.netstate.NetWorkUtil;
import com.meizhiyun.mayi.service.LocationService;
import com.meizhiyun.mayi.thread.ShareMyPosRunnable;
import com.meizhiyun.mayi.utils.CircleImageView;
import com.meizhiyun.mayi.utils.DragLayout;
import com.meizhiyun.mayi.utils.JsonTools;
import com.meizhiyun.mayi.utils.MarkerJump;
import com.meizhiyun.mayi.utils.OpenGPSSetting;
import com.meizhiyun.mayi.utils.PreferenceUtil;
import com.meizhiyun.mayi.utils.SetScreenSizeUtils;
import com.meizhiyun.mayi.utils.ShareAndSetPos;
import com.meizhiyun.mayi.utils.DragLayout.DragListener;
import com.meizhiyun.mayi.view.DownloadOfflineMapWindow;
import com.meizhiyun.mayi.view.UpdateVersionWindow;
import com.nineoldandroids.view.ViewHelper;

/**
 * 
 * @类名称: MainActivity
 * @类描述: 主界面
 * @创建人：LiXinYang
 * @备注：
 * @version V1.0
 */

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity implements LocationSource,
		AMapLocationListener, OnItemClickListener, OnInfoWindowClickListener,
		OnMarkerClickListener, InfoWindowAdapter, OnMapClickListener,
		OnMapTouchListener {
	@ViewInject(R.id.map)
	private MapView mapView;
	@ViewInject(R.id.dl)
	private DragLayout dl;
	@ViewInject(R.id.iv_back)
	private ImageView ivBack;
	@ViewInject(R.id.tv_title)
	private TextView tvTitle;
	@ViewInject(R.id.iv_teammates)
	private ImageView ivTeammates;
	@ViewInject(R.id.iv_invite_friend)
	private ImageView ivInviteFriend;
	@ViewInject(R.id.iv_find_team)
	private ImageView ivFindTeam;
	@ViewInject(R.id.iv_edit)
	private ImageView ivEdit;
	@ViewInject(R.id.rl_title_bar)
	private RelativeLayout rlTitleBar;
	@ViewInject(R.id.rl_bottom_bar)
	private RelativeLayout rlBottomBar;
	@ViewInject(R.id.civ)
	private CircleImageView civ;
	@ViewInject(R.id.tv_nickname)
	private TextView tvNickname;
	@ViewInject(R.id.tv_veri)
	private TextView tvVeri;
	@ViewInject(R.id.tv_offline)
	private TextView tvOffline;
	@ViewInject(R.id.tv_history)
	private TextView tvHistory;
	@ViewInject(R.id.tv_setting)
	private TextView tvSetting;
	@ViewInject(R.id.ll_verification)
	private LinearLayout llVerification;
	@ViewInject(R.id.ll_offline)
	private LinearLayout llOffline;
	@ViewInject(R.id.ll_history)
	private LinearLayout llHistory;
	@ViewInject(R.id.ll_setting)
	private LinearLayout llSetting;
	@ViewInject(R.id.ll_list)
	private LinearLayout llList;
	@ViewInject(R.id.ll_bottom_bar)
	private LinearLayout llBottomBar;
	@ViewInject(R.id.ll_bottom)
	private LinearLayout llBottom;
	@ViewInject(R.id.lv)
	private ListView lv;
	@ViewInject(R.id.iv_share_lat)
	private ImageView ivShareLat;
	@ViewInject(R.id.iv_share_route)
	private ImageView ivShareRoute;
	@ViewInject(R.id.ll_no_network)
	private LinearLayout llNoNetwork;
	@ViewInject(R.id.iv_circle_point)
	private ImageView ivCirclePoint;
	@ViewInject(R.id.iv_exit)
	private ImageView ivExit;
	@ViewInject(R.id.tv_msg_num)
	private TextView tvMsgNum;
	@ViewInject(R.id.iv_my_lat)
	private ImageView ivMylat;

	private boolean isOver = false;
	private int[] titleSizes;
	private AMap aMap;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private int viewHeight;
	private boolean isshow = false;
	private boolean hasNet = true;
	private boolean hasLogined = false;
	private LatLng oldll = null;
	private int count = 0;
	private List<UserLatInfo> infoList;
	private Thread setOtherPosThread;
	private List<LocAndMarker> markerList;
	private boolean isAddHeader = false;
	private LatLng mLatlng = null;
	private boolean showMe = true;
	private float zoom = 16;
	private boolean hasActive = false;
	private boolean isLoc = true;
	private String locUserid;
	private boolean isLocation = true;
	private boolean openGps = false;
	private boolean isFirstNotice = true;
	private boolean isOpen = false;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (teammatesListAdapter != null) {
					teammatesListAdapter.deleteUser(isDelete, infoList);
				}
				if (!isAddHeader) {
					setTeammatesList(infoList);
					isAddHeader = true;
				}
				break;
			case 1:
				if (!hasNet) {
					llNoNetwork.setVisibility(View.VISIBLE);
				} else {
					llNoNetwork.setVisibility(View.GONE);
				}
				break;
			case 2:
				if (count == 0) {
					count++;
				}
				break;
			case 3:
				if (msgCount > 0) {
					ivCirclePoint.setVisibility(View.VISIBLE);
					tvMsgNum.setVisibility(View.VISIBLE);
					tvMsgNum.setText("+" + msgCount);
				} else {
					ivCirclePoint.setVisibility(View.GONE);
				}
				break;
			case 4:
				if (infoList != null && infoList.size() > 0) {
					hasActive = true;
					tvTitle.setText("当前有" + (infoList.size() + 1) + "人同行");
					isactive = "yes";
					PreferenceUtil.write(MainActivity.this, "login",
							"isactive", "yes");
					if (!TextUtils.isEmpty(isactive) && "yes".equals(isactive)
							&& !TextUtils.isEmpty(iscaptain)
							&& "yes".equals(iscaptain)) {
						ivEdit.setVisibility(View.VISIBLE);
					} else {
						ivEdit.setVisibility(View.GONE);
					}
				} else {
					tvTitle.setText("蚂蚁聚聚");
					ivEdit.setVisibility(View.GONE);
					if (hasActive) {
						BaseApplication.toastMethod(MainActivity.this,
								"活动已被解散", 1);
						hasActive = false;
						if (teammatesListAdapter != null) {
							teammatesListAdapter.deleteUser(false, infoList);
							isshow = false;
							llBottomBar.setPadding(0, 0, 0, viewHeight);
							ivTeammates.setBackgroundResource(R.drawable.down);
							isDelete = false;
							teammatesListAdapter.deleteUser(isDelete, infoList);
						}
						clearMarker();
						for (int i = 0; i < plList.size(); i++) {
							plList.get(i).remove();
						}
					}
				}
				break;
			case 5:
				clearMarker();
				showMe = true;
				init();
				break;
			case 6:
				if (!openGps) {
					OpenGPSSetting.isOpenDialog(MainActivity.this);
					openGps = true;
				}
				break;
			default:
				break;
			}
		};
	};
	private int height;
	private String userid;
	private String nickname;
	private String picurl;
	private String isactive;
	private String iscaptain;
	private boolean isAuto = false;
	private boolean flag = true;
	private String isview;
	private String isshare;
	private float distance;
	private int msgCount = 0;
	private Thread shareMyPosThread;
	private HttpUtils httpUtils;
	private ShareAndSetPos pos;
	private String address;
	private View header;
	private TeammatesListAdapter teammatesListAdapter;
	private boolean isDelete = false;
	private boolean isFirst = true;
	private LatLng latLng;
	private LocationManager locationManager;
	private List<Polyline> plList;
	private Polyline polyline = null;
	private DownloadOfflineMapWindow downloadOfflineWindow;
	private UpdateVersionWindow updateVersionWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化ViewUtils
		ViewUtils.inject(this);
		mapView.onCreate(savedInstanceState);// 必须要写
		// 初始化地图
		initMap();
		// 初始化控件
		initView();
		// 初始化布局,并屏幕适配
		initLayout();
		// DragLayout()点击事件
		initDragLayout();
	}

	// 初始化控件
	private void initView() {
		tvTitle.setText("蚂蚁聚聚");
		ivBack.setBackgroundResource(R.drawable.menu);
		markerList = new ArrayList<LocAndMarker>();
		userid = PreferenceUtil.readString(this, "login", "userid");
		isAuto = PreferenceUtil.readBoolean(this, "login", "isAuto");
		PreferenceUtil.write(this, "setPos", "isFirst", true);
		if (hasNet && isAuto) {
			if (!TextUtils.isEmpty(userid)) {
				autoLogin();
			}
		}
		pos = new ShareAndSetPos();
		setOtherPos();
		userid = PreferenceUtil.readString(this, "login", "userid");
		teammatesListAdapter = new TeammatesListAdapter(MainActivity.this,
				MainActivity.this, userid);
		lv.setOnItemClickListener(this);
		plList = new ArrayList<Polyline>();
		// 检查版本更新
		checkVersionMehthod();
		downloadOfflineWindow = new DownloadOfflineMapWindow(this);
	}

	// 下载离线地图的方法
	private void downloadOfflineMap() {
		if (BaseApplication.judgeNetwork(this)
				&& NetWorkUtil.isWifiConnected(this)) {
			downloadOfflineWindow.showAtLocation(dl, Gravity.CENTER, 0, 0);
		}
	}

	// 检查更新的方法
	private void checkVersionMehthod() {
		httpUtils = new HttpUtils(20000).configCurrentHttpCacheExpiry(10000);
		RequestParams params = new RequestParams();
		params.addBodyParameter("action", "VersionControl");
		params.addBodyParameter("type", "android");
		httpUtils.send(HttpMethod.POST, UrlConfig.BASICURL, params,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
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
											String info = jsonObject
													.getString("info");
											// 更新版本的对话框
											changeVersionDialog(info,
													downloadUrl);
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

	// 更新版本的对话框
	private void changeVersionDialog(String info, final String downloadUrl) {
		updateVersionWindow = new UpdateVersionWindow(this, info, downloadUrl);
	}

	// 初始化信息
	private void init() {
		boolean islogout = PreferenceUtil
				.readBoolean(this, "login", "islogout");
		if (islogout) {
			clearMarker();
			PreferenceUtil.write(this, "login", "islogout", false);
			dl.close();
			civ.setImageResource(R.drawable.user);
			ivEdit.setVisibility(View.GONE);
			infoList = null;
			ivExit.setVisibility(View.GONE);
			tvTitle.setText("蚂蚁聚聚");
			ivShareLat.setVisibility(View.GONE);
			ivShareRoute.setVisibility(View.GONE);
		}
		userid = PreferenceUtil.readString(this, "login", "userid");
		hasLogined = PreferenceUtil.readBoolean(this, "login", "hasLogined");
		picurl = PreferenceUtil.readString(this, "login", "picurl");
		if (!TextUtils.isEmpty(picurl) && !picurl.contains("http")) {
			Bitmap bitmap = BitmapFactory.decodeFile(picurl);
			civ.setImageBitmap(bitmap);
			if (header != null) {
				CircleImageView civList = (CircleImageView) header
						.findViewById(R.id.civ_list);
				civList.setImageBitmap(bitmap);
			}

		} else if (hasNet && !TextUtils.isEmpty(picurl)
				&& picurl.contains("http")) {
			downloadIcon();
		}
		nickname = PreferenceUtil.readString(this, "login", "nickname");
		isactive = PreferenceUtil.readString(this, "login", "isactive");
		iscaptain = PreferenceUtil.readString(this, "login", "iscaptain");
		isview = PreferenceUtil.readString(this, "login", "isview");
		isshare = PreferenceUtil.readString(this, "login", "isshare");
		if (!TextUtils.isEmpty(isactive) && "yes".equals(isactive)) {
			ivShareLat.setVisibility(View.VISIBLE);
			if (!TextUtils.isEmpty(isview) && "yes".equals(isview)) {
				ivShareLat.setBackgroundResource(R.drawable.zuji_click);
			} else {
				ivShareLat.setBackgroundResource(R.drawable.zuji);
			}
			if (!TextUtils.isEmpty(iscaptain) && "yes".equals(iscaptain)) {
				ivShareRoute.setVisibility(View.VISIBLE);
				ivExit.setVisibility(View.VISIBLE);
				if (!TextUtils.isEmpty(isshare) && "yes".equals(isshare)) {
					ivShareRoute.setBackgroundResource(R.drawable.guiji_click);
				} else {
					ivShareRoute.setBackgroundResource(R.drawable.guiji);
				}
			} else if (!TextUtils.isEmpty(iscaptain) && "no".equals(iscaptain)) {
				ivShareRoute.setVisibility(View.GONE);
				ivExit.setVisibility(View.GONE);
			}
		} else {
			ivShareLat.setVisibility(View.GONE);
			ivShareRoute.setVisibility(View.GONE);
			ivExit.setVisibility(View.GONE);
			tvTitle.setText("蚂蚁聚聚");
			tvNickname.setText("请登录");
		}

		if (!TextUtils.isEmpty(nickname)) {
			tvNickname.setText(nickname);
		}
	}

	// 下载头像的方法
	public void downloadIcon() {
		httpUtils = new HttpUtils(20000).configCurrentHttpCacheExpiry(10000);
		httpUtils.download(picurl, UrlConfig.ICONPATH,
				new RequestCallBack<File>() {

					@Override
					public void onSuccess(ResponseInfo<File> responseInfo) {
						PreferenceUtil.write(MainActivity.this, "login",
								"picurl", UrlConfig.ICONPATH);
						Bitmap bitmap = BitmapFactory
								.decodeFile(UrlConfig.ICONPATH);
						civ.setImageBitmap(bitmap);
						if (header != null) {
							CircleImageView civList = (CircleImageView) header
									.findViewById(R.id.civ_list);
							civList.setImageBitmap(bitmap);
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						BaseApplication.toastMethod(MainActivity.this, msg, 0);
					}
				});
	}

	/**
	 * 初始化AMap对象
	 */
	private void initMap() {
		if (aMap == null) {
			aMap = mapView.getMap();
		}
		aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
		setUpMap();
	}

	// 初始化头部视图
	private void initHeader() {
		header = LayoutInflater.from(this).inflate(
				R.layout.teammates_list_item, null);
		LinearLayout llTeammatesList = (LinearLayout) header
				.findViewById(R.id.ll_teammates_list);
		CircleImageView civList = (CircleImageView) header
				.findViewById(R.id.civ_list);
		TextView tvNicknameList = (TextView) header
				.findViewById(R.id.tv_nickname_list);
		SetScreenSizeUtils.setViewSizeFromWidth(this, civList, 80, 80);
		SetScreenSizeUtils
				.setViewSizeFromWidth(this, llTeammatesList, 640, 115);
		tvMsgNum.setVisibility(View.GONE);

		tvNicknameList.setText(nickname);
		if (!TextUtils.isEmpty(picurl) && !picurl.contains("http")) {
			civList.setImageBitmap(BitmapFactory.decodeFile(picurl));
		} else if (hasNet && !TextUtils.isEmpty(picurl)
				&& picurl.contains("http")) {
			httpUtils = new HttpUtils(20000)
					.configCurrentHttpCacheExpiry(10000);
			httpUtils.download(picurl, UrlConfig.ICONPATH,
					new RequestCallBack<File>() {
						@Override
						public void onSuccess(ResponseInfo<File> responseInfo) {
							PreferenceUtil.write(MainActivity.this, "login",
									"picurl", UrlConfig.ICONPATH);
							civ.setImageBitmap(BitmapFactory
									.decodeFile(UrlConfig.ICONPATH));
							if (header != null) {
								CircleImageView civList = (CircleImageView) header
										.findViewById(R.id.civ_list);
								civList.setImageBitmap(BitmapFactory
										.decodeFile(UrlConfig.ICONPATH));
							}
						}

						@Override
						public void onFailure(HttpException error, String msg) {
						}
					});
		}
		header.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showMe = true;
				aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
				hideInfoWindow();
				isLoc = true;
			}
		});
	}

	// 设置地图属性
	private void setUpMap() {
		// 自定义系统定位小蓝点
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		ImageView iv = new ImageView(this);
		int[] screenSize = SetScreenSizeUtils.getScreenSize(this);
		int viewWidth = SetScreenSizeUtils.getViewWidthFromScreen(
				screenSize[0], 100);
		int viewHeight = SetScreenSizeUtils.getViewHeight(viewWidth, 100, 100);
		LayoutParams params = new LayoutParams(viewWidth, viewHeight);
		iv.setLayoutParams(params);
		iv.setBackgroundResource(R.drawable.location_marker);
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromView(iv));
		myLocationStyle.strokeColor(Color.argb(0x00, 0xff, 0xff, 0xff));// 设置圆形的边框颜色
		myLocationStyle.radiusFillColor(Color.argb(0x00, 0xff, 0xff, 0xff));// 设置圆形的填充颜色
		// myLocationStyle.anchor(int,int)//设置小蓝点的锚点
		// myLocationStyle.strokeWidth(0.1f);// 设置圆形的边框粗细
		aMap.setMyLocationStyle(myLocationStyle);
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		// 设置定位的类型为定位模式：定位（AMap.LOCATION_TYPE_LOCATE）、跟随（AMap.LOCATION_TYPE_MAP_FOLLOW）
		// 地图根据面向方向旋转（AMap.LOCATION_TYPE_MAP_ROTATE）三种模式
		aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);
		aMap.setOnInfoWindowClickListener(this);
		aMap.setInfoWindowAdapter(this);
		aMap.setOnMarkerClickListener(this);
		aMap.setOnMapClickListener(this);
		aMap.setOnMapTouchListener(this);
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	}

	// DragLayout()点击事件
	private void initDragLayout() {
		dl.setDragListener(new DragListener() {
			@Override
			public void onOpen() {
				isOpen = true;
			}

			@Override
			public void onClose() {
				isOpen = false;
			}

			@Override
			public void onDrag(float percent) {
				ViewHelper.setAlpha(ivBack, 1 - percent);
			}
		});
	}

	// 初始化布局
	private void initLayout() {
		// 设置标题栏的高度
		LayoutParams params = rlTitleBar.getLayoutParams();
		titleSizes = SetScreenSizeUtils.getTitleSize(this, 88);
		params.width = titleSizes[0];
		params.height = titleSizes[1];
		rlTitleBar.setLayoutParams(params);

		// 设置底部bar的高度
		LayoutParams params2 = rlBottomBar.getLayoutParams();
		titleSizes = SetScreenSizeUtils.getTitleSize(this, 100);
		params2.width = titleSizes[0];
		params2.height = titleSizes[1];
		rlBottomBar.setLayoutParams(params2);

		LayoutParams layoutParams = llBottom.getLayoutParams();
		layoutParams.height = titleSizes[1];
		llBottom.setLayoutParams(layoutParams);

		SetScreenSizeUtils.setViewSizeFromWidth(this, ivTeammates, 70, 70);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivEdit, 66, 69);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivBack, 50, 35);
		SetScreenSizeUtils.setViewSizeFromWidth(this, civ, 130, 130);
		SetScreenSizeUtils.setViewSizeFromWidth(this, llVerification, 640, 100);
		SetScreenSizeUtils.setViewSizeFromWidth(this, llOffline, 640, 100);
		SetScreenSizeUtils.setViewSizeFromWidth(this, llHistory, 640, 100);
		SetScreenSizeUtils.setViewSizeFromWidth(this, llSetting, 640, 100);
		SetScreenSizeUtils.setViewSizeFromWidth(this, llList, 640, 437);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivShareLat, 93, 93);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivShareRoute, 93, 93);
		SetScreenSizeUtils.setViewSizeFromWidth(this, llNoNetwork, 640, 70);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivInviteFriend, 161, 69);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivFindTeam, 161, 69);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivExit, 127, 31);

		int[] screenSize = SetScreenSizeUtils.getScreenSize(this);
		int viewWidth = SetScreenSizeUtils.getViewWidthFromScreen(
				screenSize[0], 640);
		height = SetScreenSizeUtils.getViewHeight(viewWidth, 640, 437);
		viewHeight = -height;
		llBottomBar.setPadding(0, 0, 0, viewHeight);
		llBottomBar.setBackgroundResource(R.drawable.ban);
	}

	// 点击事件
	@OnClick({ R.id.iv_back, R.id.iv_teammates, R.id.iv_invite_friend,
			R.id.iv_find_team, R.id.ll_setting, R.id.ll_offline,
			R.id.ll_offline, R.id.ll_verification, R.id.iv_exit,
			R.id.iv_share_lat, R.id.iv_share_route, R.id.iv_edit,
			R.id.ll_history, R.id.iv_my_lat, R.id.ll_no_network })
	public void click(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.iv_back:
			dl.open();
			ivCirclePoint.setVisibility(View.GONE);
			msgCount = 0;
			break;
		case R.id.iv_teammates:
			if (!TextUtils.isEmpty(userid) && hasLogined) {
				if (!isshow) {
					if ("yes".equals(isactive)) {
						llBottomBar.setPadding(0, 0, 0, 0);
						isshow = true;
						ivTeammates.setBackgroundResource(R.drawable.up);
					} else {
						BaseApplication
								.toastMethod(this, "您还没有好友哦,快去找小伙伴吧!", 1);
					}
				} else {
					llBottomBar.setPadding(0, 0, 0, viewHeight);
					isshow = false;
					ivTeammates.setBackgroundResource(R.drawable.down);
					isDelete = false;
					teammatesListAdapter.deleteUser(isDelete, infoList);
				}
			} else if (TextUtils.isEmpty(userid) || !hasLogined) {
				intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.iv_invite_friend:
			if (!TextUtils.isEmpty(userid) && hasLogined) {
				intent = new Intent(this, InviteActivity.class);
				startActivity(intent);
			} else if (TextUtils.isEmpty(userid) || !hasLogined) {
				intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.iv_find_team:
			if (!TextUtils.isEmpty(userid) && hasLogined
					&& "no".equals(isactive)) {
				intent = new Intent(this, FindTeamActivity.class);
				startActivity(intent);
			} else if (!TextUtils.isEmpty(userid) && hasLogined
					&& "yes".equals(isactive)) {
				showCantAddDialog();
			} else if (TextUtils.isEmpty(userid) || !hasLogined) {
				intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.ll_setting:
			intent = new Intent(this, SettingActivity.class);
			startActivity(intent);
			break;
		case R.id.ll_offline:
			intent = new Intent(this, OfflineCityListActivity.class);
			startActivity(intent);
			break;
		case R.id.ll_verification:
			ivCirclePoint.setVisibility(View.GONE);
			tvMsgNum.setVisibility(View.GONE);
			if (!TextUtils.isEmpty(userid)) {
				intent = new Intent(this, VerificationActivity.class);
				startActivity(intent);
			} else {
				intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.iv_exit:
			logoutDialog();
			break;
		case R.id.iv_edit:
			if (!isDelete) {
				isDelete = true;
				teammatesListAdapter.deleteUser(isDelete, infoList);
				llBottomBar.setPadding(0, 0, 0, 0);
				isshow = true;
				ivTeammates.setBackgroundResource(R.drawable.up);
			} else {
				isDelete = false;
				teammatesListAdapter.deleteUser(isDelete, infoList);
			}
			break;
		case R.id.iv_share_lat:
			if ("yes".equals(isview)) {
				isview = "no";
				ivShareLat.setBackgroundResource(R.drawable.zuji);
			} else {
				isview = "yes";
				ivShareLat.setBackgroundResource(R.drawable.zuji_click);
			}
			// 是否显示自己位置的接口
			showLatMethod();
			break;
		case R.id.iv_share_route:
			if ("yes".equals(isshare)) {
				isshare = "no";
				ivShareRoute.setBackgroundResource(R.drawable.guiji);
				if (plList != null && plList.size() > 0) {
					for (int i = 0; i < plList.size(); i++) {
						plList.get(i).remove();
					}
				}
			} else {
				isshare = "yes";
				ivShareRoute.setBackgroundResource(R.drawable.guiji_click);
			}
			showRouteMethod();
			break;
		case R.id.ll_history:
			if (!TextUtils.isEmpty(userid)) {
				intent = new Intent(this, HistoryRouteActivity.class);
				startActivity(intent);
			} else {
				intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.iv_my_lat:
			showMe = true;
			aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
			hideInfoWindow();
			isLoc = true;
			break;

		case R.id.ll_no_network:
			// 跳转到系统的网络设置界面
			// 先判断当前系统版本
			intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	// 退出同行的dialog
	private void logoutDialog() {
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示");
		if ("yes".equals(iscaptain)) {
			builder.setMessage("您确定要解散活动吗？");
		} else {
			builder.setMessage("您确定要退出活动吗？");
		}
		builder.setPositiveButton("退出当前同行",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						logoutMethod();
					}

				});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	// 显示不可以加入两个组织的对话框
	private void showCantAddDialog() {
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示");
		builder.setMessage("不能同时加入两个小组哦");
		builder.setPositiveButton("退出当前同行",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						logoutMethod();
					}

				});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	// 是否共享自己位置的方法
	private void showRouteMethod() {
		HttpUtils httpUtils = new HttpUtils(20000)
				.configCurrentHttpCacheExpiry(10000);
		RequestParams params = new RequestParams();
		params.addBodyParameter("action", "Share");
		params.addBodyParameter("userid", userid);
		params.addBodyParameter("isshare", isshare);
		httpUtils.send(HttpMethod.POST, UrlConfig.BASICURL, params,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (!TextUtils.isEmpty(responseInfo.result)) {
							try {
								JSONObject jsonObject = new JSONObject(
										responseInfo.result);
								String result = jsonObject.getString("result");
								if ("yes".equals(result)) {
									BaseApplication.toastMethod(
											MainActivity.this, "您已共享您的轨迹", 1);
									isshare = "yes";
									ivShareRoute
											.setBackgroundResource(R.drawable.guiji_click);
								} else if ("no".equals(result)) {
									BaseApplication.toastMethod(
											MainActivity.this, "您已隐藏您的轨迹", 1);
									isshare = "no";
									ivShareRoute
											.setBackgroundResource(R.drawable.guiji);
								} else if ("error".equals(result)) {
									BaseApplication.toastMethod(
											MainActivity.this, "网络访问出错", 1);
									if (!TextUtils.isEmpty(isshare)
											&& "yes".equals(isshare)) {
										isshare = "no";
										ivShareRoute
												.setBackgroundResource(R.drawable.guiji);
									} else {
										isshare = "yes";
										ivShareRoute
												.setBackgroundResource(R.drawable.guiji_click);
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						BaseApplication.toastMethod(MainActivity.this, "", 0);
					}
				});
	}

	// 是否显示自己位置的方法
	private void showLatMethod() {
		HttpUtils httpUtils = new HttpUtils(20000)
				.configCurrentHttpCacheExpiry(10000);
		RequestParams params = new RequestParams();
		params.addBodyParameter("action", "View");
		params.addBodyParameter("userid", userid);
		params.addBodyParameter("isview", isview);
		httpUtils.send(HttpMethod.POST, UrlConfig.BASICURL, params,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (!TextUtils.isEmpty(responseInfo.result)) {
							try {
								JSONObject jsonObject = new JSONObject(
										responseInfo.result);
								String result = jsonObject.getString("result");
								if ("yes".equals(result)) {
									BaseApplication.toastMethod(
											MainActivity.this, "您已共享您的位置", 1);
									isview = "yes";
									ivShareLat
											.setBackgroundResource(R.drawable.zuji_click);
								} else if ("no".equals(result)) {
									BaseApplication.toastMethod(
											MainActivity.this, "您已隐藏您的位置", 1);
									isview = "no";
									ivShareLat
											.setBackgroundResource(R.drawable.zuji);
								} else if ("error".equals(result)) {
									BaseApplication.toastMethod(
											MainActivity.this, "网络访问出错", 1);
									if (!TextUtils.isEmpty(isview)
											&& "yes".equals(isview)) {
										isview = "no";
										ivShareLat
												.setBackgroundResource(R.drawable.zuji);
									} else {
										isview = "yes";
										ivShareLat
												.setBackgroundResource(R.drawable.zuji_click);
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						BaseApplication.toastMethod(MainActivity.this, "", 0);
					}
				});
	}

	// 退出活动的方法
	private void logoutMethod() {
		HttpUtils httpUtils = new HttpUtils(20000)
				.configCurrentHttpCacheExpiry(10000);
		RequestParams params = new RequestParams();
		params.addBodyParameter("action", "Quit");
		params.addBodyParameter("userid", userid);
		params.addBodyParameter("iscaptain", iscaptain);
		httpUtils.send(HttpMethod.POST, UrlConfig.BASICURL, params,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (!TextUtils.isEmpty(responseInfo.result)) {
							try {
								JSONObject jsonObject = new JSONObject(
										responseInfo.result);
								String result = jsonObject.getString("result");
								if ("success".equals(result)) {
									clearMarker();
									if ("yes".equals(iscaptain)) {
										BaseApplication.toastMethod(
												MainActivity.this, "您已解散活动", 1);
									} else if ("no".equals(iscaptain)) {
										BaseApplication.toastMethod(
												MainActivity.this, "成功退出活动", 1);
									}
									isactive = "no";
									PreferenceUtil.write(MainActivity.this,
											"login", "isactive", isactive);
									handler.sendEmptyMessage(5);
									if (plList != null && plList.size() > 0) {
										for (int i = 0; i < plList.size(); i++) {
											plList.get(i).remove();
										}
									}
								} else if ("fail".equals(result)) {
									if ("yes".equals(iscaptain)) {
										BaseApplication.toastMethod(
												MainActivity.this, "退出活动失败", 1);
									} else if ("no".equals(iscaptain)) {
										BaseApplication.toastMethod(
												MainActivity.this, "解散活动失败", 1);
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						BaseApplication.toastMethod(MainActivity.this, msg, 0);
					}
				});
	}

	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
			// 注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
			// 在定位结束后，在合适的生命周期调用destroy()方法
			// 其中如果间隔时间为-1，则定位只定一次
			mAMapLocationManager.requestLocationData(
					LocationProviderProxy.AMapNetwork, 5000, 10, this);
		}
		mAMapLocationManager.setGpsEnable(true);
	}

	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destroy();
		}
		mAMapLocationManager = null;
	}

	@Override
	public void onLocationChanged(Location location) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (mListener != null && amapLocation != null) {
			if (amapLocation.getAMapException().getErrorCode() == 0) {
				mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
			}
		}
		double latitude = amapLocation.getLatitude();
		double longitude = amapLocation.getLongitude();
		latLng = new LatLng(latitude, longitude);
		isLocation = false;
		if (BaseApplication.judgeNetwork(this)
				&& !NetWorkUtil.isWifiConnected(this)
				&& locationManager
						.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			mAMapLocationManager.requestLocationData(
					LocationManagerProxy.GPS_PROVIDER, 5000, 10, this);
		} else if (BaseApplication.judgeNetwork(this)
				&& NetWorkUtil.isWifiConnected(this)) {
			mAMapLocationManager.requestLocationData(
					LocationProviderProxy.AMapNetwork, 5000, 10, this);
		}
		if (showMe) {
			mLatlng = latLng;
		}
		if (isLoc && showMe) {
			aMap.moveCamera(CameraUpdateFactory.changeLatLng(mLatlng));
		}
		if (isFirst) {
			zoom = 16;
			isFirst = false;
		} else {
			zoom = aMap.getCameraPosition().zoom;
		}
		boolean dontNotice = PreferenceUtil.readBoolean(this, "window",
				"dontNotice");
		if (isFirstNotice && !dontNotice) {
			// 下载离线地图
			downloadOfflineMap();
			isFirstNotice = false;
		}
		if (updateVersionWindow != null) {
			updateVersionWindow.showAtLocation(dl, Gravity.CENTER, 0, 0);
		}
		aMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));
		String city = amapLocation.getCity().toString();
		String cityCode = amapLocation.getCityCode().toString();
		address = amapLocation.getAddress();
		PreferenceUtil.write(this, "CurrentCity", "city", city);
		PreferenceUtil.write(this, "CurrentCity", "cityCode", cityCode);
		if (!TextUtils.isEmpty(isactive) && "yes".equals(isactive)) {
			ivShareLat.setVisibility(View.VISIBLE);
			ivExit.setVisibility(View.VISIBLE);
			if (!TextUtils.isEmpty(isview) && "yes".equals(isview)) {
				ivShareLat.setBackgroundResource(R.drawable.zuji_click);
			} else {
				ivShareLat.setBackgroundResource(R.drawable.zuji);
			}
			if (!TextUtils.isEmpty(iscaptain) && "yes".equals(iscaptain)) {
				ivShareRoute.setVisibility(View.VISIBLE);
				if (!TextUtils.isEmpty(isshare) && "yes".equals(isshare)) {
					ivShareRoute.setBackgroundResource(R.drawable.guiji_click);
				} else {
					ivShareRoute.setBackgroundResource(R.drawable.guiji);
				}
			} else if (!TextUtils.isEmpty(iscaptain) && "no".equals(iscaptain)) {
				ivShareRoute.setVisibility(View.GONE);
			}
			if ("yes".equals(iscaptain) && "yes".equals(isshare)) {
				// if (locCount == 3) {
				// locCount = 0;
				// GetRouteRotateUtils
				// .setCaptainDirection(aMap, latLng, oldll);
				// }
				// locCount++;
				if (oldll != null) {
					polyline = aMap.addPolyline((new PolylineOptions())
							.add(oldll, latLng).color(Color.RED).width(20));
				} else {
					polyline = aMap.addPolyline((new PolylineOptions())
							.add(latLng, latLng).color(Color.RED).width(20));
				}
				if (plList != null && polyline != null) {
					plList.add(polyline);
				}
			}
			if (oldll != null) {
				distance = AMapUtils.calculateLineDistance(oldll, latLng);
			} else {
				distance = AMapUtils.calculateLineDistance(latLng, latLng);
			}
			oldll = latLng;

		} else {
			ivShareLat.setVisibility(View.GONE);
			ivShareRoute.setVisibility(View.GONE);
			ivExit.setVisibility(View.GONE);
			tvTitle.setText("蚂蚁聚聚");
			clearMarker();

		}
		if (!TextUtils.isEmpty(userid)) {
			shareMyPosMethod(userid, String.valueOf(latitude),
					String.valueOf(longitude), address,
					String.valueOf(distance));
		}
	}

	// 共享位置的方法
	private void shareMyPosMethod(final String userid, String lat, String lon,
			String location, String distance) {
		if (shareMyPosThread != null) {
			shareMyPosThread.interrupt();
			shareMyPosThread = null;
		}

		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("action", "Location"));
		list.add(new BasicNameValuePair("userid", userid));
		list.add(new BasicNameValuePair("lat", lat));
		list.add(new BasicNameValuePair("lon", lon));
		list.add(new BasicNameValuePair("location", location));
		list.add(new BasicNameValuePair("distance", distance));

		shareMyPosThread = new Thread(new ShareMyPosRunnable(this, list,
				new OnGetJsonDataListener() {

					@Override
					public void getJsonData(String json) {
						Log.e("MainActivity", "------>json=" + json);
						if (!TextUtils.isEmpty(json)
								&& !TextUtils.isEmpty(userid)
								&& (!TextUtils.isEmpty(isactive) || "no"
										.equals(isactive))) {
							ShareLatResult latResult = JsonTools.getLatResult(
									json, handler);
							String result = latResult.getResult();
							String message_num = null;
							infoList = new ArrayList<UserLatInfo>();
							if ("success".equals(result)) {
								message_num = latResult.getMessage_num();
								infoList = latResult.getList();
								if (infoList != null && infoList.size() > 0) {
									if ("yes".equals(iscaptain)) {
										String active_method = PreferenceUtil
												.readString(MainActivity.this,
														"login",
														"active_method");
										if ("iscaptain_no"
												.equals(active_method)) {
											iscaptain = "no";
											PreferenceUtil.write(
													MainActivity.this, "login",
													"iscaptain", iscaptain);
										}
									}
									pos.setPosMethod(infoList);
									handler.sendEmptyMessage(0);
								}
								handler.sendEmptyMessage(4);
							} else if ("fail".equals(result)) {
								message_num = latResult.getMessage_num();
								isactive = "no";
								iscaptain = "yes";
								PreferenceUtil.write(MainActivity.this,
										"login", "isactive", "no");
								PreferenceUtil.write(MainActivity.this,
										"login", "iscaptain", "yes");
								handler.sendEmptyMessage(4);
							}
							if (!TextUtils.isEmpty(message_num)) {
								msgCount = Integer.parseInt(message_num);
								handler.sendEmptyMessage(3);
							}
						}
					}

				}));
		shareMyPosThread.start();
		shareMyPosThread.setPriority(Thread.MAX_PRIORITY);
	}

	// 设置组员列表的方法
	private void setTeammatesList(List<UserLatInfo> infoList) {
		lv.addHeaderView(header);
		lv.setAdapter(teammatesListAdapter);
	}

	// 停止定位的方法
	@SuppressWarnings("deprecation")
	private void stopLocation() {
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
	}

	// 判断是否有网络
	private void judgeNetwork() {
		new Thread() {
			private int count = 0;
			private boolean flag1 = false;

			@Override
			public void run() {
				super.run();
				while (flag) {
					if (count != 0 && count < 120) {
						SystemClock.sleep(5000);
					} else if (count != 0 && count >= 120) {
						SystemClock.sleep(10000);
					}
					hasNet = BaseApplication.judgeNetwork(MainActivity.this);
					handler.sendEmptyMessage(1);

					if (!flag1
							&& BaseApplication.judgeNetwork(MainActivity.this)
							&& !NetWorkUtil.isWifiConnected(MainActivity.this)
							&& !locationManager
									.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
						handler.sendEmptyMessage(6);
						flag1 = true;
					}
				}
			}
		}.start();
	}

	// 自动登录的方法
	private void autoLogin() {
		HttpUtils httpUtils = new HttpUtils(20000)
				.configCurrentHttpCacheExpiry(10000);
		RequestParams params = new RequestParams("utf-8");
		params.addBodyParameter("action", "Login");
		params.addBodyParameter("userid", userid);
		httpUtils.send(HttpMethod.POST, UrlConfig.BASICURL, params,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (!TextUtils.isEmpty(responseInfo.result)) {
							try {
								JSONObject jsonObject = new JSONObject(
										responseInfo.result);
								String result = jsonObject.getString("result");
								if (!TextUtils.isEmpty(result)
										&& "0".equals(result)) {
									PreferenceUtil.write(MainActivity.this,
											"login", "hasLogined", true);
									isactive = jsonObject.getString("isactive");
									iscaptain = jsonObject
											.getString("iscaptain");
									PreferenceUtil.write(MainActivity.this,
											"login", "iscaptain", iscaptain);
									isview = jsonObject.getString("isview");
									isshare = jsonObject.getString("isshare");
									if ("yes".equals(isactive)) {
										String captainisshare = jsonObject
												.getString("capatinisshare");
										if ("yes".equals(captainisshare)) {
											List<LatBean> list = new ArrayList<LatBean>();
											JSONArray jsonArray = jsonObject
													.getJSONArray("captainpath");
											for (int i = 0; i < jsonArray
													.length(); i++) {
												JSONObject object = jsonArray
														.getJSONObject(i);
												LatBean latBean = new LatBean();
												latBean.setLat(Double.parseDouble(object
														.getString("lat")));
												latBean.setLon(Double.parseDouble(object
														.getString("lon")));
												list.add(latBean);
											}
											// 设置队长的轨迹
											setCaptainRoute(list);
										}
									}

									PreferenceUtil.write(MainActivity.this,
											"login", "isactive", isactive);
									PreferenceUtil.write(MainActivity.this,
											"login", "iscaptain", iscaptain);
									PreferenceUtil.write(MainActivity.this,
											"login", "isview", isview);
									PreferenceUtil.write(MainActivity.this,
											"login", "isshare", isshare);
								} else if (!TextUtils.isEmpty(result)
										&& "3".equals(result)) {
									BaseApplication.toastMethod(
											MainActivity.this, "登录出错,请重新登录", 1);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
							hasLogined = true;
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						BaseApplication.toastMethod(MainActivity.this, msg, 0);
					}
				});
	}

	// 设置队长的轨迹
	private void setCaptainRoute(final List<LatBean> list) {
		LatLng latlngOld = null;
		LatLng lat = null;
		for (int i = 0; i < list.size(); i++) {
			LatBean latBean = list.get(i);
			lat = new LatLng(latBean.getLat(), latBean.getLon());
			// if (latlngOld != null && showCapRoutCount == 3) {
			// showCapRoutCount = 0;
			// GetRouteRotateUtils.setCaptainDirection(aMap, lat, latlngOld);
			// }
			// showCapRoutCount++;
			if (latlngOld != null) {
				polyline = aMap.addPolyline((new PolylineOptions())
						.add(latlngOld, lat).color(Color.RED).width(20));
			} else {
				polyline = aMap.addPolyline((new PolylineOptions())
						.add(lat, lat).color(Color.RED).width(20));
			}
			latlngOld = lat;
			if (plList != null && polyline != null) {
				plList.add(polyline);
			}
		}
		PreferenceUtil.write(MainActivity.this, "oldll", "latitude",
				String.valueOf(lat.latitude));
		PreferenceUtil.write(MainActivity.this, "oldll", "longitude",
				String.valueOf(lat.longitude));
	}

	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		View infoWindow = LayoutInflater.from(this).inflate(
				R.layout.infowindow_layout, null);
		TextView tvNickname = (TextView) infoWindow
				.findViewById(R.id.tv_nickname);
		TextView tvLocation = (TextView) infoWindow
				.findViewById(R.id.tv_location);
		// tvNickname.setTextSize(TypedValue.COMPLEX_UNIT_SP,
		// SetScreenSizeUtils.getFontSize(this, 32));
		// tvLocation.setTextSize(TypedValue.COMPLEX_UNIT_SP,
		// SetScreenSizeUtils.getFontSize(this, 28));
		String title = marker.getTitle();
		String snippet = marker.getSnippet();

		if (!TextUtils.isEmpty(title)) {
			SpannableString titleText = new SpannableString(title);
			titleText.setSpan(
					new ForegroundColorSpan(getResources().getColor(
							R.color.invite_method_color)), 0,
					titleText.length(), 0);
			tvNickname.setText(titleText);
		} else {
			tvNickname.setText("");
		}
		if (!TextUtils.isEmpty(snippet)) {
			SpannableString snippetText = new SpannableString(snippet);
			snippetText.setSpan(new ForegroundColorSpan(getResources()
					.getColor(R.color.notice_font_color)), 0, snippetText
					.length(), 0);
			tvLocation.setText(snippetText);
		} else {
			tvLocation.setText("");
		}
		return infoWindow;
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		marker.showInfoWindow();
		isLoc = true;
		isLocation = true;
		return true;
	}

	@SuppressWarnings("static-access")
	private void setOtherPos() {
		if (setOtherPosThread != null) {
			setOtherPosThread.interrupt();
			setOtherPosThread = null;
		}
		if (setOtherPosThread == null) {
			setOtherPosThread = new Thread(new SetOtherPosRunnable(this,
					markerList, infoList, pos, aMap, isactive));
			if (setOtherPosThread != null) {
				setOtherPosThread.setDaemon(true);
				setOtherPosThread.setPriority(Thread.MAX_PRIORITY);
				setOtherPosThread.start();
				try {
					if (shareMyPosThread != null) {
						shareMyPosThread.sleep(1000);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * 
	 * @类名称: SetOtherPosRunnable
	 * @类描述: 设置他人信息的runnable
	 * @创建人：LiXinYang
	 * @创建时间：2015-2-27 上午1:37:16
	 * @备注：
	 * @version V1.0
	 */
	class SetOtherPosRunnable implements Runnable {
		private List<LocAndMarker> markerList;
		private AMap aMap;
		private ShareAndSetPos pos;
		private Activity context;
		private String isactive;

		public SetOtherPosRunnable(final Activity context,
				final List<LocAndMarker> markerList, List<UserLatInfo> locList,
				ShareAndSetPos pos, AMap aMap, String isactive) {
			this.aMap = aMap;
			this.markerList = markerList;
			this.pos = pos;
			this.context = context;
			this.isactive = isactive;
		}

		@Override
		public void run() {
			while (!isOver) {
				SystemClock.sleep(5000);
				if (aMap != null) {
					pos.getPosMethod(context, markerList, aMap, isactive,
							isLoc, locUserid, showMe, plList);
				}
			}
		}
	}

	// 清楚覆盖物的方法
	private void clearMarker() {
		if (markerList != null && markerList.size() > 0) {
			for (int i = 0; i < markerList.size(); i++) {
				if (!userid.equals(markerList.get(i).getUserid())) {
					markerList.get(i).getMarker().setVisible(false);
				}
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position > 0) {
			UserLatInfo info = infoList.get(position - 1);
			String lat = info.getLat();
			String lon = info.getLon();
			locUserid = info.getUserid();
			String isview2 = info.getIsview();
			if ("yes".equals(isview2) && !TextUtils.isEmpty(lat)
					&& !TextUtils.isEmpty(lon)) {
				mLatlng = new LatLng(Double.parseDouble(lat),
						Double.parseDouble(lon));
				showMe = false;
				aMap.moveCamera(CameraUpdateFactory.changeLatLng(mLatlng));
				isLoc = true;
				isLocation = true;
				for (int i = 0; i < markerList.size(); i++) {
					String userid2 = markerList.get(i).getUserid();
					String userid3 = info.getUserid();
					if (userid2.equals(userid3)) {
						MarkerJump.jumpPoint(aMap, markerList.get(i)
								.getMarker(), mLatlng);
						markerList.get(i).getMarker().showInfoWindow();
					}
				}
			} else if ("no".equals(isview2)) {
				BaseApplication.toastMethod(MainActivity.this, "您的小伙伴屏蔽了位置", 1);
			}
		}
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		if (marker.isInfoWindowShown()) {
			marker.hideInfoWindow();
		}
		isLocation = true;
	}

	@Override
	public void onMapClick(LatLng latlng) {
		if (isshow) {
			llBottomBar.setPadding(0, 0, 0, viewHeight);
			isshow = false;
			ivTeammates.setBackgroundResource(R.drawable.down);
			isDelete = false;
			teammatesListAdapter.deleteUser(isDelete, infoList);
		}
		hideInfoWindow();
		isLoc = true;
		isLocation = true;
	}

	// 隐藏信息窗口
	private void hideInfoWindow() {
		for (int i = 0; i < markerList.size(); i++) {
			Marker marker = markerList.get(i).getMarker();
			marker.hideInfoWindow();
		}
	}

	@Override
	public void onTouch(MotionEvent event) {
		isLoc = false;
		if (isLocation) {
			isLoc = true;
		}
	}

	// 初始化后台服务的方法
	private void initService() {
		Intent intent = new Intent(this, LocationService.class);
		intent.putExtra("isupload", false);
		intent.putExtra("userid", userid);
		intent.putExtra("isview", isview);
		intent.putExtra("count", 0);
		startService(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isOpen) {
				dl.close();
			} else {
				finish();
			}
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	/************************* 生命周期方法 ******************************/
	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
		isOver = false;
		init();
		initService();
		initHeader();
		judgeNetwork();
		isFirst = true;
		isLoc = true;
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		// mapView.onPause();
		isOver = true;
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (!TextUtils.isEmpty(userid) && "yes".equals(isactive)
				&& "yes".equals(isview)) {
			Intent intent = new Intent(this, LocationService.class);
			intent.putExtra("isupload", true);
			intent.putExtra("userid", userid);
			intent.putExtra("isview", isview);
			intent.putExtra("count", 0);
			startService(intent);
		}
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
		stopLocation();
		PreferenceUtil.write(this, "login", "hasLogined", false);
		PreferenceUtil.write(MainActivity.this, "login", "isactive", isactive);
		PreferenceUtil
				.write(MainActivity.this, "login", "iscaptain", iscaptain);
		PreferenceUtil.write(MainActivity.this, "login", "isview", isview);
		PreferenceUtil.write(MainActivity.this, "login", "isshare", isshare);
		isAddHeader = false;
	}

}
