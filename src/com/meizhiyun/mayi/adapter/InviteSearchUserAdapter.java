package com.meizhiyun.mayi.adapter;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Context;
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
 * @类名称: InviteSearchUserAdapter
 * @类描述: 邀请时搜索到的人的适配器
 * @创建人：Guoqiao Li
 * @备注：
 * @version V1.0
 */
public class InviteSearchUserAdapter extends BaseAdapter {

	private Context context;
	private List<SearchedUserInfo> list;
	private Activity activity;
	private String userid;

	public InviteSearchUserAdapter(Activity activity, Context context,
			String userid) {
		this.context = context;
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
			view = LayoutInflater.from(context).inflate(
					R.layout.invite_result_list_item, null);
			holder = new ViewHolder();
			holder.civ = (CircleImageView) view.findViewById(R.id.civ);
			holder.tvNickname = (TextView) view.findViewById(R.id.tv_nickname);
			holder.ivInvite = (ImageView) view.findViewById(R.id.iv_invite);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}

		final SearchedUserInfo info = list.get(position);
		if ("yes".equals(info.getIstogether())) {
			SetScreenSizeUtils.setViewSizeFromWidth(activity, holder.ivInvite,
					129, 44);
			holder.ivInvite.setBackgroundResource(R.drawable.now);
			holder.ivInvite.setClickable(false);
		} else if ("yes".equals(info.getIssend())) {
			SetScreenSizeUtils.setViewSizeFromWidth(activity, holder.ivInvite,
					106, 44);
			holder.ivInvite.setBackgroundResource(R.drawable.inviteover);
			holder.ivInvite.setClickable(false);
		} else {
			SetScreenSizeUtils.setViewSizeFromWidth(activity, holder.ivInvite,
					106, 44);
			holder.ivInvite.setBackgroundResource(R.drawable.invitefriend_);
			holder.ivInvite.setClickable(true);
		}

		holder.ivInvite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				HttpUtils httpUtils = new HttpUtils(20000)
						.configCurrentHttpCacheExpiry(10000);
				RequestParams params = new RequestParams();
				params.addBodyParameter("action", "Member");
				params.addBodyParameter("userid", userid);
				params.addBodyParameter("touserid", info.getUserid());
				params.addBodyParameter("type", "0");
				httpUtils.send(HttpMethod.POST, UrlConfig.BASICURL, params,
						new RequestCallBack<String>() {

							@Override
							public void onSuccess(
									ResponseInfo<String> responseInfo) {
								if (!TextUtils.isEmpty(responseInfo.result)) {
									JsonTools.getInviteUserResult(activity,
											responseInfo.result,
											holder.ivInvite, true);
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

		holder.tvNickname.setText(info.getNickname());

		String picurl = info.getPicurl();
		String iconPath = PreferenceUtil.readString(context, "usersInfo",
				info.getUserid() + "icon");
		final String path = SDUtils.getSDCardPath() + File.separator + "MaYi"
				+ File.separator + "icon" + File.separator + info.getUserid()
				+ ".jpg";
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
		ImageView ivInvite;
	}

}
