package com.meizhiyun.mayi.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
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
import com.meizhiyun.mayi.adapter.InviteFriendAdapter;
import com.meizhiyun.mayi.bean.SearchedUserInfo;
import com.meizhiyun.mayi.bean.UrlConfig;
import com.meizhiyun.mayi.utils.JsonTools;
import com.meizhiyun.mayi.utils.PreferenceUtil;
import com.meizhiyun.mayi.utils.SetScreenSizeUtils;
import com.meizhiyun.mayi.utils.WxUtils;
import com.meizhiyun.mayi.view.HorizontalListView;
import com.tencent.mm.sdk.openapi.SendMessageToWX;

/**
 * 
 * @类名称: InviteActivity
 * @类描述: 邀请界面
 * @创建人：Guoqiao Li
 * @备注：
 * @version V1.0
 */

@ContentView(R.layout.invite_activity_layout)
public class InviteActivity extends BaseActivity {
	@ViewInject(R.id.iv_back)
	private ImageView ivBack;
	@ViewInject(R.id.tv_title)
	private TextView tvTitle;
	@ViewInject(R.id.iv_invite_search)
	private ImageView ivInviteSearch;
	@ViewInject(R.id.iv_invite_delete)
	private ImageView ivInviteDelete;
	@ViewInject(R.id.et_invite_search)
	private EditText etInviteSearch;
	@ViewInject(R.id.tv_tongguo)
	private TextView tvTongguo;
	@ViewInject(R.id.tv_history_friend)
	private TextView tvHistoryFriend;
	@ViewInject(R.id.rl_title_bar)
	private RelativeLayout rlTitleBar;
	@ViewInject(R.id.ll_search_bar)
	private LinearLayout llSearchBar;
	@ViewInject(R.id.invite_from_wechat)
	private LinearLayout llInviteWechat;
	@ViewInject(R.id.invite_from_wechat_momment)
	private LinearLayout llInviteWechatMomment;
	@ViewInject(R.id.invite_from_book)
	private LinearLayout llInviteBook;
	@ViewInject(R.id.iv_wechat)
	private ImageView ivWechat;
	@ViewInject(R.id.iv_wechat_momment)
	private ImageView ivWechatMomment;
	@ViewInject(R.id.iv_book)
	private ImageView ivBook;
	@ViewInject(R.id.tv_wechat)
	private TextView tvWechat;
	@ViewInject(R.id.tv_wechat_momment)
	private TextView tvWechatMomment;
	@ViewInject(R.id.tv_book)
	private TextView tvBook;
	@ViewInject(R.id.tv_none)
	private TextView tvNone;
	@ViewInject(R.id.ll_loading)
	private LinearLayout llLoading;
	@ViewInject(R.id.iv_loading)
	private ImageView ivLoading;
	@ViewInject(R.id.ll_no_network)
	private LinearLayout llNoNetwork;
	@ViewInject(R.id.hlv)
	private HorizontalListView hlv;
	private String userid;
	private List<SearchedUserInfo> list;
	private InviteFriendAdapter adapter;
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
		WxUtils.registWxApi(this);
		userid = PreferenceUtil.readString(this, "login", "userid");
		ivLoading.setBackgroundResource(R.drawable.loading_animation);
		animationDrawable = (AnimationDrawable) ivLoading.getBackground();
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
						Intent intent = new Intent(InviteActivity.this,
								InviteResultActivity.class);
						intent.putExtra("nickname", nickname);
						startActivity(intent);
					} else {
						BaseApplication.toastMethod(InviteActivity.this,
								"昵称不能为空", 1);
					}
					return true;
				}
				return false;
			}
		});
		list = new ArrayList<SearchedUserInfo>();
		requestTogetherFriend();
		adapter = new InviteFriendAdapter(this, userid);
		hlv.setAdapter(adapter);
	}

	// 初始化布局
	private void initLayout() {
		ivBack.setBackgroundResource(R.drawable.back_selector);
		tvTitle.setText("邀请朋友");

		// 设置标题栏的高度
		LayoutParams params = rlTitleBar.getLayoutParams();
		int[] titleSizes = SetScreenSizeUtils.getTitleSize(this, 88);
		params.width = titleSizes[0];
		params.height = titleSizes[1];
		rlTitleBar.setLayoutParams(params);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivBack, 41, 66);
		SetScreenSizeUtils.setViewSizeFromWidth(this, llNoNetwork, 640, 70);

		SetScreenSizeUtils.setViewSizeFromWidth(this, ivInviteSearch, 25, 28);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivInviteDelete, 30, 30);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivWechat, 60, 60);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivWechatMomment, 60, 60);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivBook, 60, 60);
		SetScreenSizeUtils.setViewSizeFromWidth(this, tvTongguo, 640, 67);
		SetScreenSizeUtils.setViewSizeFromWidth(this, tvHistoryFriend, 640, 67);
		SetScreenSizeUtils.setViewSizeFromWidth(this, llInviteWechat, 640, 100);
		SetScreenSizeUtils.setViewSizeFromWidth(this, llInviteWechatMomment,
				640, 100);
		SetScreenSizeUtils.setViewSizeFromWidth(this, llInviteBook, 640, 100);
	}

	// 点击事件
	@OnClick({ R.id.iv_back, R.id.invite_from_wechat,
			R.id.invite_from_wechat_momment, R.id.invite_from_book,
			R.id.iv_invite_delete, R.id.ll_no_network })
	public void click(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.invite_from_wechat:
			// 分享给微信好友
			sendToWxFriend();
			break;
		// case R.id.invite_from_qq:
		//
		// break;
		case R.id.invite_from_wechat_momment:
			// 分享到微信朋友圈
			sendToWxMomment();
			break;
		case R.id.invite_from_book:
			// 使用短信分享
			sendSMS();
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

	// 请求一起同行过的好友列表
	private void requestTogetherFriend() {
		llLoading.setVisibility(View.VISIBLE);
		animationDrawable.start();
		HttpUtils httpUtils = new HttpUtils(20000)
				.configCurrentHttpCacheExpiry(10000);
		RequestParams params = new RequestParams();
		params.addBodyParameter("action", "Friends");
		params.addBodyParameter("type", "0");
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
								adapter.setData(list);
							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						animationDrawable.stop();
						llLoading.setVisibility(View.GONE);
						BaseApplication
								.toastMethod(InviteActivity.this, msg, 0);
					}
				});
	}

	// 分享到微信朋友圈
	private void sendToWxMomment() {
		llLoading.setVisibility(View.VISIBLE);
		animationDrawable.start();
		String text = "世界这么大，走丢了怎么办？\nsharing location with me";
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.share_logo_120);
		WxUtils.sendWebPageWx("http://www.mezyun.com/share.html", "蚂蚁聚聚", text, bitmap,
				120, 120, SendMessageToWX.Req.WXSceneTimeline);
	}

	// 分享到微信好友
	private void sendToWxFriend() {
		llLoading.setVisibility(View.VISIBLE);
		animationDrawable.start();
		String text = "世界这么大，走丢了怎么办？\nsharing location with me";
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.share_logo_120);
		WxUtils.sendWebPageWx("http://www.mezyun.com/share.html", "蚂蚁聚聚", text, bitmap,
				120, 120, SendMessageToWX.Req.WXSceneSession);
	}

	// 发短信分享
	private void sendSMS() {
		Uri smsToUri = Uri.parse("smsto:");
		Intent sendIntent = new Intent(Intent.ACTION_VIEW, smsToUri);
		sendIntent
				.putExtra("sms_body",
						"世界这么大，走丢了怎么办？\nsharing location with me\nhttp://www.mezyun.com/share.html");
		sendIntent.setType("vnd.android-dir/mms-sms");
		startActivityForResult(sendIntent, 1002);
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
					hasNet = BaseApplication.judgeNetwork(InviteActivity.this);
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

	@Override
	protected void onPause() {
		super.onPause();
		animationDrawable.stop();
		llLoading.setVisibility(View.GONE);
	}

}
