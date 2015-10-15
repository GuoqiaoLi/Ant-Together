package com.meizhiyun.mayi.adapter;

import java.io.File;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
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
import android.widget.RelativeLayout;
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
import com.meizhiyun.mayi.bean.VerificationBean;
import com.meizhiyun.mayi.utils.CircleImageView;
import com.meizhiyun.mayi.utils.PreferenceUtil;
import com.meizhiyun.mayi.utils.SDUtils;
import com.meizhiyun.mayi.utils.SetScreenSizeUtils;

/**
 * 
 * @类名称: VerificationAdapter
 * @类描述: 消息列表的适配器
 * @创建人：Guoqiao Li
 * @备注：
 * @version V1.0
 */
public class VerificationAdapter extends BaseAdapter {

	private Activity context;
	private String userid;
	private List<VerificationBean> mList;

	public VerificationAdapter(Activity context, List<VerificationBean> list,
			String userid) {
		this.context = context;
		this.mList = list;
		this.userid = userid;
	}

	@Override
	public int getCount() {
		if (mList != null && mList.size() > 0) {
			return mList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
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
					R.layout.veri_list_item_layout, null);
			holder = new ViewHolder();
			holder.civ = (CircleImageView) view.findViewById(R.id.civ);
			holder.tvNickname = (TextView) view.findViewById(R.id.tv_nickname);
			holder.tvNotice = (TextView) view.findViewById(R.id.tv_notice);
			holder.ivAgree = (ImageView) view.findViewById(R.id.iv_agree);
			holder.rlItem = (RelativeLayout) view.findViewById(R.id.rl_item);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}

		SetScreenSizeUtils.setViewSizeFromWidth(context, holder.rlItem, 640,
				134);
		SetScreenSizeUtils.setViewSizeFromWidth(context, holder.civ, 100, 100);
		SetScreenSizeUtils.setViewSizeFromWidth(context, holder.ivAgree, 106,
				44);
//		holder.tvNickname.setTextSize(TypedValue.COMPLEX_UNIT_SP,
//				SetScreenSizeUtils.getFontSize(context, 32));
//		holder.tvNotice.setTextSize(TypedValue.COMPLEX_UNIT_SP,
//				SetScreenSizeUtils.getFontSize(context, 28));

		VerificationBean bean = mList.get(position);
		String sourcepicurl = bean.getSourcepicurl();
		final String messagetype = bean.getMessagetype();
		String sourcenickname = bean.getSourcenickname();
		final String messageid = bean.getMessageid();
		final String sourceuserid = bean.getSourceuserid();

		holder.tvNickname.setText(sourcenickname);
		if ("0".equals(messagetype)) {
			holder.tvNotice.setText("邀请您加入同行");
		} else if ("1".equals(messagetype)) {
			holder.tvNotice.setText("请求加入同行");
		}

		String iconPath = PreferenceUtil.readString(context, "usersInfo",
				sourceuserid + "icon");
		final String path = SDUtils.getSDCardPath() + File.separator + "MaYi"
				+ File.separator + "icon" + File.separator + sourceuserid
				+ ".jpg";
		Bitmap bitmap = null;
		if (!TextUtils.isEmpty(iconPath)) {
			bitmap = BitmapFactory.decodeFile(iconPath);
		}
		if ((TextUtils.isEmpty(iconPath) || bitmap == null)&&!TextUtils.isEmpty(sourcepicurl)) {
			HttpUtils httpUtils = new HttpUtils(20000)
					.configCurrentHttpCacheExpiry(10000);
			httpUtils.download(sourcepicurl, path,
					new RequestCallBack<File>() {

						@Override
						public void onSuccess(ResponseInfo<File> responseInfo) {
							if (responseInfo.result != null) {
								Bitmap bitmap = BitmapFactory.decodeFile(path);
								holder.civ.setImageBitmap(bitmap);
								PreferenceUtil.write(context, "usersInfo",
										sourceuserid + "icon", path);
							}
						}
						@Override
						public void onFailure(HttpException error, String msg) {
						}
					});
		}else if (bitmap!=null) {
			holder.civ.setImageBitmap(bitmap);
		}
		
//		if (!TextUtils.isEmpty(iconPath)) {
//			Bitmap bitmap = BitmapFactory.decodeFile(iconPath);
//			if (bitmap != null) {
//				holder.civ.setImageBitmap(bitmap);
//			}
//		} else {
//			HttpUtils httpUtils = new HttpUtils(20000)
//					.configCurrentHttpCacheExpiry(10000);
//			httpUtils.download(sourcepicurl, utils.getDownImgPath(),
//					new RequestCallBack<File>() {
//
//						@Override
//						public void onSuccess(ResponseInfo<File> responseInfo) {
//							holder.civ.setImageBitmap(BitmapFactory
//									.decodeFile(utils.getDownImgPath()));
//							PreferenceUtil.write(context, "usersInfo",
//									sourceuserid + "icon",
//									utils.getDownImgPath());
//						}
//
//						@Override
//						public void onFailure(HttpException error, String msg) {
//
//						}
//					});
//		}

		holder.ivAgree.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String isactive = PreferenceUtil.readString(context, "login", "isactive");
				if ("yes".equals(isactive)&&"0".equals(messagetype)) {
					Builder builder = new AlertDialog.Builder(context);
					builder.setTitle("提示");
					builder.setMessage("不能同时加入两个小组哦");
					builder.setPositiveButton("退出当前同行",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									logoutMethod(holder.ivAgree, userid, messageid, messagetype);
								}

							});
					builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					builder.create().show();
				}else {
					agreeRequest(holder.ivAgree, userid, messageid, messagetype);
				}
			}
		});

		return view;
	}

	class ViewHolder {
		CircleImageView civ;
		TextView tvNickname;
		TextView tvNotice;
		ImageView ivAgree;
		RelativeLayout rlItem;
	}

	// 退出活动的方法
		private void logoutMethod(final ImageView iv, final String userid,
				final String messageid, final String messagetype) {
			String isCaptain = PreferenceUtil.readString(context, "login", "iscaptain");
			HttpUtils httpUtils = new HttpUtils(20000)
					.configCurrentHttpCacheExpiry(10000);
			RequestParams params = new RequestParams();
			params.addBodyParameter("action", "Quit");
			params.addBodyParameter("userid", userid);
			params.addBodyParameter("iscaptain", isCaptain);
			httpUtils.send(HttpMethod.POST, UrlConfig.BASICURL, params,
					new RequestCallBack<String>() {
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							if (!TextUtils.isEmpty(responseInfo.result)) {
								try {
									JSONObject jsonObject = new JSONObject(
											responseInfo.result);
									String result = jsonObject.getString("result");
									if ("success".equals(result)) {
										agreeRequest(iv, userid, messageid, messagetype);
									} else if ("fail".equals(result)) {
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}

						@Override
						public void onFailure(HttpException error, String msg) {
							BaseApplication.toastMethod(context, msg, 0);
						}
					});
		}
	
	//同意请求的方法
	public void agreeRequest(final ImageView iv, String userid,
			String messageid, final String messagetype) {
		HttpUtils httpUtils = new HttpUtils(20000)
				.configCurrentHttpCacheExpiry(10000);
		RequestParams params = new RequestParams();
		params.addBodyParameter("action", "Handle_Message");
		params.addBodyParameter("userid", userid);
		params.addBodyParameter("type", "0");
		params.addBodyParameter("messageid", messageid);
		params.addBodyParameter("operation", "1");
		httpUtils.send(HttpMethod.POST, UrlConfig.BASICURL, params,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (!TextUtils.isEmpty(responseInfo.result)) {
							try {
								JSONObject jsonObject = new JSONObject(
										responseInfo.result);
								String result = jsonObject.getString("result");
								if ("success".equals(result)) {
									iv.setBackgroundResource(R.drawable.agree);
									if ("0".equals(messagetype)) {
										String isactive = PreferenceUtil
												.readString(context, "login",
														"isactive");
										if ("yes".equals(isactive)) {
											showDialog();
										} else if ("no".equals(isactive)) {
											PreferenceUtil.write(context,
													"login", "iscaptain", "no");
											PreferenceUtil.write(context,
													"login", "active_method",
													"iscaptain_no");
										}
									} else if ("1".equals(messagetype)) {
										String iscaptain = PreferenceUtil
												.readString(context, "login",
														"iscaptain");
										if ("yes".equals(iscaptain)) {
											PreferenceUtil.write(context,
													"login", "active_method",
													"iscaptain_yes");
										} else {
											PreferenceUtil.write(context,
													"login", "active_method",
													"iscaptain_no");
										}
									}
								} else if ("fail".equals(result)) {
									BaseApplication.toastMethod(context,
											"网络请求失败,请检查网络", 1);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}

					private void showDialog() {
						Builder builder = new AlertDialog.Builder(context);
						builder.setTitle("提示");
						builder.setMessage("您不可以同时加入两个活动，请您先退出当前活动！");
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}

								});
						builder.create().show();
					}

					@Override
					public void onFailure(HttpException error, String msg) {

					}
				});
	}

}
