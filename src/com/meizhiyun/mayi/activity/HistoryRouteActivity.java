package com.meizhiyun.mayi.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.meizhiyun.mayi.adapter.HistoryRouteListAdapter;
import com.meizhiyun.mayi.bean.HistoryRouteList;
import com.meizhiyun.mayi.bean.HistoryRouteUserInfo;
import com.meizhiyun.mayi.bean.UrlConfig;
import com.meizhiyun.mayi.utils.PreferenceUtil;
import com.meizhiyun.mayi.utils.SetScreenSizeUtils;

/**
 * 
 * @类名称: HistoryRoute
 * @类描述: 历史轨迹界面
 * @创建人：Guoqiao Li
 * @备注：
 * @version V1.0
 */

@ContentView(R.layout.history_route_activity)
public class HistoryRouteActivity extends BaseActivity {

	@ViewInject(R.id.iv_back)
	private ImageView ivBack;
	@ViewInject(R.id.tv_title)
	private TextView tvTitle;
	@ViewInject(R.id.rl_title_bar)
	private RelativeLayout rlTitleBar;
	@ViewInject(R.id.lv)
	private ListView lv;
	@ViewInject(R.id.tv_none)
	private TextView tvNone;
	@ViewInject(R.id.ll_loading)
	private LinearLayout llLoading;
	@ViewInject(R.id.iv_loading)
	private ImageView ivLoading;
	private AnimationDrawable animationDrawable;

	private List<HistoryRouteList> list;
	private String userid;
	private HistoryRouteListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化VIewUtils
		ViewUtils.inject(this);
		// 初始化数据
		init();
	}

	// 初始化数据
	private void init() {
		tvTitle.setText("历史轨迹");
		ivBack.setBackgroundResource(R.drawable.back_selector);
		ivLoading.setBackgroundResource(R.drawable.loading_animation);
		animationDrawable = (AnimationDrawable) ivLoading.getBackground();
		// 设置标题栏的高度
		LayoutParams params = rlTitleBar.getLayoutParams();
		int[] titleSizes = SetScreenSizeUtils.getTitleSize(this, 88);
		params.width = titleSizes[0];
		params.height = titleSizes[1];
		rlTitleBar.setLayoutParams(params);

		SetScreenSizeUtils.setViewSizeFromWidth(this, ivBack, 41, 66);
		userid = PreferenceUtil.readString(this, "login", "userid");
		adapter = new HistoryRouteListAdapter(HistoryRouteActivity.this,
				userid);
		lv.setAdapter(adapter);
	}

	// 获取历史轨迹列表
	private void getHistoryRouteList() {
		llLoading.setVisibility(View.VISIBLE);
		animationDrawable.start();
		HttpUtils httpUtils = new HttpUtils(20000)
				.configCurrentHttpCacheExpiry(10000);
		RequestParams params = new RequestParams();
		params.addBodyParameter("action", "HistoryPath");
		params.addBodyParameter("userid", userid);
		httpUtils.send(HttpMethod.POST, UrlConfig.BASICURL, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						animationDrawable.stop();
						llLoading.setVisibility(View.GONE);
						if (!TextUtils.isEmpty(responseInfo.result)) {
							try {
								JSONObject jsonObject = new JSONObject(
										responseInfo.result);
								int result = jsonObject.getInt("result");
								if (result == 0) {
									tvNone.setVisibility(View.VISIBLE);
								} else {
									list = new ArrayList<HistoryRouteList>();
									tvNone.setVisibility(View.GONE);
									JSONArray jsonArray = jsonObject
											.getJSONArray("data");
									for (int i = 0; i < jsonArray.length(); i++) {
										HistoryRouteList historyRouteList = new HistoryRouteList();
										JSONObject object = jsonArray
												.getJSONObject(i);
										String groupid = object
												.getString("groupid");
										String endTime = object
												.getString("endtime");
										int count = object.getInt("count");
										String startlocation = object
												.getString("startlocation");
										String endlocation = object
												.getString("endlocation");
										historyRouteList.setGroupid(groupid);
										historyRouteList.setEndTime(endTime);
										historyRouteList.setCount(count);
										historyRouteList
												.setStartlocation(startlocation);
										historyRouteList
												.setEndlocation(endlocation);
										JSONArray array = object
												.getJSONArray("members");
										List<HistoryRouteUserInfo> picList = new ArrayList<HistoryRouteUserInfo>();
										for (int j = 0; j < array.length(); j++) {
											JSONObject obj = array
													.getJSONObject(j);
											String picurl = obj
													.getString("picurl");
											String userid = obj.getString("userid");
											HistoryRouteUserInfo userinfo = new HistoryRouteUserInfo();
											userinfo.setPicurl(picurl);
											userinfo.setUserid(userid);
											picList.add(userinfo);
										}
										historyRouteList.setPicList(picList);
										list.add(historyRouteList);
									}
									if (list==null||list.size()==0) {
										tvNone.setVisibility(View.VISIBLE);
									}else {
										adapter.setData(list);
									}
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
						BaseApplication.toastMethod(HistoryRouteActivity.this,
								"", 0);
					}
				});
	}


	// 点击事件
	@OnClick(R.id.iv_back)
	public void click(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;

		default:
			break;
		}
	}

	/*************** 生命周期方法 ********************/
	@Override
	protected void onResume() {
		super.onResume();
		getHistoryRouteList();
	}

}
