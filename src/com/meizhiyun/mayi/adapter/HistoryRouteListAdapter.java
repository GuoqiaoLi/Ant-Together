package com.meizhiyun.mayi.adapter;

import java.io.File;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.meizhiyun.mayi.BaseApplication;
import com.meizhiyun.mayi.R;
import com.meizhiyun.mayi.activity.HistoryMapActivity;
import com.meizhiyun.mayi.bean.HistoryRouteList;
import com.meizhiyun.mayi.bean.HistoryRouteUserInfo;
import com.meizhiyun.mayi.bean.UrlConfig;
import com.meizhiyun.mayi.utils.CircleImageView;
import com.meizhiyun.mayi.utils.PreferenceUtil;
import com.meizhiyun.mayi.utils.SDUtils;
import com.meizhiyun.mayi.utils.SetScreenSizeUtils;

/**
 * 
 * @类名称: HistoryRouteListAdapter
 * @类描述: 历史轨迹列表的适配器
 * @创建人：Guoqiao Li
 * @备注：
 * @version V1.0
 */
public class HistoryRouteListAdapter extends BaseAdapter {

	private String userid;
	private List<HistoryRouteList> list;
	private Activity activity;
	private Bitmap bitmap = null;

	public HistoryRouteListAdapter(Activity activity, String userid) {
		this.userid = userid;
		this.activity = activity;
	}
	public void setData(List<HistoryRouteList> list) {
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
		ViewHolder holder;
		if (convertView == null) {
			view = LayoutInflater.from(activity).inflate(
					R.layout.history_map_list_item, null);
			holder = new ViewHolder();
			holder.tvDelete = (TextView) view.findViewById(R.id.tv_delete);
			holder.tvEnd = (TextView) view.findViewById(R.id.tv_history_end);
			holder.tvStart = (TextView) view
					.findViewById(R.id.tv_history_start);
			holder.tvTime = (TextView) view.findViewById(R.id.tv_time);
			holder.tvLook = (TextView) view.findViewById(R.id.tv_look);
			holder.tvNum = (TextView) view.findViewById(R.id.tv_num);
			holder.civ1 = (CircleImageView) view.findViewById(R.id.civ1);
			holder.civ2 = (CircleImageView) view.findViewById(R.id.civ2);
			holder.civ3 = (CircleImageView) view.findViewById(R.id.civ3);
			holder.civ4 = (CircleImageView) view.findViewById(R.id.civ4);
			holder.civ5 = (CircleImageView) view.findViewById(R.id.civ5);
			holder.civ6 = (CircleImageView) view.findViewById(R.id.civ6);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}

		holder.civ1.setVisibility(View.GONE);
		holder.civ2.setVisibility(View.GONE);
		holder.civ3.setVisibility(View.GONE);
		holder.civ4.setVisibility(View.GONE);
		holder.civ5.setVisibility(View.GONE);
		holder.civ6.setVisibility(View.GONE);
		
		HistoryRouteList routeList = list.get(position);
		holder.tvTime.setText(routeList.getEndTime());
		holder.tvStart.setText(routeList.getStartlocation());
		holder.tvEnd.setText(routeList.getEndlocation());
		holder.tvNum.setText(String.valueOf(routeList.getCount()));
		List<HistoryRouteUserInfo> picList = routeList.getPicList();
		SetScreenSizeUtils.setViewSizeFromWidth(activity, holder.civ1, 60, 60);
		SetScreenSizeUtils.setViewSizeFromWidth(activity, holder.civ2, 60, 60);
		SetScreenSizeUtils.setViewSizeFromWidth(activity, holder.civ3, 60, 60);
		SetScreenSizeUtils.setViewSizeFromWidth(activity, holder.civ4, 60, 60);
		SetScreenSizeUtils.setViewSizeFromWidth(activity, holder.civ5, 60, 60);
		SetScreenSizeUtils.setViewSizeFromWidth(activity, holder.civ6, 60, 60);

		for (int i = 0; i < picList.size(); i++) {
			if (i < 6) {
				HistoryRouteUserInfo userInfo = picList.get(i);
				final String userid = userInfo.getUserid();
				String picurl = userInfo.getPicurl();
				final String path = SDUtils.getSDCardPath() + File.separator
						+ "MaYi" + File.separator + "icon" + File.separator
						+ userid + ".jpg";
				String iconPath = PreferenceUtil.readString(activity,
						"usersInfo", userid + "icon");
				
				if (!TextUtils.isEmpty(iconPath)) {
					bitmap = BitmapFactory.decodeFile(iconPath);
				}
				if ((TextUtils.isEmpty(iconPath) || bitmap == null)
						&& !TextUtils.isEmpty(picurl)) {
					HttpUtils httpUtils = new HttpUtils(20000)
							.configCurrentHttpCacheExpiry(10000);
					httpUtils.download(picurl, path,
							new RequestCallBack<File>() {

								@Override
								public void onSuccess(
										ResponseInfo<File> responseInfo) {
									if (responseInfo.result != null) {
										bitmap = BitmapFactory.decodeFile(path);
										PreferenceUtil.write(activity,
												"usersInfo", userid + "icon",
												path);
									}
								}
								@Override
								public void onFailure(HttpException error,
										String msg) {
								}
							});
				}
				if (i == 0) {
					holder.civ1.setVisibility(View.VISIBLE);
					holder.civ1.setImageResource(R.drawable.user);
					if (bitmap != null) {
						holder.civ1.setImageBitmap(bitmap);
					}
				} else if (i == 1) {
					holder.civ2.setVisibility(View.VISIBLE);
					holder.civ2.setImageResource(R.drawable.user);
					if (bitmap != null) {
						holder.civ2.setImageBitmap(bitmap);
					}
				} else if (i == 2) {
					holder.civ3.setVisibility(View.VISIBLE);
					holder.civ3.setImageResource(R.drawable.user);
					if (bitmap != null) {
						holder.civ3.setImageBitmap(bitmap);
					}
				} else if (i == 3) {
					holder.civ4.setVisibility(View.VISIBLE);
					holder.civ4.setImageResource(R.drawable.user);
					if (bitmap != null) {
						holder.civ4.setImageBitmap(bitmap);
					}
				} else if (i == 4) {
					holder.civ5.setVisibility(View.VISIBLE);
					holder.civ5.setImageResource(R.drawable.user);
					if (bitmap != null) {
						holder.civ5.setImageBitmap(bitmap);
					}
				} else if (i == 5) {
					holder.civ6.setVisibility(View.VISIBLE);
					holder.civ6.setImageResource(R.drawable.user);
					if (bitmap != null) {
						holder.civ6.setImageBitmap(bitmap);
					}
				}
			}
		}
		holder.tvDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle("提示");
				builder.setMessage("您确定要删除此条历史轨迹吗？");
				builder.setPositiveButton("确定删除",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								deleteHistory();
							}

							private void deleteHistory() {
								HttpUtils httpUtils = new HttpUtils(20000)
										.configCurrentHttpCacheExpiry(10000);
								RequestParams params = new RequestParams();
								params.addBodyParameter("action", "DeletePath");
								params.addBodyParameter("userid", userid);
								params.addBodyParameter("groupid",
										list.get(position).getGroupid());
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
															BaseApplication
																	.toastMethod(
																			activity,
																			"历史轨迹删除成功",
																			1);
															list.remove(position);
															notifyDataSetChanged();
														} else if ("fail"
																.equals(result)) {
															BaseApplication
																	.toastMethod(
																			activity,
																			"历史轨迹删除失败",
																			1);
														} else if ("error"
																.equals(result)) {
															BaseApplication
																	.toastMethod(
																			activity,
																			"未查询到该历史轨迹",
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
												BaseApplication.toastMethod(
														activity, "", 0);
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

		holder.tvLook.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String groupid = list.get(position).getGroupid();
				Intent intent = new Intent(activity, HistoryMapActivity.class);
				intent.putExtra("groupid", groupid);
				activity.startActivity(intent);
			}
		});

		return view;
	}

	class ViewHolder {
		TextView tvTime;
		TextView tvStart;
		TextView tvEnd;
		TextView tvLook;
		TextView tvDelete;
		TextView tvNum;
		CircleImageView civ1;
		CircleImageView civ2;
		CircleImageView civ3;
		CircleImageView civ4;
		CircleImageView civ5;
		CircleImageView civ6;
	}

}