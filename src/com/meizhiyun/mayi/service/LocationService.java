package com.meizhiyun.mayi.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.meizhiyun.mayi.BaseApplication;
import com.meizhiyun.mayi.listener.OnGetJsonDataListener;
import com.meizhiyun.mayi.netstate.NetWorkUtil;
import com.meizhiyun.mayi.thread.ShareMyPosRunnable;

/**
 * 
 * @类名称: LocationService
 * @类描述: 实现后台定位的服务
 * @创建人：Guoqiao
 * @备注：
 * @version V1.0
 */
public class LocationService extends Service implements AMapLocationListener {

	private LocationManagerProxy mAMapLocManager = null;
	private String userid;
	private LatLng latLng;
	private LatLng oldll;
	private float distance;
	private int count;
	private String isview;
	private boolean isupload;
	private Thread shareMyPosThread;
	private LocationManager locationManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mAMapLocManager = LocationManagerProxy.getInstance(this);
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			userid = intent.getStringExtra("userid");
			isview = intent.getStringExtra("isview");
			count = intent.getIntExtra("count", 0);
			isupload = intent.getBooleanExtra("isupload", false);
			if (mAMapLocManager != null) {
				mAMapLocManager.requestLocationData(
						LocationProviderProxy.AMapNetwork, 5000, 2, this);
			}
		}
		return START_STICKY;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mAMapLocManager != null) {
			mAMapLocManager.removeUpdates(this);
			mAMapLocManager.destory();
		}
		mAMapLocManager = null;
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
	public void onLocationChanged(AMapLocation location) {
		if ("yes".equals(isview) && isupload) {
			if (location != null && count < 120) {
				if (BaseApplication.judgeNetwork(this)
						&& !NetWorkUtil.isWifiConnected(this)
						&& locationManager
								.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					mAMapLocManager.requestLocationData(
							LocationManagerProxy.GPS_PROVIDER, 5000, 10, this);
				} else if (BaseApplication.judgeNetwork(this)
						&& NetWorkUtil.isWifiConnected(this)) {
					mAMapLocManager.requestLocationData(
							LocationProviderProxy.AMapNetwork, 5000, 10, this);
				}
				double geoLat = location.getLatitude();
				double geoLng = location.getLongitude();
				String address = location.getAddress();
				latLng = new LatLng(geoLat, geoLng);
				if (oldll != null) {
					distance = AMapUtils.calculateLineDistance(oldll, latLng);
				} else {
					distance = 0;
				}
				oldll = latLng;
				if (shareMyPosThread != null) {
					shareMyPosThread.interrupt();
					shareMyPosThread = null;
				}

				List<NameValuePair> list = new ArrayList<NameValuePair>();
				list.add(new BasicNameValuePair("action", "Location"));
				list.add(new BasicNameValuePair("userid", userid));
				list.add(new BasicNameValuePair("lat", String.valueOf(geoLat)));
				list.add(new BasicNameValuePair("lon", String.valueOf(geoLng)));
				list.add(new BasicNameValuePair("location", address));
				list.add(new BasicNameValuePair("distance", String
						.valueOf(distance)));

				shareMyPosThread = new Thread(new ShareMyPosRunnable(this,
						list, new OnGetJsonDataListener() {

							@Override
							public void getJsonData(String json) {

							}
						}

				));
				shareMyPosThread.start();
				shareMyPosThread.setPriority(Thread.MAX_PRIORITY);
				count++;
			} else {
				stopSelf();
			}
		}
	}

}
