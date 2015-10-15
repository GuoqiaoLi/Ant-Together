package com.meizhiyun.mayi.adapter;

import java.io.File;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.meizhiyun.mayi.BaseApplication;
import com.meizhiyun.mayi.R;
import com.meizhiyun.mayi.bean.UrlConfig;
import com.meizhiyun.mayi.bean.UserLatInfo;
import com.meizhiyun.mayi.utils.CircleImageView;
import com.meizhiyun.mayi.utils.PreferenceUtil;
import com.meizhiyun.mayi.utils.SDUtils;
import com.meizhiyun.mayi.utils.SetScreenSizeUtils;

/**
 * 
 * @类名称: TeammatesListAdapter
 * @类描述: 组员列表的适配器
 * @创建人：Guoqiao Li
 * @备注：
 * @version V1.0
 */
public class TeammatesListAdapter extends BaseAdapter {

	private Context context;
	private List<UserLatInfo> list;
	private Activity activity;
	private String userid;
	private boolean isDelete;

	public TeammatesListAdapter(Activity activity, Context context,
			String userid) {
		this.context = context;
		this.activity = activity;
		this.userid = userid;
	}

	public void deleteUser(boolean isDelete, List<UserLatInfo> list) {
		this.isDelete = isDelete;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = null;
		final ViewHolder holder;
		if (convertView == null) {
			view = LayoutInflater.from(context).inflate(
					R.layout.teammates_list_item, null);
			holder = new ViewHolder();
			holder.civ = (CircleImageView) view.findViewById(R.id.civ_list);
			holder.tvNickname = (TextView) view
					.findViewById(R.id.tv_nickname_list);
			holder.ivDelete = (ImageView) view.findViewById(R.id.iv_delete);
			holder.llTeammatesList = (LinearLayout) view
					.findViewById(R.id.ll_teammates_list);
			holder.ivQun = (ImageView) view.findViewById(R.id.iv_qun);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}

		final UserLatInfo info = list.get(position);

		SetScreenSizeUtils.setViewSizeFromWidth(activity, holder.civ, 80, 80);
		SetScreenSizeUtils.setViewSizeFromWidth(activity,
				holder.llTeammatesList, 640, 115);
		SetScreenSizeUtils.setViewSizeFromWidth(activity, holder.ivDelete, 41,
				41);
		SetScreenSizeUtils.setViewSizeFromWidth(activity, holder.ivQun, 42, 32);

		holder.tvNickname.setText(info.getNickname());
		if (isDelete) {
			holder.ivDelete.setVisibility(View.VISIBLE);
		} else {
			holder.ivDelete.setVisibility(View.GONE);
		}

		String iconPath = PreferenceUtil.readString(context, "usersInfo",
				info.getUserid() + "icon");

		final String icon = SDUtils.getSDCardPath() + File.separator + "MaYi"
				+ File.separator + "icon" + File.separator + info.getUserid()
				+ ".jpg";

		if (!TextUtils.isEmpty(iconPath)) {
			Bitmap bitmap = BitmapFactory.decodeFile(iconPath);
			if (bitmap != null) {
				holder.civ.setImageBitmap(bitmap);
			}
		} else {
			HttpUtils httpUtils = new HttpUtils(20000)
					.configCurrentHttpCacheExpiry(10000);
			httpUtils.download(info.getPicurl(), icon,
					new RequestCallBack<File>() {

						@Override
						public void onSuccess(ResponseInfo<File> responseInfo) {
							if (responseInfo.result != null) {
								Bitmap bitmap = BitmapFactory.decodeFile(icon);
								holder.civ.setImageBitmap(bitmap);
								PreferenceUtil.write(context, "usersInfo",
										info.getUserid() + "icon", icon);
							}
						}

						@Override
						public void onFailure(HttpException error, String msg) {

						}
					});
		}

		holder.ivDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialogMethod();
			}

			// 显示对话框的方法
			private void showDialogMethod() {
				Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle("提示");
				builder.setMessage("确定要删除" + "“" + info.getNickname() + "”"
						+ "吗？");
				builder.setPositiveButton("删除",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								deleteRequest();
							}

							private void deleteRequest() {
								HttpUtils httpUtils = new HttpUtils(20000)
										.configCurrentHttpCacheExpiry(10000);
								RequestParams params = new RequestParams();
								params.addBodyParameter("action", "Kick");
								params.addBodyParameter("userid",
										info.getUserid());
								params.addBodyParameter("captainid", userid);
								httpUtils.send(HttpMethod.POST,
										UrlConfig.BASICURL, params,
										new RequestCallBack<String>() {
											@Override
											public void onSuccess(
													ResponseInfo<String> responseInfo) {
												if (!TextUtils
														.isEmpty(responseInfo.result)) {
													try {
														JSONObject jsonObject = new JSONObject(
																responseInfo.result);
														String result = jsonObject
																.getString("result");
														if ("success"
																.equals(result)) {
															list.remove(position);
															BaseApplication
																	.toastMethod(
																			activity,
																			"成功把他T走了",
																			1);
														} else if ("fail"
																.equals(result)) {
															BaseApplication
																	.toastMethod(
																			activity,
																			"失败了~~~~(>_<)~~~~",
																			1);
														}
													} catch (JSONException e) {
														e.printStackTrace();
													}
												}
											}

											@Override
											public void onFailure(
													HttpException error,
													String msg) {
											}
										});
							}

						});
				builder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder.create().show();
			}

		});

		return view;
	}

	class ViewHolder {
		CircleImageView civ;
		TextView tvNickname;
		ImageView ivDelete;
		LinearLayout llTeammatesList;
		ImageView ivQun;
	}

}
