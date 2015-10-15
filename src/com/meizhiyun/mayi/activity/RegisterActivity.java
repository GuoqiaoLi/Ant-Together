package com.meizhiyun.mayi.activity;

import java.security.NoSuchAlgorithmException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.meizhiyun.mayi.bean.UrlConfig;
import com.meizhiyun.mayi.utils.JudgeStringType;
import com.meizhiyun.mayi.utils.MD5;
import com.meizhiyun.mayi.utils.PreferenceUtil;
import com.meizhiyun.mayi.utils.SetScreenSizeUtils;

/**
 * 
 * @类名称: RegisterActivity
 * @类描述: 注册界面
 * @创建人：Guoqiao Li
 * @备注：
 * @version V1.0
 */

@ContentView(R.layout.register_activity_layout)
public class RegisterActivity extends BaseActivity {
	@ViewInject(R.id.tv_account)
	private TextView tvAccount;
	@ViewInject(R.id.tv_had_account)
	private TextView tvHadAccount;
	@ViewInject(R.id.tv_password)
	private TextView tvPassword;
	@ViewInject(R.id.tv_nickname)
	private TextView tvNickname;
	@ViewInject(R.id.et_account)
	private EditText etAccount;
	@ViewInject(R.id.et_password)
	private EditText etPassword;
	@ViewInject(R.id.et_nickname)
	private EditText etNickname;
	@ViewInject(R.id.btn_register)
	private Button btnRegister;
	@ViewInject(R.id.ll_loading)
	private LinearLayout llLoading;
	@ViewInject(R.id.iv_loading)
	private ImageView ivLoading;
	private AnimationDrawable animationDrawable;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化ViewUtils
		ViewUtils.inject(this);
		// 初始化布局
		initLayout();
	}

	// 初始化布局
	private void initLayout() {
		SetScreenSizeUtils.setViewSizeFromWidth(this, btnRegister, 473, 75);
		ivLoading.setBackgroundResource(R.drawable.loading_animation);
		animationDrawable = (AnimationDrawable) ivLoading.getBackground();
	}

	// 点击事件
	@OnClick({ R.id.btn_register, R.id.tv_had_account })
	public void click(View v) {
		switch (v.getId()) {
		case R.id.btn_register:
			registerMethod();
			break;
		case R.id.tv_had_account:
			finish();
			break;
		default:
			break;
		}
	}

	// 注册普通账号的方法
	private void registerMethod() {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);   
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); 
		String account = etAccount.getText().toString().trim();
		String password = etPassword.getText().toString().trim();
		String nickname = etNickname.getText().toString().trim();
		if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)
				&& !TextUtils.isEmpty(nickname)) {
			if (JudgeStringType.checkEmail(account)) {
				if (password.length()>6) {
					StringBuffer buffer;
					try {
						buffer = MD5.getMD5(password);
						registerRequest(account,buffer.toString(),nickname);
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					}
				}else {
					BaseApplication.toastMethod(this, "密码长度不可小于6位", 1);
				}
			}else {
				BaseApplication.toastMethod(this, "邮箱格式不正确", 1);
			}
		} else if (TextUtils.isEmpty(account)) {
			BaseApplication.toastMethod(this, "邮箱不可为空", 1);
		} else if (TextUtils.isEmpty(password)) {
			BaseApplication.toastMethod(this, "密码不可为空", 1);
		} else if (TextUtils.isEmpty(nickname)) {
			BaseApplication.toastMethod(this, "昵称不可为空", 1);
		}
	}

	//用户信息注册请求
	private void registerRequest(String username,String password,final String nickname) {
		llLoading.setVisibility(View.VISIBLE);
		animationDrawable.start();
		HttpUtils httpUtils = new HttpUtils(20000).configCurrentHttpCacheExpiry(10000);
		RequestParams params = new RequestParams();
		params.addBodyParameter("action", "Register");
		params.addBodyParameter("username", username);
		params.addBodyParameter("password", password);
		params.addBodyParameter("nickname", nickname);
		httpUtils.send(HttpMethod.POST, UrlConfig.BASICURL, params, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				animationDrawable.stop();
				llLoading.setVisibility(View.GONE);
				if (!TextUtils.isEmpty(responseInfo.result)) {
					try {
						JSONObject jsonObject = new JSONObject(responseInfo.result);
						String result = jsonObject.getString("result");
						if (!TextUtils.isEmpty(result)&&"success".equals(result)) {
							String userid = jsonObject.getString("userid");
							PreferenceUtil.write(RegisterActivity.this, "login", "userid", userid);
							BaseApplication.toastMethod(RegisterActivity.this, "注册成功,请前往邮箱激活该账号", 1);
							Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
							startActivity(intent);
							finish();
						}else if (!TextUtils.isEmpty(result)&&"fail".equals(result)) {
							BaseApplication.toastMethod(RegisterActivity.this, "此邮箱已注册", 1);
						}else if (!TextUtils.isEmpty(result)&&"error".equals(result)) {
							BaseApplication.toastMethod(RegisterActivity.this, "验证邮件发送失败，请查看邮箱地址是否正确", 1);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				BaseApplication.toastMethod(RegisterActivity.this, msg, 0);
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		animationDrawable.stop();
		llLoading.setVisibility(View.GONE);
	}
	
}
