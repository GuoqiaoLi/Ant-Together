package com.meizhiyun.mayi.activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.meizhiyun.mayi.BaseApplication;
import com.meizhiyun.mayi.R;
import com.meizhiyun.mayi.adapter.VerificationAdapter;
import com.meizhiyun.mayi.bean.UrlConfig;
import com.meizhiyun.mayi.bean.VerificationBean;
import com.meizhiyun.mayi.utils.JsonTools;
import com.meizhiyun.mayi.utils.PreferenceUtil;
import com.meizhiyun.mayi.utils.SetScreenSizeUtils;

/**
 * 
 * @类名称: VerificationActivity
 * @类描述: 消息界面
 * @创建人：Guoqiao Li
 * @备注：
 * @version V1.0
 */

@ContentView(R.layout.verification_activity_layout)
public class VerificationActivity extends BaseActivity {

	@ViewInject(R.id.iv_back)
	private ImageView ivBack;
	@ViewInject(R.id.tv_title)
	private TextView tvTitle;
	@ViewInject(R.id.rl_title_bar)
	private RelativeLayout rlTitleBar;
	@ViewInject(R.id.slv)
	private SwipeMenuListView slv;
	@ViewInject(R.id.tv_none)
	private TextView tvNone;
	@ViewInject(R.id.ll_loading)
	private LinearLayout llLoading;
	@ViewInject(R.id.iv_loading)
	private ImageView ivLoading;
	private String userid;
	private VerificationAdapter adapter;
	private List<VerificationBean> veriList;
	private AnimationDrawable animationDrawable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化ViewUtils
		ViewUtils.inject(this);
		// 初始化布局
		initLayout();
		// 初始化数据
		init();
	}

	// 初始化数据
	private void init() {
		userid = PreferenceUtil.readString(this, "login", "userid");
		ivLoading.setBackgroundResource(R.drawable.loading_animation);
		animationDrawable = (AnimationDrawable) ivLoading.getBackground();
		requestVeriList();
	}

	// 初始化布局
	private void initLayout() {
		ivBack.setBackgroundResource(R.drawable.back_selector);
		tvTitle.setText("消息");

		// 设置标题栏的高度
		LayoutParams params = rlTitleBar.getLayoutParams();
		int[] titleSizes = SetScreenSizeUtils.getTitleSize(this, 88);
		params.width = titleSizes[0];
		params.height = titleSizes[1];
		rlTitleBar.setLayoutParams(params);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivBack, 41, 66);
	}

	// 请求消息列表
	private void requestVeriList() {
		llLoading.setVisibility(View.VISIBLE);
		animationDrawable.start();
		HttpUtils httpUtils = new HttpUtils(20000)
				.configCurrentHttpCacheExpiry(10000);
		RequestParams params = new RequestParams();
		params.addBodyParameter("action", "Message");
		params.addBodyParameter("userid", userid);
		httpUtils.send(HttpMethod.POST, UrlConfig.BASICURL, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (!TextUtils.isEmpty(responseInfo.result)) {
							animationDrawable.stop();
							llLoading.setVisibility(View.GONE);
							veriList = JsonTools
									.getVeriList(responseInfo.result);
							if (veriList != null && veriList.size() > 0) {
								tvNone.setVisibility(View.GONE);
								adapter = new VerificationAdapter(
										VerificationActivity.this, veriList,
										userid);
								slv.setAdapter(adapter);
								slv.setMenuCreator(creator);
								slv.setOnMenuItemClickListener(new OnMenuItemClickListener() {
									@Override
									public boolean onMenuItemClick(
											int position, SwipeMenu menu,
											int index) {
										switch (index) {
										case 0:
											String messageid = veriList.get(
													position).getMessageid();
											deleteRequest(messageid, position);
											break;
										}
										return false;
									}
								});
							} else {
								tvNone.setVisibility(View.VISIBLE);
							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						animationDrawable.stop();
						llLoading.setVisibility(View.GONE);
						BaseApplication.toastMethod(VerificationActivity.this,
								msg, 0);
					}
				});
	}

	// 删除消息的请求
	public void deleteRequest(String messageid, final int position) {
		llLoading.setVisibility(View.VISIBLE);
		animationDrawable.start();
		HttpUtils httpUtils = new HttpUtils(20000)
				.configCurrentHttpCacheExpiry(10000);
		RequestParams params = new RequestParams();
		params.addBodyParameter("action", "Handle_Message");
		params.addBodyParameter("userid", userid);
		params.addBodyParameter("messageid", messageid);
		params.addBodyParameter("operation", "3");
		params.addBodyParameter("type", "0");
		httpUtils.send(HttpMethod.POST, UrlConfig.BASICURL, params,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (!TextUtils.isEmpty(responseInfo.result)) {
							animationDrawable.stop();
							llLoading.setVisibility(View.GONE);
							try {
								JSONObject jsonObject = new JSONObject(
										responseInfo.result);
								String result = jsonObject.getString("result");
								if ("success".equals(result)) {
									veriList.remove(position);
									adapter.notifyDataSetChanged();
									if (veriList.size()==0) {
										tvNone.setVisibility(View.VISIBLE);
									}
								} else if ("fail".equals(result)) {
									BaseApplication.toastMethod(
											VerificationActivity.this,
											"网络请求失败,请检查网络", 1);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						animationDrawable.stop();
						llLoading.setVisibility(View.GONE);
						BaseApplication.toastMethod(VerificationActivity.this, "", 0);
					}
				});
	}

	private SwipeMenuCreator creator = new SwipeMenuCreator() {

		@Override
		public void create(SwipeMenu menu) {
			SwipeMenuItem deleteItem = new SwipeMenuItem(VerificationActivity.this);
			deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F,
					0x25)));
			deleteItem.setWidth(dp2px(72));
			deleteItem.setIcon(R.drawable.ic_delete);
			menu.addMenuItem(deleteItem);
		}
	};

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

	// 点击事件
	@OnClick({ R.id.iv_back })
	public void click(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;

		default:
			break;
		}
	}
}
