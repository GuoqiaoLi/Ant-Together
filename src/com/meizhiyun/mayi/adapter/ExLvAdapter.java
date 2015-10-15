package com.meizhiyun.mayi.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
import com.amap.api.maps.offlinemap.OfflineMapStatus;
import com.meizhiyun.mayi.R;

public class ExLvAdapter extends BaseExpandableListAdapter {

	private Context context;
	private List<OfflineMapProvince> provinceList;
	private HashMap<Object, List<OfflineMapCity>> cityMap;
	private boolean[] isOpen;
	private int groupPos;
	private int childPos;
	private int completeCode;// 记录下载比例

	public ExLvAdapter(Context context, List<OfflineMapProvince> provinceList,
			HashMap<Object, List<OfflineMapCity>> cityMap, boolean[] isOpen,
			int groupPos, int childPos, int completeCode) {
		this.context = context;
		this.provinceList = provinceList;
		this.cityMap = cityMap;
		this.isOpen = isOpen;
		this.groupPos = groupPos;
		this.childPos = childPos;
		this.completeCode = completeCode;
		notifyDataSetChanged();
	}

	@Override
	public int getGroupCount() {
		if (provinceList != null && provinceList.size() > 0) {
			return provinceList.size();
		}
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {

		return provinceList.get(groupPosition).getProvinceName();
	}

	@Override
	public long getGroupId(int groupPosition) {

		return groupPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (cityMap != null) {
			return cityMap.get(groupPosition).size();
		}
		return 0;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		if (cityMap != null) {
			return cityMap.get(groupPosition).get(childPosition).getCity();
		}
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {

		return childPosition;
	}

	@Override
	public boolean hasStableIds() {

		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		TextView group_text;
		ImageView group_image;
		if (convertView == null) {
			convertView = (RelativeLayout) RelativeLayout.inflate(context,
					R.layout.offline_map_list_group, null);
		}
		group_text = (TextView) convertView.findViewById(R.id.tv_group);
		group_image = (ImageView) convertView.findViewById(R.id.iv_group);
		group_text.setText(provinceList.get(groupPosition).getProvinceName());
		if (isOpen[groupPosition]) {
			group_image.setImageDrawable(context.getResources().getDrawable(
					R.drawable.look_up));
		} else {
			group_image.setImageDrawable(context.getResources().getDrawable(
					R.drawable.look_down));
		}
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = (RelativeLayout) RelativeLayout.inflate(context,
					R.layout.offline_map_list_child, null);
		}
		ViewHolder holder = new ViewHolder(convertView);
		holder.cityName.setText(cityMap.get(groupPosition).get(childPosition)
				.getCity());
		holder.citySize.setText(String.valueOf(
				(cityMap.get(groupPosition).get(childPosition).getSize())
						/ (1024 * 1024f)).substring(0, 5) + "MB");
		
		if (cityMap.get(groupPosition).get(childPosition).getState() == OfflineMapStatus.SUCCESS) {
			holder.cityDown.setText("安装完成");
		} else if (cityMap.get(groupPosition).get(childPosition).getState() == OfflineMapStatus.LOADING) {
			if (groupPosition == groupPos && childPosition == childPos) {
				holder.cityDown.setText("正在下载" + completeCode + "%");
			}
		} else if (cityMap.get(groupPosition).get(childPosition).getState() == OfflineMapStatus.UNZIP) {
			holder.cityDown.setText("正在解压" + completeCode + "%");
		} else if (cityMap.get(groupPosition).get(childPosition).getState() == OfflineMapStatus.LOADING) {
			holder.cityDown.setText("下载");
		}
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {

		return true;
	}

	class ViewHolder {
		TextView cityName;
		TextView citySize;
		TextView cityDown;

		public ViewHolder(View view) {
			cityName = (TextView) view.findViewById(R.id.tv_current_city);
			citySize = (TextView) view.findViewById(R.id.tv_city_size);
			cityDown = (TextView) view.findViewById(R.id.tv_download);
		}
	}

}
