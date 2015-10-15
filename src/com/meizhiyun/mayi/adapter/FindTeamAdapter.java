package com.meizhiyun.mayi.adapter;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.meizhiyun.mayi.BaseApplication;
import com.meizhiyun.mayi.R;
import com.meizhiyun.mayi.bean.SearchedUserInfo;
import com.meizhiyun.mayi.bean.UrlConfig;
import com.meizhiyun.mayi.utils.CircleImageView;
import com.meizhiyun.mayi.utils.JsonTools;
import com.meizhiyun.mayi.utils.PreferenceUtil;
import com.meizhiyun.mayi.utils.SDUtils;
import com.meizhiyun.mayi.utils.SetScreenSizeUtils;

/**
 * 
 * @类名称: FindTeamAdapter
 * @类描述: 寻找组织时搜索到的人的适配器
 * @创建人：LiXinYang
 * @备注：
 * @version V1.0
 */
public class FindTeamAdapter extends BaseAdapter {

	private List<SearchedUserInfo> list;
	private Activity activity;
	private String userid;

	public FindTeamAdapter(Activity activity, String userid) {
		this.activity = activity;
		this.userid = userid;
	}

	public void setDataMethod(List<SearchedUserInfo> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (list != null && list.size() > 0) {
			return list.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {

		return list.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		final ViewHolder holder;
		if (convertView == null) {
			view = LayoutInflater.from(activity).inflate(
					R.layout.find_team_item_layout, null);
			holder = new ViewHolder();
			holder.civ = (CircleImageView) view.findViewById(R.id.civ);
			holder.tvNickname = (TextView) view.findViewById(R.id.tv_nickname);
			holder.ivAdd = (ImageView) view.findViewById(R.id.iv_add);
			holder.ivUserCount = (ImageView) view
					.findViewById(R.id.iv_user_count);
			holder.tvUserCount = (TextView) view
					.findViewById(R.id.tv_user_count);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}

		final SearchedUserInfo info = list.get(position);
		if ("yes".equals(info.getIstogether())) {
			SetScreenSizeUtils.setViewSizeFromWidth(activity, holder.ivAdd,
					106, 44);
			holder.ivAdd.setBackgroundResource(R.drawable.apply);
			holder.ivAdd.setClickable(false);
		} else if ("yes".equals(info.getIssend())) {
			SetScreenSizeUtils.setViewSizeFromWidth(activity, holder.ivAdd,
					106, 44);
			holder.ivAdd.setBackgroundResource(R.drawable.apply);
			holder.ivAdd.setClickable(false);
		} else {
			SetScreenSizeUtils.setViewSizeFromWidth(activity, holder.ivAdd,
					106, 44);
			holder.ivAdd.setBackgroundResource(R.drawable.jion);
			holder.ivAdd.setClickable(true);
		}

		holder.tvUserCount.setText(info.getNums());

		holder.ivAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				HttpUtils httpUtils = new HttpUtils(20000)
						.configCurrentHttpCacheExpiry(10000);
				RequestParams params = new RequestParams();
				params.addBodyParameter("action", "Member");
				params.addBodyParameter("userid", userid);
				params.addBodyParameter("touserid", info.getUserid());
				params.addBodyParameter("type", "1");
				httpUtils.send(HttpMethod.POST, UrlConfig.BASICURL, params,
						new RequestCallBack<String>() {

							@Override
							public void onSuccess(
									ResponseInfo<String> responseInfo) {
								if (!TextUtils.isEmpty(responseInfo.result)) {
									JsonTools.getFindTeamResult(activity,
											responseInfo.result);
								}
							}

							@Override
							public void onFailure(HttpException error,
									String msg) {
								BaseApplication.toastMethod(activity, msg, 0);
							}
						});
			}
		});

		SetScreenSizeUtils.setViewSizeFromWidth(activity, holder.civ, 100, 100);

		SetScreenSizeUtils.setViewSizeFromWidth(activity, holder.ivUserCount,
				25, 29);

		holder.tvNickname.setText(info.getNickname());
		final String path = SDUtils.getSDCardPath() + File.separator + "MaYi"
				+ File.separator + "icon" + File.separator + info.getUserid()
				+ ".jpg";
		String iconPath = PreferenceUtil.readString(activity, "usersInfo",
				info.getUserid() + "icon");
		String picurl = info.getPicurl();
		if (!TextUtils.isEmpty(iconPath)) {
			Bitmap bitmap = BitmapFactory.decodeFile(iconPath);
			if (bitmap != null) {
				holder.civ.setImageBitmap(bitmap);
			}
		} else if (!TextUtils.isEmpty(picurl) && !"null".equals(picurl)) {
			HttpUtils httpUtils = new HttpUtils(20000)
					.configCurrentHttpCacheExpiry(10000);
			httpUtils.download(info.getPicurl(), path,
					new RequestCallBack<File>() {

						@Override
						public void onSuccess(ResponseInfo<File> responseInfo) {
							if (responseInfo.result != null) {
								Bitmap bitmap = BitmapFactory.decodeFile(path);
								holder.civ.setImageBitmap(bitmap);
								PreferenceUtil.write(activity, "usersInfo",
										info.getUserid() + "icon", path);
							}
						}

						@Override
						public void onFailure(HttpException error, String msg) {

						}
					});
		} else {
			holder.civ.setImageResource(R.drawable.user);
		}

		return view;
	}

	class ViewHolder {
		CircleImageView civ;
		TextView tvNickname;
		ImageView ivAdd;
		ImageView ivUserCount;
		TextView tvUserCount;
	}

}
