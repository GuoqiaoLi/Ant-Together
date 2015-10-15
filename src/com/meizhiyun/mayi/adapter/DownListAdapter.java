package com.meizhiyun.mayi.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.meizhiyun.mayi.R;
import com.meizhiyun.mayi.view.MyView;

public class DownListAdapter extends BaseAdapter {

	private Context context;

	private List<OfflineMapCity> cities;

	public DownListAdapter(Context context, List<OfflineMapCity> cities) {
		this.cities = cities;
		this.context = context;
	}

	@Override
	public int getCount() {
		return cities.size();
	}

	@Override
	public Object getItem(int position) {
		return cities.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = (RelativeLayout) RelativeLayout.inflate(context,
					R.layout.offline_map_list_child, null);
		}
		ViewHolder holder = new ViewHolder(convertView);
		holder.cityName.setText((cities.get(position)).getCity());
		holder.citySize.setText(String.valueOf(
				cities.get(position).getSize() / (1024 * 1024f))
				.substring(0, 5)
				+ "MB");
		holder.cityDown.setText("安装完成");
		holder.rlOfflineChild.setBackground(null);
		return convertView;

	}

	class ViewHolder {
		TextView cityName;
		TextView citySize;
		RelativeLayout rlOfflineChild;
		MyView cityDown;

		public ViewHolder(View view) {
			cityName = (TextView) view.findViewById(R.id.tv_city);
			citySize = (TextView) view.findViewById(R.id.tv_city_size);
			cityDown = (MyView) view.findViewById(R.id.tv_download);
			rlOfflineChild = (RelativeLayout) view.findViewById(R.id.rl_offline_child);
		}
	}
}
