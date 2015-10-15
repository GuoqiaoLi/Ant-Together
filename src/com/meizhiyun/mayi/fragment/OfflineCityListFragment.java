package com.meizhiyun.mayi.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMapException;
import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapManager.OfflineMapDownloadListener;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
import com.amap.api.maps.offlinemap.OfflineMapStatus;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.meizhiyun.mayi.BaseApplication;
import com.meizhiyun.mayi.R;
import com.meizhiyun.mayi.activity.OfflineCityListActivity;
import com.meizhiyun.mayi.utils.SetScreenSizeUtils;
import com.meizhiyun.mayi.view.MyView;

/**
 * 
 * @类名称: OfflineCityListFragment
 * @类描述: 高德地图离线地图城市列表的fragment
 * @创建人：LiXinYang
 * @创建时间：2015-2-17 下午8:04:42
 * @备注：
 * @version V1.0
 */
public class OfflineCityListFragment extends Fragment implements
		OfflineMapDownloadListener {
	private View view;
	private OfflineMapManager amapManager = null;// 离线地图下载控制器
	private List<OfflineMapProvince> provinceList = new ArrayList<OfflineMapProvince>();// 保存一级目录的省直辖市
	private HashMap<Object, List<OfflineMapCity>> cityMap = new HashMap<Object, List<OfflineMapCity>>();// 保存二级目录的市
	private int groupPosition;// 记录一级目录的position
	private int childPosition;// 记录二级目录的position
	private int completeCode;// 记录下载比例
	private boolean isStart = false;// 判断是否开始下载,true表示开始下载，false表示下载失败
	private boolean[] isOpen;// 记录一级目录是否打开
	private static OfflineCityListFragment fragment;

	public static OfflineCityListFragment getInstance() {
		if (fragment == null) {
			fragment = new OfflineCityListFragment();
		}
		return fragment;
	}

	@ViewInject(R.id.ex_lv)
	private ExpandableListView exLv;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.offline_map_list_fragment, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		// 初始化信息
		init();
	}

	/**
	 * 初始化UI布局文件
	 */
	private void init() {
		// 此版本限制，使用离线地图，请初始化一个MapView
		amapManager = new OfflineMapManager(getActivity(), this);
		((OfflineCityListActivity) getActivity()).setAmapOffline(amapManager);
		exLv.setGroupIndicator(null);
		provinceList = amapManager.getOfflineMapProvinceList();
		List<OfflineMapProvince> bigCityList = new ArrayList<OfflineMapProvince>();// 以省格式保存直辖市、港澳、全国概要图
		List<OfflineMapCity> cityList = new ArrayList<OfflineMapCity>();// 以市格式保存直辖市、港澳、全国概要图
		List<OfflineMapCity> gangaoList = new ArrayList<OfflineMapCity>();// 保存港澳城市
		for (int i = 0; i < provinceList.size(); i++) {
			OfflineMapProvince offlineMapProvince = provinceList.get(i);
			List<OfflineMapCity> city = new ArrayList<OfflineMapCity>();
			OfflineMapCity aMapCity = getCicy(offlineMapProvince);
			if (offlineMapProvince.getCityList().size() != 1) {
				city.add(aMapCity);
				city.addAll(offlineMapProvince.getCityList());
			} else {
				cityList.add(aMapCity);
				bigCityList.add(offlineMapProvince);
			}
			cityMap.put(i, city);
		}

		OfflineMapProvince title = new OfflineMapProvince();
		title.setProvinceName("直辖市");
		provinceList.add(provinceList.size(), title);
		title = new OfflineMapProvince();
		title.setProvinceName("港澳");
		provinceList.add(provinceList.size(), title);
		provinceList.removeAll(bigCityList);

		for (OfflineMapProvince aMapProvince : bigCityList) {
			if (aMapProvince.getProvinceName().contains("香港")
					|| aMapProvince.getProvinceName().contains("澳门")) {
				gangaoList.add(getCicy(aMapProvince));
			}
		}
		cityList.remove(4);// 从List集合体中删除香港
		cityList.remove(4);// 从List集合体中删除澳门
		cityList.remove(4);// 从List集合体中删除全国概要图
		cityMap.put(provinceList.size() - 2, cityList);// 在HashMap中第1位置添加直辖市
		cityMap.put(provinceList.size() - 1, gangaoList);// 在HashMap中第2位置添加港澳
		isOpen = new boolean[provinceList.size()];
		// View header = getOfflineHeader();
		// exLv.addHeaderView(header);
		// 为列表绑定数据源
		exLv.setAdapter(adapter);
		exLv.setOnGroupCollapseListener(new OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
				isOpen[groupPosition] = false;
			}
		});

		exLv.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
				isOpen[groupPosition] = true;
			}
		});
		// 设置二级item点击的监听器
		exLv.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPos, int childPos, long id) {
				try {
					// 下载全国概要图、直辖市、港澳离线地图数据
					if (groupPos == provinceList.size() - 1
							|| groupPos == provinceList.size() - 2) {
						isStart = amapManager.downloadByProvinceName(cityMap
								.get(groupPos).get(childPos).getCity());
					}
					// 下载各省的离线地图数据
					else {
						// 下载各省列表中的省份离线地图数据
						if (childPos == 0) {
							isStart = amapManager
									.downloadByProvinceName(provinceList.get(
											groupPos).getProvinceName());
						}
						// 下载各省列表中的城市离线地图数据
						else if (childPos > 0) {
							isStart = amapManager.downloadByCityName(cityMap
									.get(groupPos).get(childPos).getCity());
						}
					}
				} catch (AMapException e) {
					e.printStackTrace();
					Log.e("离线地图下载", "离线地图下载抛出异常" + e.getErrorMessage());
				}
				// 保存当前正在正在下载省份或者城市的position位置
				if (isStart) {
					groupPosition = groupPos;
					childPosition = childPos;
				}
				return false;
			}
		});

	}

	final ExpandableListAdapter adapter = new BaseExpandableListAdapter() {

		@Override
		public int getGroupCount() {
			return provinceList.size();
		}

		/**
		 * 获取一级标签内容
		 */
		@Override
		public Object getGroup(int groupPosition) {
			return provinceList.get(groupPosition).getProvinceName();
		}

		/**
		 * 获取一级标签的ID
		 */
		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		/**
		 * 获取一级标签下二级标签的总数
		 */
		@Override
		public int getChildrenCount(int groupPosition) {
			return cityMap.get(groupPosition).size();
		}

		/**
		 * 获取一级标签下二级标签的内容
		 */
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return cityMap.get(groupPosition).get(childPosition).getCity();
		}

		/**
		 * 获取二级标签的ID
		 */
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		/**
		 * 指定位置相应的组视图
		 */
		@Override
		public boolean hasStableIds() {
			return true;
		}

		/**
		 * 对一级标签进行设置
		 */
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView group_text;
			ImageView group_image;
			if (convertView == null) {
				convertView = (RelativeLayout) RelativeLayout.inflate(
						getActivity(), R.layout.offline_map_list_group, null);
			}
			group_text = (TextView) convertView.findViewById(R.id.tv_group);
			group_image = (ImageView) convertView.findViewById(R.id.iv_group);
			group_text.setText(provinceList.get(groupPosition)
					.getProvinceName());
			if (isOpen[groupPosition]) {
				group_image.setImageResource(R.drawable.look_up);
			} else {
				group_image.setImageResource(R.drawable.look_down);
			}

			SetScreenSizeUtils.setViewSizeFromWidth(getActivity(), group_image,
					26, 16);
			// int fontSize = SetScreenSizeUtils.getFontSize(getActivity(), 32);
			// group_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

			return convertView;
		}

		/**
		 * 对一级标签下的二级标签进行设置
		 */
		@Override
		public View getChildView(final int groupPosition,
				final int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			boolean isWaiting = false;
			if (convertView == null) {
				convertView = (RelativeLayout) RelativeLayout.inflate(
						getActivity(), R.layout.offline_map_list_child, null);
			}
			ViewHolder holder = new ViewHolder(convertView);
			holder.cityName.setText(cityMap.get(groupPosition)
					.get(childPosition).getCity());
			holder.citySize.setText(String.valueOf(
					(cityMap.get(groupPosition).get(childPosition).getSize())
							/ (1024 * 1024f)).substring(0, 5)
					+ "MB");

			if (amapManager.getDownloadOfflineMapCityList() != null) {
				if (amapManager.getDownloadOfflineMapCityList().contains(
						cityMap.get(groupPosition).get(childPosition))) {
					// 下载完成时显示方式
					holder.cityDown.setText("安装完成");
					holder.cityDown.setVisibility(View.VISIBLE);
					holder.cityDown.setmCurrent(MyView.MUPDATE);
					isWaiting = true;
				}

			}
			if ((amapManager.getDownloadingCityList()) != null) {
				if (amapManager.getDownloadingCityList().contains(
						cityMap.get(groupPosition).get(childPosition))) {
					// 正在或等待下载列表时item的显示方式
					holder.cityDown.setText("正在下载" + completeCode + "%");
					isWaiting = true;
				}
			}
			if (!isWaiting) {
				if (cityMap.get(groupPosition).get(childPosition).getState() == OfflineMapStatus.SUCCESS) {
					holder.cityDown.setText("安装完成");
					holder.cityDown.setVisibility(View.VISIBLE);
					holder.cityDown.setmCurrent(MyView.MUPDATE);
				} else if (cityMap.get(groupPosition).get(childPosition)
						.getState() == -2) {
					holder.cityDown.setText("正在下载" + completeCode + "%");
					holder.cityDown.setmCurrent(MyView.MPAUSE);
				} else if (cityMap.get(groupPosition).get(childPosition)
						.getState() == OfflineMapStatus.UNZIP) {
					holder.cityDown.setmCurrent(MyView.MGONE);
					holder.cityDown.setText("正在解压");

				} else if (cityMap.get(groupPosition).get(childPosition)
						.getState() == OfflineMapStatus.PAUSE) {
					holder.cityDown.setText("继续下载");
					holder.cityDown.setmCurrent(MyView.MCONTIUNE);
				} else if (cityMap.get(groupPosition).get(childPosition)
						.getState() == OfflineMapStatus.ERROR) {
					holder.cityDown.setText("下载失败");
				} else {
					holder.cityDown.setText("下载");
					holder.cityDown.setmCurrent(MyView.DEFAULT);
				}
			}
			// 对自定义button设置监听
			holder.cityDown.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					MyView tv = (MyView) v;
					switch (tv.getmCurrent()) {
					case MyView.DEFAULT:
						doDownLoadMap(groupPosition, childPosition);
						tv.setmCurrent(MyView.MPAUSE);
						break;
					case MyView.MUPDATE:
						doUpDateMap(groupPosition, childPosition);
						tv.setmCurrent(MyView.MPAUSE);
						break;
					case MyView.MPAUSE:
						doPauseMap();
						tv.setmCurrent(MyView.MCONTIUNE);
						break;
					case MyView.MCONTIUNE:
						doRestartMap();
						tv.setmCurrent(MyView.MPAUSE);
						break;

					default:
						break;
					}
				}

			});

			return convertView;
		}

		class ViewHolder {
			TextView cityName;
			TextView citySize;
			MyView cityDown;

			public ViewHolder(View view) {
				cityName = (TextView) view.findViewById(R.id.tv_city);
				citySize = (TextView) view.findViewById(R.id.tv_city_size);
				cityDown = (MyView) view.findViewById(R.id.tv_download);
				// int fontSize = SetScreenSizeUtils
				// .getFontSize(getActivity(), 32);
				// cityName.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
				// citySize.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
				// cityDown.setTextSize(TypedValue.COMPLEX_UNIT_SP,
				// SetScreenSizeUtils.getFontSize(getActivity(), 28));
			}
		}

		/**
		 * 当选择子节点的时候，调用该方法
		 */
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	};

	public void doDownLoadMap(int groupPosition, int childPosition) {
		try {
			// 下载全国概要图、直辖市、港澳离线地图数据
			if (groupPosition == provinceList.size() - 1
					|| groupPosition == provinceList.size() - 2) {
				isStart = amapManager.downloadByProvinceName(cityMap
						.get(groupPosition).get(childPosition).getCity());
				((OfflineCityListActivity) getActivity()).refresh();
			} // 下载各省的离线地图数据
			else { // 下载各省列表中的省份离线地图数据
				if (childPosition == 0) {
					isStart = amapManager.downloadByProvinceName(provinceList
							.get(groupPosition).getProvinceName());
					((OfflineCityListActivity) getActivity()).refresh();
				} // 下载各省列表中的城市离线地图数据
				else if (childPosition > 0) {
					isStart = amapManager.downloadByCityName(cityMap
							.get(groupPosition).get(childPosition).getCity());
					((OfflineCityListActivity) getActivity()).refresh();
				}
			}
		} catch (AMapException e) {
			e.printStackTrace();
			Log.e("离线地图下载", "离线地图下载抛出异常" + e.getErrorMessage());
		} // 保存当前正在正在下载省份或者城市的position位置
		if (isStart) {
			OfflineCityListFragment.this.groupPosition = groupPosition;
			OfflineCityListFragment.this.childPosition = childPosition;
		}

	}

	public void doPauseMap() {
		amapManager.pause();
	}

	public void doRestartMap() {
		amapManager.restart();
	}

	public void doUpDateMap(int groupPosition, int childPosition) {

		final String cityName = cityMap.get(groupPosition).get(childPosition)
				.getCity();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					boolean update = amapManager
							.updateOfflineCityByName(cityName);
					if (update) {
						if (getActivity() != null) {
							BaseApplication.toastMethod(getActivity(),
									"有更新数据包正在下载", 1);
						}
						amapManager.downloadByCityName(cityName);

					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	public void doUpDateMap1(final String cityName) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					boolean update = amapManager
							.updateOfflineCityByName(cityName);
					if (update) {
						if (getActivity() != null) {
							BaseApplication.toastMethod(getActivity(),
									"有更新数据包正在下载", 1);
						}
						amapManager.downloadByCityName(cityName);

					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	/**
	 * 把一个省的对象转化为一个市的对象
	 */
	public OfflineMapCity getCicy(OfflineMapProvince aMapProvince) {
		OfflineMapCity aMapCity = new OfflineMapCity();
		aMapCity.setCity(aMapProvince.getProvinceName());
		aMapCity.setSize(aMapProvince.getSize());
		aMapCity.setCompleteCode(aMapProvince.getcompleteCode());
		aMapCity.setState(aMapProvince.getState());
		aMapCity.setUrl(aMapProvince.getUrl());
		return aMapCity;
	}

	@Override
	public void onDownload(int status, int completeCode, String downName) {
		switch (status) {
		case OfflineMapStatus.SUCCESS:
			changeOfflineMapTitle(OfflineMapStatus.SUCCESS);
			BaseApplication.toastMethod(getActivity(), "下载成功", 1);
			break;
		case OfflineMapStatus.LOADING:
			OfflineCityListFragment.this.completeCode = completeCode;
			changeOfflineMapTitle(-2);// -2表示正在下载离线地图数据
			break;

		case OfflineMapStatus.UNZIP:
			changeOfflineMapTitle(OfflineMapStatus.UNZIP);
			break;
		case OfflineMapStatus.WAITING:
			changeOfflineMapTitle(OfflineMapStatus.WAITING);
			break;
		case OfflineMapStatus.PAUSE:
			doPauseMap();
			changeOfflineMapTitle(OfflineMapStatus.PAUSE);
			break;
		case OfflineMapStatus.STOP:
			changeOfflineMapTitle(OfflineMapStatus.STOP);
			break;
		case OfflineMapStatus.ERROR:
			changeOfflineMapTitle(OfflineMapStatus.ERROR);
			break;
		default:
			break;
		}
		((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
	}

	/**
	 * 更改离线地图下载状态文字
	 */
	private void changeOfflineMapTitle(int status) {
		if (groupPosition == provinceList.size() - 1
				|| groupPosition == provinceList.size() - 2) {
			cityMap.get(groupPosition).get(childPosition).setState(status);// -2表示正在下载离线地图数据
		} else {
			if (childPosition == 0) {
				for (int i = 0; i < cityMap.get(groupPosition).size(); i++) {
					cityMap.get(groupPosition).get(i).setState(status);// -2表示正在下载离线地图数据
				}
			} else {
				cityMap.get(groupPosition).get(childPosition).setState(status);// -2表示正在下载离线地图数据
			}
		}
	}

	// 获取离线地图的headerview
	// public View getOfflineHeader() {
	// View headerView = LayoutInflater.from(getActivity()).inflate(
	// R.layout.offline_map_list_header, null);
	// tvCurrentCity = (TextView) headerView
	// .findViewById(R.id.tv_current_city);
	// tvCurrentCitySize = (TextView) headerView
	// .findViewById(R.id.tv_current_city_size);
	// MyView tvDown = (MyView) headerView.findViewById(R.id.tv_down);
	// TextView tvAllCities = (TextView) headerView
	// .findViewById(R.id.tv_all_cities);
	// TextView tvCurrent = (TextView) headerView
	// .findViewById(R.id.tv_current);
	//
	// SetScreenSizeUtils.setViewSizeFromWidth(getActivity(), tvCurrent, 640,
	// 85);
	// SetScreenSizeUtils.setViewSizeFromWidth(getActivity(), tvAllCities,
	// 640, 85);
	// // int fontSize = SetScreenSizeUtils.getFontSize(getActivity(), 32);
	// // tvCurrentCity.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
	// // tvCurrentCitySize.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
	// // tvDown.setTextSize(TypedValue.COMPLEX_UNIT_SP,
	// // SetScreenSizeUtils.getFontSize(getActivity(), 28));
	// final String city = PreferenceUtil.readString(getActivity(),
	// "CurrentCity", "city");
	// String cityCode = PreferenceUtil.readString(getActivity(),
	// "CurrentCity", "cityCode");
	// OfflineMapCity offlineMapCity = null;
	// if (!TextUtils.isEmpty(city)) {
	// tvCurrentCity.setText(city);
	// offlineMapCity = amapManager.getItemByCityCode(cityCode);
	// tvCurrentCitySize.setText(String.valueOf(
	// (offlineMapCity.getSize()) / (1024 * 1024f))
	// .substring(0, 5)
	// + "MB");
	// }
	// boolean isWaiting = false;
	// if (offlineMapCity != null) {
	// if (amapManager.getDownloadOfflineMapCityList() != null) {
	// if (amapManager.getDownloadOfflineMapCityList().contains(
	// offlineMapCity)) {
	// // 下载完成时显示方式
	// tvDown.setText("安装完成");
	// tvDown.setVisibility(View.VISIBLE);
	// tvDown.setmCurrent(MyView.MUPDATE);
	// isWaiting = true;
	// }
	//
	// }
	// if ((amapManager.getDownloadingCityList()) != null) {
	// if (amapManager.getDownloadingCityList().contains(
	// offlineMapCity)) {
	// // 正在或等待下载列表时item的显示方式
	// tvDown.setText("正在下载" + completeCode + "%");
	// isWaiting = true;
	// }
	// }
	// if (!isWaiting) {
	// if (offlineMapCity.getState() == OfflineMapStatus.SUCCESS) {
	// tvDown.setText("安装完成");
	// tvDown.setVisibility(View.VISIBLE);
	// tvDown.setmCurrent(MyView.MUPDATE);
	// } else if (offlineMapCity.getState() == -2) {
	// tvDown.setText("正在下载" + completeCode + "%");
	// tvDown.setmCurrent(MyView.MPAUSE);
	// } else if (offlineMapCity.getState() == OfflineMapStatus.UNZIP) {
	// tvDown.setmCurrent(MyView.MGONE);
	// tvDown.setText("正在解压");
	//
	// } else if (offlineMapCity.getState() == OfflineMapStatus.PAUSE) {
	// tvDown.setText("继续下载");
	// tvDown.setmCurrent(MyView.MCONTIUNE);
	// } else if (offlineMapCity.getState() == OfflineMapStatus.ERROR) {
	// tvDown.setText("下载失败");
	// } else {
	// tvDown.setText("下载");
	// tvDown.setmCurrent(MyView.DEFAULT);
	// }
	// }
	// // 对自定义button设置监听
	// tvDown.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// MyView tv = (MyView) v;
	// switch (tv.getmCurrent()) {
	// case MyView.DEFAULT:
	// // doDownLoadMap(groupPosition, childPosition);
	// try {
	// amapManager.downloadByCityName(city);
	// } catch (AMapException e) {
	// e.printStackTrace();
	// }
	// tv.setmCurrent(MyView.MPAUSE);
	// break;
	// case MyView.MUPDATE:
	// doUpDateMap1(city);
	// tv.setmCurrent(MyView.MPAUSE);
	// break;
	// case MyView.MPAUSE:
	// doPauseMap();
	// tv.setmCurrent(MyView.MCONTIUNE);
	// break;
	// case MyView.MCONTIUNE:
	// doRestartMap();
	// tv.setmCurrent(MyView.MPAUSE);
	// break;
	//
	// default:
	// break;
	// }
	// }
	//
	// });
	// }
	//
	// return headerView;
	//
	// }

	// @OnClick({ R.id.iv_sousuo })
	// public void click(View v) {
	// switch (v.getId()) {
	// case R.id.iv_sousuo:
	//
	// break;
	//
	// default:
	// break;
	// }
	// }

}
