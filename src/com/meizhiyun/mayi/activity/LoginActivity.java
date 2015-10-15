package com.meizhiyun.mayi.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.meizhiyun.mayi.bean.UrlConfig;
import com.meizhiyun.mayi.utils.AccessTokenKeeper;
import com.meizhiyun.mayi.utils.Constant;
import com.meizhiyun.mayi.utils.JudgeStringType;
import com.meizhiyun.mayi.utils.MD5;
import com.meizhiyun.mayi.utils.PreferenceUtil;
import com.meizhiyun.mayi.utils.SetScreenSizeUtils;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * 
 * @类名称: LoginActivity
 * @类描述: 登录界面
 * @创建人：LiXinYang
 * @备注：
 * @version V1.0
 */

@ContentView(R.layout.login_activity_layout)
public class LoginActivity extends Activity {
	@ViewInject(R.id.qq_login)
	private ImageView ivQQLogin;
	@ViewInject(R.id.wechat_login)
	private ImageView ivWechatLogin;
	@ViewInject(R.id.weibo_login)
	private ImageView ivWeiboLogin;
	@ViewInject(R.id.et_account)
	private EditText etAccount;
	@ViewInject(R.id.et_password)
	private EditText etPassword;
	@ViewInject(R.id.btn_login)
	private Button btnLogin;
	@ViewInject(R.id.tv_register)
	private TextView tvRegister;
	@ViewInject(R.id.tv_vertical_line)
	private TextView tvVerticalLine;
	@ViewInject(R.id.tv_forget)
	private TextView tvForget;
	@ViewInject(R.id.rl)
	private RelativeLayout rl;
	@ViewInject(R.id.ll_loading)
	private LinearLayout llLoading;
	@ViewInject(R.id.iv_loading)
	private ImageView ivLoading;
	private String picurl = "";
	// 微博sso授权后的用户信息
	private AuthInfo weiboAuthInfo;
	/** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能 */
	private Oauth2AccessToken mAccessToken;

	/** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
	private SsoHandler mSsoHandler;
	private long uid;
	public static IWXAPI WXapi;
	private static String get_access_token = "";
	// 获取第一步的code后，请求以下链接获取access_token
	public static String GetCodeRequest = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
	// 获取用户个人信息
	public static String GetUserInfo = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
	public static Tencent mTencent;

	private String logintype = null;
	private AnimationDrawable animationDrawable;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				llLoading.setVisibility(View.VISIBLE);
				animationDrawable.start();
				break;
			case 1:
				animationDrawable.stop();
				llLoading.setVisibility(View.GONE);
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		// 初始化ViewUtils
		ViewUtils.inject(this);
		// 初始化信息
		init();
		// 初始化布局
		initLayout();
	}

	// 初始化信息
	private void init() {
		// 快速授权时，请不要传入 SCOPE，否则可能会授权不成功
		weiboAuthInfo = new AuthInfo(this, Constant.APP_KEY,
				Constant.REDIRECT_URL, Constant.SCOPE);
		mSsoHandler = new SsoHandler(this, weiboAuthInfo);
		if (mTencent == null) {
			mTencent = Tencent.createInstance(Constant.QQ_APP_ID, this);
		}
		PreferenceUtil.write(this, "wxlogin", "token", "");

		ivLoading.setBackgroundResource(R.drawable.loading_animation);
		animationDrawable = (AnimationDrawable) ivLoading.getBackground();
	}

	// 初始化布局
	private void initLayout() {
		// rl.setBackgroundResource(R.drawable.dise);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivQQLogin, 134, 134);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivWechatLogin, 134, 134);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivWeiboLogin, 134, 134);
		SetScreenSizeUtils.setViewSizeFromHeight(this, btnLogin, 473, 75);
		SetScreenSizeUtils.setViewSizeFromWidth(this, tvRegister, 319, 60);
		SetScreenSizeUtils.setViewSizeFromWidth(this, tvForget, 319, 60);
		SetScreenSizeUtils.setViewSizeFromHeight(this, tvVerticalLine, 1, 60);
	}

	// 点击事件
	@OnClick({ R.id.qq_login, R.id.weibo_login, R.id.wechat_login,
			R.id.tv_register, R.id.tv_forget, R.id.btn_login })
	public void click(View v) {
		Intent intent;
		switch (v.getId()) {
		// 微博登录
		case R.id.weibo_login:
			// 微博第三方登录
			logintype = "1";
			weiboLoginMethod();
			break;
		// QQ登录
		case R.id.qq_login:
			logintype = "3";
			QQLoginMethod();
			break;
		// 微信登录
		case R.id.wechat_login:
			logintype = "2";
			WXLogin();
			break;
		// 注册
		case R.id.tv_register:
			intent = new Intent(this, RegisterActivity.class);
			startActivity(intent);
			break;
		// 忘记密码
		case R.id.tv_forget:
			intent = new Intent(this, ForgetPassActivity.class);
			startActivity(intent);
			break;
		// 普通登录
		case R.id.btn_login:
			loginMethod();
			break;
		default:
			break;
		}
	}

	/************************* 普通登录的方法 **************************************/
	private void loginMethod() {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);   
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); 
		String account = etAccount.getText().toString().trim();
		String password = etPassword.getText().toString().trim();
		if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {
			if (JudgeStringType.checkEmail(account)) {
				try {
					StringBuffer buffer = MD5.getMD5(password);
					loginRequest(account, buffer.toString());
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
			} else {
				BaseApplication.toastMethod(this, "账号请输入邮箱格式", 1);
			}
		} else {
			BaseApplication.toastMethod(this, "账号和密码不可为空", 1);
		}

	}

	// 普通登录的网络请求
	private void loginRequest(final String username, final String password) {
		llLoading.setVisibility(View.VISIBLE);
		animationDrawable.start();
		HttpUtils httpUtils = new HttpUtils(20000)
				.configCurrentHttpCacheExpiry(10000);
		RequestParams params = new RequestParams();
		params.addBodyParameter("action", "Login");
		params.addBodyParameter("logintype", "0");
		params.addBodyParameter("username", username);
		params.addBodyParameter("password", password);
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
								String result = jsonObject.getString("result");
								if (!TextUtils.isEmpty(result)
										&& "0".equals(result)) {
									String userid = jsonObject
											.getString("userid");
									String isactive = jsonObject
											.getString("isactive");
									String iscaptain = jsonObject
											.getString("iscaptain");
									String isview = jsonObject
											.getString("isview");
									String isshare = jsonObject
											.getString("isshare");
									String nickname = jsonObject
											.getString("nickname");
									if (!TextUtils.isEmpty(userid)
											&& !TextUtils.isEmpty(isactive)
											&& !TextUtils.isEmpty(iscaptain)) {
										PreferenceUtil.write(
												LoginActivity.this, "login",
												"username", username);
										PreferenceUtil.write(
												LoginActivity.this, "login",
												"password", password);
										PreferenceUtil.write(
												LoginActivity.this, "login",
												"nickname", nickname);
										PreferenceUtil.write(
												LoginActivity.this, "login",
												"hasLogined", true);
										PreferenceUtil.write(
												LoginActivity.this, "login",
												"userid", userid);
										PreferenceUtil.write(
												LoginActivity.this, "login",
												"isactive", isactive);
										PreferenceUtil.write(
												LoginActivity.this, "login",
												"iscaptain", iscaptain);
										PreferenceUtil.write(
												LoginActivity.this, "login",
												"isview", isview);
										PreferenceUtil.write(
												LoginActivity.this, "login",
												"isshare", isshare);
										PreferenceUtil.write(
												LoginActivity.this, "login",
												"logintype", logintype);
										PreferenceUtil.write(
												LoginActivity.this, "login",
												"isAuto", true);
										PreferenceUtil.write(
												LoginActivity.this, "login",
												"picurl", picurl);
										Intent intent = new Intent(
												LoginActivity.this,
												MainActivity.class);
										startActivity(intent);
										finish();
									}
								} else if (!TextUtils.isEmpty(result)
										&& "1".equals(result)) {
									BaseApplication.toastMethod(
											LoginActivity.this, "账号或密码不正确", 1);
								} else if (!TextUtils.isEmpty(result)
										&& "2".equals(result)) {
									BaseApplication.toastMethod(
											LoginActivity.this,
											"邮箱未激活,请前往邮箱激活该账号", 1);
								} else if ("3".equals(result)) {
									BaseApplication
											.toastMethod(LoginActivity.this,
													"登录出错,请重新登录", 1);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						BaseApplication.toastMethod(LoginActivity.this, msg, 0);
					}
				});
	}

	/************************ QQ登录的方法 *******************************/

	private void QQLoginMethod() {
		if (!mTencent.isSessionValid()) {
			mTencent.login(this, "all", loginListener);
		}
		llLoading.setVisibility(View.VISIBLE);
		animationDrawable.start();
	}

	IUiListener loginListener = new BaseUiListener() {
		@Override
		protected void doComplete(JSONObject values) {
			animationDrawable.stop();
			llLoading.setVisibility(View.GONE);
			initOpenidAndToken(values);
		}
	};

	public void initOpenidAndToken(JSONObject jsonObject) {
		try {
			String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
			String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
			final String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
			if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
					&& !TextUtils.isEmpty(openId)) {
				mTencent.setAccessToken(token, expires);
				mTencent.setOpenId(openId);
				QQToken qqToken = mTencent.getQQToken();
				UserInfo info = new UserInfo(getApplicationContext(), qqToken);
				info.getUserInfo(new IUiListener() {

					@Override
					public void onError(UiError arg0) {
						
					}

					@Override
					public void onComplete(Object response) {
						JSONObject object = (JSONObject) response;
						try {
							String picurl = object.getString("figureurl_qq_1");
							String nickname = object.getString("nickname");
							openLoginMethod(nickname, openId, picurl);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onCancel() {

					}
				});
			}
		} catch (Exception e) {
		}
	}

	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
			if (null == response) {
				BaseApplication.toastMethod(LoginActivity.this, "登录失败", 1);
				return;
			}
			JSONObject jsonResponse = (JSONObject) response;
			if (null != jsonResponse && jsonResponse.length() == 0) {
				BaseApplication.toastMethod(LoginActivity.this, "登录失败", 1);
				return;
			} else {
				BaseApplication.toastMethod(LoginActivity.this, "登录成功", 1);
			}
			doComplete((JSONObject) response);
		}

		protected void doComplete(JSONObject values) {
		}

		@Override
		public void onError(UiError e) {
			BaseApplication.toastMethod(LoginActivity.this, "登录出错"
					+ e.errorDetail, 1);
		}

		@Override
		public void onCancel() {
			BaseApplication.toastMethod(LoginActivity.this, "登录取消", 1);
		}
	}

	/********************** 微信登录 *****************************/

	/**
	 * 登录微信
	 */
	private void WXLogin() {
		WXapi = WXAPIFactory.createWXAPI(this, Constant.WX_APP_ID, true);
		WXapi.registerApp(Constant.WX_APP_ID);
		SendAuth.Req req = new SendAuth.Req();
		req.scope = "snsapi_userinfo";
		req.state = "wechat_sdk_demo";
		WXapi.sendReq(req);
		llLoading.setVisibility(View.VISIBLE);
		animationDrawable.start();
	}

	/**
	 * 获取access_token的URL（微信）
	 * 
	 * @param code
	 *            授权时，微信回调给的
	 * @return URL
	 */
	public String getCodeRequest(String code) {
		animationDrawable.stop();
		llLoading.setVisibility(View.GONE);
		String result = null;
		GetCodeRequest = GetCodeRequest.replace("APPID",
				urlEnodeUTF8(Constant.WX_APP_ID));
		GetCodeRequest = GetCodeRequest.replace("SECRET",
				urlEnodeUTF8(Constant.WX_APP_SECRET));
		GetCodeRequest = GetCodeRequest.replace("CODE", urlEnodeUTF8(code));
		result = GetCodeRequest;
		return result;
	}

	/**
	 * 获取用户个人信息的URL（微信）
	 * 
	 * @param access_token
	 *            获取access_token时给的
	 * @param openid
	 *            获取access_token时给的
	 * @return URL
	 */
	public static String getUserInfo(String access_token, String openid) {
		String result = null;
		GetUserInfo = GetUserInfo.replace("ACCESS_TOKEN",
				urlEnodeUTF8(access_token));
		GetUserInfo = GetUserInfo.replace("OPENID", urlEnodeUTF8(openid));
		result = GetUserInfo;
		return result;
	}

	public static String urlEnodeUTF8(String str) {
		String result = str;
		try {
			result = URLEncoder.encode(str, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public Runnable downloadRun = new Runnable() {

		@Override
		public void run() {
			WXGetAccessToken();
		}
	};

	/**
	 * 获取access_token等等的信息(微信)
	 */
	private void WXGetAccessToken() {
		HttpClient get_access_token_httpClient = new DefaultHttpClient();
		String access_token = "";
		String openid = "";
		try {
			HttpPost postMethod = new HttpPost(get_access_token);
			HttpResponse response = get_access_token_httpClient
					.execute(postMethod); // 执行POST方法
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStream is = response.getEntity().getContent();
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				String str = "";
				StringBuffer sb = new StringBuffer();
				while ((str = br.readLine()) != null) {
					sb.append(str);
				}
				is.close();
				String json = sb.toString();
				JSONObject jsonObject = new JSONObject(json);
				access_token = jsonObject.getString("access_token");
				openid = jsonObject.getString("openid");
				String get_user_info_url = getUserInfo(access_token, openid);
				WXGetUserInfo(get_user_info_url);
			} else {

			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取微信用户个人信息
	 * 
	 * @param get_user_info_url
	 *            调用URL
	 */
	private void WXGetUserInfo(String get_user_info_url) {
		HttpClient get_access_token_httpClient = new DefaultHttpClient();
		String openid = "";
		String nickname = "";
		try {
			HttpGet getMethod = new HttpGet(get_user_info_url);
			HttpResponse response = get_access_token_httpClient
					.execute(getMethod); // 执行GET方法
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStream is = response.getEntity().getContent();
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				String str = "";
				StringBuffer sb = new StringBuffer();
				while ((str = br.readLine()) != null) {
					sb.append(str);
				}
				is.close();
				String json = sb.toString();
				JSONObject jsonObject = new JSONObject(json);
				openid = jsonObject.getString("openid");
				nickname = jsonObject.getString("nickname");
				picurl = jsonObject.getString("headimgurl");
				openLoginMethod(nickname, openid, picurl);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/***************************** 微博登录 *********************************/

	// 微博登录的方法
	private void weiboLoginMethod() {
		mSsoHandler.authorize(new AuthListener());
		llLoading.setVisibility(View.VISIBLE);
		animationDrawable.start();
	}

	/**
	 * 微博认证授权回调类。 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用
	 * {@link SsoHandler#authorizeCallBack} 后， 该回调才会被执行。 2. 非 SSO
	 * 授权时，当授权结束后，该回调就会被执行。 当授权成功后，请保存该 access_token、expires_in、uid 等信息到
	 * SharedPreferences 中。
	 */
	class AuthListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			animationDrawable.stop();
			llLoading.setVisibility(View.GONE);
			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.removeAllCookie();
			// 从 Bundle 中解析 Token
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			if (mAccessToken.isSessionValid()) {
				// 保存 Token 到 SharedPreferences
				AccessTokenKeeper.writeAccessToken(LoginActivity.this,
						mAccessToken);
				UsersAPI usersAPI = new UsersAPI(LoginActivity.this,
						Constant.APP_KEY, mAccessToken);
				uid = Long.parseLong(mAccessToken.getUid());
				// 获取微博用户信息
				usersAPI.show(uid, mListener);
				Toast.makeText(LoginActivity.this,
						R.string.weibosdk_demo_toast_auth_success,
						Toast.LENGTH_SHORT).show();
			} else {
				// 以下几种情况，您会收到 Code：
				// 1. 当您未在平台上注册的应用程序的包名与签名时；
				// 2. 当您注册的应用程序包名与签名不正确时；
				// 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
				String code = values.getString("code");
				String message = getString(R.string.weibosdk_demo_toast_auth_failed);
				if (!TextUtils.isEmpty(code)) {
					message = message + "\nObtained the code: " + code;
				}
				Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG)
						.show();
			}
		}

		@Override
		public void onCancel() {
			animationDrawable.stop();
			llLoading.setVisibility(View.GONE);
			Toast.makeText(LoginActivity.this,
					R.string.weibosdk_demo_toast_auth_canceled,
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			animationDrawable.stop();
			llLoading.setVisibility(View.GONE);
			Toast.makeText(LoginActivity.this,
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}
	}

	// 获取用户信息的回调接口
	private RequestListener mListener = new RequestListener() {

		@Override
		public void onWeiboException(WeiboException msg) {
			BaseApplication
					.toastMethod(LoginActivity.this, msg.getMessage(), 1);
		}

		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
				User user = User.parse(response);
				String name = user.name;
				picurl = user.avatar_hd;
				if (BaseApplication.judgeNetwork(LoginActivity.this)) {
					openLoginMethod(name, String.valueOf(uid), picurl);
				}
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// qq登录回调信息
		if (requestCode == Constants.REQUEST_API) {
			if (resultCode == Constants.RESULT_LOGIN) {
				Tencent.handleResultData(data, loginListener);
			}
		} else if (requestCode == Constants.REQUEST_APPBAR) { // app内应用吧登录
			if (resultCode == Constants.RESULT_LOGIN) {
				BaseApplication.toastMethod(LoginActivity.this,
						"登录成功" + data.getStringExtra(Constants.LOGIN_INFO), 1);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	// 三方登录的方法
	private void openLoginMethod(final String nickname, final String uid,
			final String picurl) {
		handler.sendEmptyMessage(0);
		RequestParams params = new RequestParams("utf-8");
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("action", "Login"));
		list.add(new BasicNameValuePair("logintype", "1"));
		list.add(new BasicNameValuePair("nickname", nickname));
		list.add(new BasicNameValuePair("openid", uid));
		list.add(new BasicNameValuePair("picurl", picurl));
		params.addBodyParameter(list);
		HttpUtils httpUtils = new HttpUtils(20000)
				.configCurrentHttpCacheExpiry(10000);
		httpUtils.send(HttpMethod.POST, UrlConfig.BASICURL, params,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (!TextUtils.isEmpty(responseInfo.result)) {
							try {
								JSONObject jsonObject = new JSONObject(
										responseInfo.result);
								String result = jsonObject.getString("result");
								if ("0".equals(result)) {
									String userid = jsonObject
											.getString("userid");
									String isactive = jsonObject
											.getString("isactive");
									String iscaptain = jsonObject
											.getString("iscaptain");
									String isview = jsonObject
											.getString("isview");
									String isshare = jsonObject
											.getString("isshare");
									if (!TextUtils.isEmpty(userid)
											&& !TextUtils.isEmpty(isactive)
											&& !TextUtils.isEmpty(iscaptain)) {
										PreferenceUtil.write(
												LoginActivity.this, "login",
												"userid", userid);
										PreferenceUtil.write(
												LoginActivity.this, "login",
												"hasLogined", true);
										PreferenceUtil.write(
												LoginActivity.this, "login",
												"nickname", nickname);
										PreferenceUtil.write(
												LoginActivity.this, "login",
												"isactive", isactive);
										PreferenceUtil.write(
												LoginActivity.this, "login",
												"iscaptain", iscaptain);
										PreferenceUtil.write(
												LoginActivity.this, "login",
												"isview", isview);
										PreferenceUtil.write(
												LoginActivity.this, "login",
												"isshare", isshare);
										PreferenceUtil.write(
												LoginActivity.this, "login",
												"openid", uid);
										PreferenceUtil.write(
												LoginActivity.this, "login",
												"logintype", logintype);
										PreferenceUtil.write(
												LoginActivity.this, "login",
												"isAuto", true);
										PreferenceUtil.write(
												LoginActivity.this, "login",
												"picurl", picurl);
										Intent intent = new Intent(
												LoginActivity.this,
												MainActivity.class);
										startActivity(intent);
										finish();
									}
								} else if ("3".equals(result)) {
									BaseApplication
											.toastMethod(LoginActivity.this,
													"登录出错,请重新登录", 1);
								}
								handler.sendEmptyMessage(1);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						BaseApplication.toastMethod(LoginActivity.this, msg, 0);
					}
				});

	}

	/******************** 生命周期方法 ************************/
	@Override
	protected void onResume() {
		super.onResume();
		/*
		 * resp是你保存在全局变量中的
		 */
		String code = PreferenceUtil.readString(LoginActivity.this, "wxlogin",
				"token");
		if (!TextUtils.isEmpty(code)) {
			get_access_token = getCodeRequest(code);
			Thread thread = new Thread(downloadRun);
			thread.start();
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (ivLoading != null && animationDrawable != null
				&& animationDrawable.isRunning()
				&& animationDrawable.isVisible()) {
			animationDrawable.stop();
			llLoading.setVisibility(View.GONE);
		}
	}

}