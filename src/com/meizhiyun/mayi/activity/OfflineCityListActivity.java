package com.meizhiyun.mayi.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.meizhiyun.mayi.R;
import com.meizhiyun.mayi.fragment.OfflineCityListFragment;
import com.meizhiyun.mayi.fragment.OfflineDownFragment;
import com.meizhiyun.mayi.utils.SetScreenSizeUtils;

/**
 * 
 * @类名称: OfflineCityListActivity
 * @类描述: 高德地图离线地图城市列表的activity
 * @创建人：LiXinYang
 * @备注：
 * @version V1.0
 */

@ContentView(R.layout.offline_map_activity)
public class OfflineCityListActivity extends BaseActivity {
	private FragmentManager manager;
	@ViewInject(R.id.ll_city_list)
	private LinearLayout llCityList;
	@ViewInject(R.id.ll_has_download)
	private LinearLayout llHasDownload;
	@ViewInject(R.id.tv_city_list)
	private TextView tvCityList;
	@ViewInject(R.id.tv_has_download)
	private TextView tvHasDownload;
	@ViewInject(R.id.rl_title)
	private RelativeLayout rlTitle;
	@ViewInject(R.id.iv_back)
	private ImageView ivBack;

	private OfflineMapManager amapOffline;
	private boolean isCityList = true;
	private List<Fragment> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		// 初始化信息
		init();
		// 初始化布局
		initLayout();
	}

	private void init() {
		manager = getFragmentManager();
		OfflineCityListFragment offlineCityListFragment = OfflineCityListFragment
				.getInstance();
		OfflineDownFragment offlineDownFragment = OfflineDownFragment
				.getInstance();
		list = new ArrayList<Fragment>();
		list.add(offlineCityListFragment);
		list.add(offlineDownFragment);
		manager.beginTransaction().add(R.id.ll_fragment, list.get(0))
				.add(R.id.ll_fragment, list.get(1)).commit();
	}

	// 初始化布局
	private void initLayout() {
		// 设置标题栏的高度
		LayoutParams params = rlTitle.getLayoutParams();
		int[] titleSizes = SetScreenSizeUtils.getTitleSize(this, 88);
		params.width = titleSizes[0];
		params.height = titleSizes[1];
		rlTitle.setLayoutParams(params);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivBack, 41, 66);

		SetScreenSizeUtils.setViewSizeFromWidth(this, llCityList, 162, 58);
		SetScreenSizeUtils.setViewSizeFromWidth(this, llHasDownload, 162, 58);
	}

	public void refresh() {

	}

	public OfflineMapManager getAmapOffline() {
		return amapOffline;
	}

	public void setAmapOffline(OfflineMapManager amapOffline) {
		this.amapOffline = amapOffline;
	}

	// 点击事件
	@OnClick({ R.id.iv_back, R.id.ll_city_list, R.id.ll_has_download })
	public void click(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.ll_city_list:
			if (!isCityList) {
				llCityList.setBackgroundResource(R.drawable.caozuokuang);
				tvCityList.setTextColor(getResources().getColor(
						R.color.title_bar));
				llHasDownload.setBackgroundResource(R.drawable.caozuokuang2);
				tvHasDownload.setTextColor(Color.WHITE);
				manager.beginTransaction().show(list.get(0)).hide(list.get(1)).commit();
				isCityList = true;
				llHasDownload.setClickable(true);
				llCityList.setClickable(false);
			}
			break;
		case R.id.ll_has_download:
			if (isCityList) {
				llCityList.setBackgroundResource(R.drawable.caozuokuang4);
				tvCityList.setTextColor(Color.WHITE);
				llHasDownload.setBackgroundResource(R.drawable.caozuokuang3);
				tvHasDownload.setTextColor(getResources().getColor(
						R.color.title_bar));
				manager.beginTransaction().show(list.get(1)).hide(list.get(0)).commit();
				isCityList = false;
				llHasDownload.setClickable(false);
				llCityList.setClickable(true);
			}
			break;

		default:
			break;
		}
	}

}
