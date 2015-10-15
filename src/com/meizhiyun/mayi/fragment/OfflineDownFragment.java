package com.meizhiyun.mayi.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.meizhiyun.mayi.R;
import com.meizhiyun.mayi.activity.OfflineCityListActivity;
import com.meizhiyun.mayi.adapter.DownListAdapter;

@SuppressLint("ValidFragment")
public class OfflineDownFragment extends Fragment {
	@ViewInject(R.id.slv)
	private SwipeMenuListView slv;

	private View view;
	private static OfflineMapManager amapManager;

	private List<OfflineMapCity> offListAll = new ArrayList<OfflineMapCity>();
	private List<OfflineMapCity> offlineMapCities = null; //保存二级目录的市
	private List<OfflineMapProvince> offlineMapProvince = null;// 保存一级目录的省直辖市
	private List<OfflineMapCity> offlineDownloading = null; //保存已下载的市
	private List<OfflineMapProvince> downedProvinces = null;//保存已下载的省直辖市
	private DownListAdapter adapter;
	private static OfflineDownFragment fragment;
	public static OfflineDownFragment getInstance(){
		if (fragment == null) {
			fragment = new OfflineDownFragment();
		}
		return fragment;
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.offline_download_list_fragment, null);
		ViewUtils.inject(this, view);
		return view;

	}

	@Override
	public void onResume() {
		super.onResume();
		amapManager = ((OfflineCityListActivity) getActivity()).getAmapOffline();
		init();
		adapter = new DownListAdapter(getActivity(), offListAll);
		slv.setAdapter(adapter);
		slv.setMenuCreator(creator);
		slv.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				String city = offListAll.get(position).getCity();
				amapManager.remove(city);
				offListAll.remove(position);
				adapter.notifyDataSetChanged();
				return false;
			}
		});
	}
	
	public void init() {

		offlineDownloading = amapManager.getDownloadingCityList();
		offlineMapCities = amapManager.getDownloadOfflineMapCityList();

		downedProvinces = amapManager.getDownloadOfflineMapProvinceList();
		offlineMapProvince = amapManager.getDownloadingProvinceList();
		for (OfflineMapProvince offlineProvince : downedProvinces) {
			offlineMapCities.add(getCicy(offlineProvince));
		}
		for (OfflineMapProvince offlineProvince : offlineMapProvince) {
			offlineDownloading.add(getCicy(offlineProvince));
		}

		offListAll.clear();
		offListAll.addAll(offlineDownloading);
		offListAll.addAll(offlineMapCities);

	}
	
	public void doRefresh(){
		adapter.notifyDataSetChanged();
	}

	public OfflineMapCity getCicy(OfflineMapProvince aMapProvince) {
		OfflineMapCity aMapCity = new OfflineMapCity();
		aMapCity.setCity(aMapProvince.getProvinceName());
		aMapCity.setSize(aMapProvince.getSize());
		aMapCity.setCompleteCode(aMapProvince.getcompleteCode());
		aMapCity.setState(aMapProvince.getState());
		aMapCity.setUrl(aMapProvince.getUrl());
		return aMapCity;
	}
	
	private SwipeMenuCreator creator = new SwipeMenuCreator() {

		@Override
		public void create(SwipeMenu menu) {
			SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
			deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F,
					0x25)));
			deleteItem.setWidth(dp2px(66));
			deleteItem.setIcon(R.drawable.ic_delete);
			menu.addMenuItem(deleteItem);
		}
	};

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}


}
