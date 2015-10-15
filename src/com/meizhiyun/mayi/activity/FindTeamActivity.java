package com.meizhiyun.mayi.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

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
import com.meizhiyun.mayi.adapter.FindTeamAdapter;
import com.meizhiyun.mayi.bean.SearchedUserInfo;
import com.meizhiyun.mayi.bean.UrlConfig;
import com.meizhiyun.mayi.utils.JsonTools;
import com.meizhiyun.mayi.utils.PreferenceUtil;
import com.meizhiyun.mayi.utils.SetScreenSizeUtils;

/**
 * 
 * @类名称: FindTeamActivity
 * @类描述: 寻找活动的界面
 * @创建人：Guoqiao Li
 * @备注：
 * @version V1.0
 */
@ContentView(R.layout.find_team_activity_layout)
public class FindTeamActivity extends BaseActivity {
	@ViewInject(R.id.iv_back)
	private ImageView ivBack;
	@ViewInject(R.id.tv_title)
	private TextView tvTitle;
	@ViewInject(R.id.iv_find_search)
	private ImageView ivFindSearch;
	@ViewInject(R.id.iv_find_delete)
	private ImageView ivFindDelete;
	@ViewInject(R.id.et_find_search)
	private EditText etFindSearch;
	@ViewInject(R.id.tv_notice)
	private TextView tvNotice;
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
	private String userid;
	private List<SearchedUserInfo> list;
	private FindTeamAdapter adapter;
	@ViewInject(R.id.ll_no_network)
	private LinearLayout llNoNetwork;
	private boolean flag = true;
	private boolean hasNet;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (!hasNet) {
					llNoNetwork.setVisibility(View.VISIBLE);
				} else {
					llNoNetwork.setVisibility(View.GONE);
				}
				break;
			default:
				break;
			}
		};
	};

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
		etFindSearch.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(getCurrentFocus()
									.getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
					String nickname = etFindSearch.getText().toString().trim();
					if (!TextUtils.isEmpty(nickname)) {
						Intent intent = new Intent(FindTeamActivity.this,
								FindTeamResultActivity.class);
						intent.putExtra("nickname", nickname);
						startActivity(intent);
					} else {
						BaseApplication.toastMethod(FindTeamActivity.this,
								"昵称不能为空", 1);
					}
					return true;
				}
				return false;
			}
		});
		list = new ArrayList<SearchedUserInfo>();
		adapter = new FindTeamAdapter(FindTeamActivity.this, userid);
		lv.setAdapter(adapter);
		requestTogetherGroup();
	}

	// 初始化布局
	private void initLayout() {
		ivBack.setBackgroundResource(R.drawable.back_selector);
		tvTitle.setText("寻找组织");

		// 设置标题栏的高度
		LayoutParams params = rlTitleBar.getLayoutParams();
		int[] titleSizes = SetScreenSizeUtils.getTitleSize(this, 88);
		params.width = titleSizes[0];
		params.height = titleSizes[1];
		rlTitleBar.setLayoutParams(params);

		SetScreenSizeUtils.setViewSizeFromWidth(this, ivBack, 41, 66);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivFindSearch, 25, 28);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivFindDelete, 30, 30);
		SetScreenSizeUtils.setViewSizeFromWidth(this, tvNotice, 640, 67);
		SetScreenSizeUtils.setViewSizeFromWidth(this, llNoNetwork, 640, 70);
	}

	// 点击事件
	@OnClick({ R.id.iv_back, R.id.iv_find_delete, R.id.ll_no_network })
	public void click(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.iv_find_delete:
			etFindSearch.setText(null);
			break;
		case R.id.ll_no_network:
			// 跳转到系统的网络设置界面
			// 先判断当前系统版本
			Intent intent = new Intent(
					android.provider.Settings.ACTION_SETTINGS);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	// 请求一起同行过的好友的活动
	private void requestTogetherGroup() {
		llLoading.setVisibility(View.VISIBLE);
		animationDrawable.start();
		HttpUtils httpUtils = new HttpUtils(20000)
				.configCurrentHttpCacheExpiry(10000);
		RequestParams params = new RequestParams();
		params.addBodyParameter("action", "Friends");
		params.addBodyParameter("type", "1");
		params.addBodyParameter("userid", userid);
		httpUtils.send(HttpMethod.POST, UrlConfig.BASICURL, params,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						animationDrawable.stop();
						llLoading.setVisibility(View.GONE);
						if (!TextUtils.isEmpty(responseInfo.result)) {
							list = JsonTools
									.getTogetherFriend(responseInfo.result);
							if (list != null && list.size() > 0) {
								tvNone.setVisibility(View.GONE);
								adapter.setDataMethod(list);
							}else {
								tvNone.setVisibility(View.VISIBLE);
							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						animationDrawable.stop();
						llLoading.setVisibility(View.GONE);
						BaseApplication.toastMethod(FindTeamActivity.this, msg,
								0);
					}
				});
	}

	// 判断是否有网络
	private void judgeNetwork() {
		new Thread() {
			private int count = 0;

			@Override
			public void run() {
				super.run();
				while (flag) {
					if (count != 0 && count < 120) {
						SystemClock.sleep(5000);
					} else if (count != 0 && count >= 120) {
						SystemClock.sleep(10000);
					}
					hasNet = BaseApplication
							.judgeNetwork(FindTeamActivity.this);
					handler.sendEmptyMessage(1);
				}
			}
		}.start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		judgeNetwork();
	}
}
