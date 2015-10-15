package com.meizhiyun.mayi.activity;

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
import com.meizhiyun.mayi.adapter.InviteSearchUserAdapter;
import com.meizhiyun.mayi.bean.SearchedUserInfo;
import com.meizhiyun.mayi.bean.UrlConfig;
import com.meizhiyun.mayi.utils.JsonTools;
import com.meizhiyun.mayi.utils.PreferenceUtil;
import com.meizhiyun.mayi.utils.SetScreenSizeUtils;

/**
 * 
 * @类名称: InviteResultActivity
 * @类描述: 邀请组员搜索结果界面
 * @创建人：Guoqiao Li
 * @备注：
 * @version V1.0
 */
@ContentView(R.layout.search_result_activity_layout)
public class InviteResultActivity extends BaseActivity {
	@ViewInject(R.id.ll_title_bar)
	private LinearLayout llTitleBar;
	@ViewInject(R.id.ll_search_bar)
	private LinearLayout llSearchBar;
	@ViewInject(R.id.iv_back)
	private ImageView ivBack;
	@ViewInject(R.id.iv_invite_search)
	private ImageView ivInviteSearch;
	@ViewInject(R.id.iv_invite_delete)
	private ImageView ivInviteDelete;
	@ViewInject(R.id.et_invite_search)
	private EditText etInviteSearch;
	@ViewInject(R.id.lv)
	private ListView lv;
	@ViewInject(R.id.tv_none)
	private TextView tvNone;
	@ViewInject(R.id.ll_loading)
	private LinearLayout llLoading;
	@ViewInject(R.id.iv_loading)
	private ImageView ivLoading;
	@ViewInject(R.id.ll_no_network)
	private LinearLayout llNoNetwork;
	private String nickname;
	private String userid;
	private InviteSearchUserAdapter adapter;
	private AnimationDrawable animationDrawable;
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
		Intent intent = getIntent();
		nickname = intent.getStringExtra("nickname");
		etInviteSearch.setText(nickname);
		ivLoading.setBackgroundResource(R.drawable.loading_animation);
		animationDrawable = (AnimationDrawable) ivLoading.getBackground();
		searchFriend(nickname);
		etInviteSearch.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(getCurrentFocus()
									.getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
					String nickname = etInviteSearch.getText().toString()
							.trim();
					if (!TextUtils.isEmpty(nickname)) {
						searchFriend(nickname);
					} else {
						BaseApplication.toastMethod(InviteResultActivity.this,
								"昵称不能为空", 1);
					}
					return true;
				}
				return false;
			}
		});
		adapter = new InviteSearchUserAdapter(InviteResultActivity.this,
				InviteResultActivity.this, userid);
		lv.setAdapter(adapter);
	}

	// 初始化布局
	private void initLayout() {
		// 设置标题栏的高度
		LayoutParams params = llTitleBar.getLayoutParams();
		int[] titleSizes = SetScreenSizeUtils.getTitleSize(this, 88);
		params.width = titleSizes[0];
		params.height = titleSizes[1];
		llTitleBar.setLayoutParams(params);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivBack, 41, 66);

//		SetScreenSizeUtils.setViewSizeFromWidth(this, llSearchBar, 528, 54);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivInviteSearch, 25, 28);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivInviteDelete, 30, 30);
		SetScreenSizeUtils.setViewSizeFromWidth(this, llNoNetwork, 640, 70);
		
		// int fontSize = SetScreenSizeUtils.getFontSize(this, 32);
		// etInviteSearch.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
		// tvNone.setTextSize(TypedValue.COMPLEX_UNIT_SP,
		// SetScreenSizeUtils.getFontSize(this, 36));

		userid = PreferenceUtil.readString(this, "login", "userid");
	}

	// 点击事件
	@OnClick({ R.id.iv_back, R.id.iv_invite_delete, R.id.ll_no_network })
	public void click(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.iv_invite_delete:
			etInviteSearch.setText(null);
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

	// 搜索好友的方法
	private void searchFriend(String nickname) {
		llLoading.setVisibility(View.VISIBLE);
		animationDrawable.start();
		HttpUtils httpUtils = new HttpUtils(20000)
				.configCurrentHttpCacheExpiry(10000);
		RequestParams params = new RequestParams();
		params.addBodyParameter("action", "Search");
		params.addBodyParameter("userid", userid);
		params.addBodyParameter("nickname", nickname);
		httpUtils.send(HttpMethod.POST, UrlConfig.BASICURL, params,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (!TextUtils.isEmpty(responseInfo.result)) {
							animationDrawable.stop();
							llLoading.setVisibility(View.GONE);
							List<SearchedUserInfo> list = JsonTools
									.getSearchedUserInfo(responseInfo.result);
							if (list != null && list.size() > 0) {
								tvNone.setVisibility(View.GONE);
								adapter.setDataMethod(list);
							}else {
								tvNone.setVisibility(View.VISIBLE);
								BaseApplication.toastMethod(InviteResultActivity.this, "未搜索到此昵称", 1);
							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						animationDrawable.stop();
						llLoading.setVisibility(View.GONE);
						BaseApplication.toastMethod(InviteResultActivity.this,
								msg, 0);
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
							.judgeNetwork(InviteResultActivity.this);
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
