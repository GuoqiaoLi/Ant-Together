package com.meizhiyun.mayi.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.AMap.OnMapScreenShotListener;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.MapView;
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
import com.meizhiyun.mayi.bean.LatBean;
import com.meizhiyun.mayi.bean.UrlConfig;
import com.meizhiyun.mayi.utils.PreferenceUtil;
import com.meizhiyun.mayi.utils.SDUtils;
import com.meizhiyun.mayi.utils.SetScreenSizeUtils;
import com.meizhiyun.mayi.utils.WxUtils;
import com.meizhiyun.mayi.view.HistoryShareWindow;
import com.tencent.mm.sdk.openapi.SendMessageToWX;

/**
 * 
 * @类名称: HistoryMapActivity
 * @类描述: 历史轨迹地图界面
 * @创建人：Guoqiao Li
 * @备注：
 * @version V1.0
 */

@ContentView(R.layout.history_map_activity)
public class HistoryMapActivity extends BaseActivity implements
		OnMapScreenShotListener {

	@ViewInject(R.id.iv_back)
	private ImageView ivBack;
	@ViewInject(R.id.tv_title)
	private TextView tvTitle;
	@ViewInject(R.id.rl_title_bar)
	private RelativeLayout rlTitleBar;
	@ViewInject(R.id.iv_exit)
	private ImageView ivExit;
	@ViewInject(R.id.map)
	private MapView mapView;
	@ViewInject(R.id.ll_loading)
	private LinearLayout llLoading;
	@ViewInject(R.id.iv_loading)
	private ImageView ivLoading;
	@ViewInject(R.id.tv_loading)
	private TextView tvLoading;
	private String groupid;
	private String userid;
	private AMap aMap;
	private HistoryShareWindow historyShareWindow;
	public static int scene;
	private LatLng oldll;
	public String Path = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化ViewUtils
		ViewUtils.inject(this);
		mapView.onCreate(savedInstanceState);// 必须要写
		// 初始化数据
		init();
		// 初始化地图
		initMap();
	}

	// 初始化地图
	private void initMap() {
		if (aMap == null) {
			aMap = mapView.getMap();
		}
		aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
		aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
	}

	// 初始化数据
	private void init() {
		WxUtils.registWxApi(this);
		Intent intent = getIntent();
		groupid = intent.getStringExtra("groupid");
		userid = PreferenceUtil.readString(this, "login", "userid");
		tvTitle.setText("历史轨迹");
		ivBack.setBackgroundResource(R.drawable.back_selector);
		ivExit.setBackgroundResource(R.drawable.sharing_selector);
		ivExit.setVisibility(View.VISIBLE);
		// 设置标题栏的高度
		LayoutParams params = rlTitleBar.getLayoutParams();
		int[] titleSizes = SetScreenSizeUtils.getTitleSize(this, 88);
		params.width = titleSizes[0];
		params.height = titleSizes[1];
		rlTitleBar.setLayoutParams(params);

		SetScreenSizeUtils.setViewSizeFromWidth(this, ivBack, 41, 66);
		ivLoading.setBackgroundResource(R.drawable.loading_animation);
		animationDrawable = (AnimationDrawable) ivLoading.getBackground();

		String dir = SDUtils.getSDCardPath() + File.separator + "MaYi"
				+ File.separator + "mapView";
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}
		Path = dir + File.separator + getCurrentTime() + ".png";
	}

	@Override
	public void onMapScreenShot(Bitmap bitmap) {
		tvLoading.setText("正在截图...");
		llLoading.setVisibility(View.VISIBLE);
		animationDrawable.start();
		if (null == bitmap) {
			return;
		}
		try {
			FileOutputStream fos = new FileOutputStream(Path);
			boolean b = bitmap.compress(CompressFormat.PNG, 100, fos);
			try {
				fos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (b) {
				tvLoading.setText("正在分享...");
				WxUtils.sendImageWx(null, Path, null, bitmap, scene);
			} else {
				animationDrawable.stop();
				llLoading.setVisibility(View.GONE);
				BaseApplication.toastMethod(HistoryMapActivity.this,
						"截屏失败,请重新分享", 1);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(new Date());
	}

	// 点击事件
	@OnClick({ R.id.iv_back, R.id.iv_exit })
	public void click(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.iv_exit:
			historyShareWindow = new HistoryShareWindow(this, onclick);
			historyShareWindow.showAtLocation(
					HistoryMapActivity.this.findViewById(R.id.share),
					Gravity.BOTTOM, 0, 0);
			break;

		default:
			break;
		}
	}

	// 弹窗点击事件
	private OnClickListener onclick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_wechat_share:
				scene = SendMessageToWX.Req.WXSceneSession;
				aMap.getMapScreenShot(HistoryMapActivity.this);
				historyShareWindow.dismiss();
				break;
			case R.id.iv_wechat_momment_share:
				scene = SendMessageToWX.Req.WXSceneTimeline;
				aMap.getMapScreenShot(HistoryMapActivity.this);
				historyShareWindow.dismiss();
				break;
			case R.id.tv_cancel:
				historyShareWindow.dismiss();
				break;

			default:
				break;
			}
		}
	};
	private List<LatBean> list;
	private AnimationDrawable animationDrawable;

	// 获取轨迹经纬度信息
	private void getLatInfo() {
		HttpUtils httpUtils = new HttpUtils(20000)
				.configCurrentHttpCacheExpiry(10000);
		RequestParams params = new RequestParams();
		params.addBodyParameter("action", "PathDetail");
		params.addBodyParameter("userid", userid);
		params.addBodyParameter("groupid", groupid);
		httpUtils.send(HttpMethod.POST, UrlConfig.BASICURL, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (!TextUtils.isEmpty(responseInfo.result)) {
							try {
								JSONObject jsonObject = new JSONObject(
										responseInfo.result);
								int result = jsonObject.getInt("result");
								if (result > 0) {
									JSONArray jsonArray = jsonObject
											.getJSONArray("data");
									list = new ArrayList<LatBean>();
									for (int i = 0; i < jsonArray.length(); i++) {
										JSONObject object = jsonArray
												.getJSONObject(i);
										String lat = object.getString("lat");
										String lon = object.getString("lon");

										LatBean latBean = new LatBean();
										latBean.setLat(Double.parseDouble(lat));
										latBean.setLon(Double.parseDouble(lon));
										list.add(latBean);
									}
									new GetThread().start();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						BaseApplication.toastMethod(HistoryMapActivity.this,
								"", 0);
					}
				});
	}

	class GetThread extends Thread {

		@Override
		public void run() {
			super.run();
			setRouteInfo();
		}
	}

	// 显示轨迹
	private void setRouteInfo() {
		if (list != null && list.size() > 0) {
			for (int j = 0; j < list.size(); j++) {
				LatBean latBean = list.get(j);
				LatLng latLng = new LatLng(latBean.getLat(), latBean.getLon());
				if (j == 0) {
					MarkerOptions options = new MarkerOptions();
					options.position(latLng).icon(
							BitmapDescriptorFactory
									.fromResource(R.drawable.start));
					aMap.addMarker(options);
					aMap.addPolyline((new PolylineOptions())
							.add(latLng, latLng).color(Color.RED).width(20));
					aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
					aMap.moveCamera(CameraUpdateFactory.zoomTo(aMap
							.getCameraPosition().zoom));
					oldll = latLng;
				} else if (oldll != null
						&& (oldll.latitude != latLng.latitude || oldll.longitude != latLng.longitude)) {

					if (j == list.size() - 1) {
						MarkerOptions options = new MarkerOptions();
						options.position(latLng).icon(
								BitmapDescriptorFactory
										.fromResource(R.drawable.over));
						aMap.addMarker(options);
						aMap.addPolyline((new PolylineOptions())
								.add(oldll, latLng).color(Color.RED).width(20));
						oldll = latLng;
					} else {
						aMap.addPolyline((new PolylineOptions())
								.add(oldll, latLng).color(Color.RED).width(20));
						oldll = latLng;
					}
				}
			}
		}
	}

	/*********************** 生命周期方法 **************************/
	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
		// 获取轨迹经纬度信息
		getLatInfo();

	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		animationDrawable.stop();
		llLoading.setVisibility(View.GONE);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

}
