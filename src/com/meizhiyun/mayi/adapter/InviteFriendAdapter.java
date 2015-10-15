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
 * @类名称: InviteFriendAdapter
 * @类描述: 邀请好友时推荐的好友列表
 * @创建人：KevinLee
 * @备注：
 * @version V1.0
 */
public class InviteFriendAdapter extends BaseAdapter {

	private String userid;
	private Activity activity;
	private List<SearchedUserInfo> list;

	public InviteFriendAdapter(Activity activity, String userid) {
		this.activity = activity;
		this.userid = userid;
	}

	public void setData(List<SearchedUserInfo> list) {
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
					R.layout.invite_together_friend_list_item, null);
			holder = new ViewHolder();
			holder.civ = (CircleImageView) view.findViewById(R.id.civ);
			holder.tvNickname = (TextView) view.findViewById(R.id.tv_nickname);
			holder.ivInvite = (ImageView) view.findViewById(R.id.iv_invite);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}

		SetScreenSizeUtils
				.setViewSizeFromHeight(activity, holder.civ, 100, 100);
		SetScreenSizeUtils.setViewSizeFromHeight(activity, holder.ivInvite,
				106, 44);

		SearchedUserInfo info = list.get(position);
		final String touserid = info.getUserid();
		String picurl = info.getPicurl();
		String nickname = info.getNickname();
		String iconPath = PreferenceUtil.readString(activity, "usersInfo",
				touserid + "icon");

		holder.tvNickname.setText(nickname);
		holder.civ.setImageResource(R.drawable.user);
		Bitmap bitmap = null;
		if (!TextUtils.isEmpty(iconPath)) {
			bitmap = BitmapFactory.decodeFile(iconPath);
		}
		if ((TextUtils.isEmpty(iconPath) || bitmap == null)&&!TextUtils.isEmpty(info.getPicurl())) {
			final String path = SDUtils.getSDCardPath() + File.separator
					+ "MaYi" + File.separator + "icon" + File.separator
					+ touserid + ".jpg";
			HttpUtils httpUtils = new HttpUtils(20000)
					.configCurrentHttpCacheExpiry(10000);
			httpUtils.download(picurl, path,
					new RequestCallBack<File>() {

						@Override
						public void onSuccess(ResponseInfo<File> responseInfo) {
							if (responseInfo.result != null) {
								Bitmap bitmap = BitmapFactory.decodeFile(path);
								holder.civ.setImageBitmap(bitmap);
								PreferenceUtil.write(activity, "usersInfo",
										touserid + "icon", path);
							}
						}

						@Override
						public void onFailure(HttpException error, String msg) {
						}
					});
		} else if (bitmap != null) {
			holder.civ.setImageBitmap(bitmap);
		}

		holder.ivInvite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				HttpUtils httpUtils = new HttpUtils(20000)
						.configCurrentHttpCacheExpiry(10000);
				RequestParams params = new RequestParams();
				params.addBodyParameter("action", "Member");
				params.addBodyParameter("userid", userid);
				params.addBodyParameter("touserid", touserid);
				params.addBodyParameter("type", "0");
				httpUtils.send(HttpMethod.POST, UrlConfig.BASICURL, params,
						new RequestCallBack<String>() {

							@Override
							public void onSuccess(
									ResponseInfo<String> responseInfo) {
								if (!TextUtils.isEmpty(responseInfo.result)) {
									JsonTools.getInviteUserResult(activity,
											responseInfo.result,
											holder.ivInvite, false);
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

		return view;
	}

	class ViewHolder {
		CircleImageView civ;
		TextView tvNickname;
		ImageView ivInvite;
	}

}
