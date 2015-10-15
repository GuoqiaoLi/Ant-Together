package com.meizhiyun.mayi.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
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
import com.meizhiyun.mayi.bean.UrlConfig;
import com.meizhiyun.mayi.utils.JudgeStringType;
import com.meizhiyun.mayi.utils.SetScreenSizeUtils;

/**
 * 
 * @类名称: ForgetPassActivity
 * @类描述: 忘记密码界面
 * @创建人：LiXinYang
 * @备注：
 * @version V1.0
 */

@ContentView(R.layout.forget_pass_activity)
public class ForgetPassActivity extends BaseActivity {

	@ViewInject(R.id.iv_back)
	private ImageView ivBack;
	@ViewInject(R.id.tv_title)
	private TextView tvTitle;
	@ViewInject(R.id.et_email)
	private EditText etEmail;
	@ViewInject(R.id.rl_title_bar)
	private RelativeLayout rlTitleBar;
	@ViewInject(R.id.ll_loading)
	private LinearLayout llLoading;
	@ViewInject(R.id.iv_loading)
	private ImageView ivLoading;
	private AnimationDrawable animationDrawable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		// 初始化信息
		init();
	}

	// 初始化信息
	private void init() {
		ivBack.setBackgroundResource(R.drawable.back_selector);
		tvTitle.setText("忘记密码");
		ivLoading.setBackgroundResource(R.drawable.loading_animation);
		animationDrawable = (AnimationDrawable) ivLoading.getBackground();

		// 设置标题栏的高度
		LayoutParams params = rlTitleBar.getLayoutParams();
		int[] titleSizes = SetScreenSizeUtils.getTitleSize(this, 88);
		params.width = titleSizes[0];
		params.height = titleSizes[1];
		rlTitleBar.setLayoutParams(params);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivBack, 41, 66);

		etEmail.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_GO) {
					((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(getCurrentFocus()
									.getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
					String email = etEmail.getText().toString().trim();
					if (!TextUtils.isEmpty(email)) {
						boolean checkEmail = JudgeStringType.checkEmail(email);
						if (checkEmail) {
							requestResetPass(email);
						} else {
							BaseApplication.toastMethod(
									ForgetPassActivity.this, "邮箱格式不正确", 1);
						}
					} else {
						BaseApplication.toastMethod(ForgetPassActivity.this,
								"邮箱不能为空", 1);
					}
					return true;
				}
				return false;
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

	// 请求重置密码
	private void requestResetPass(String email) {
		llLoading.setVisibility(View.VISIBLE);
		animationDrawable.start();
		HttpUtils httpUtils = new HttpUtils(20000)
				.configCurrentHttpCacheExpiry(10000);
		RequestParams params = new RequestParams();
		params.addBodyParameter("action", "ForgetPassword");
		params.addBodyParameter("username", email);
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
								if ("0".equals(result)) {
									BaseApplication.toastMethod(
											ForgetPassActivity.this,
											"请您前往您的邮箱修改密码", 1);
									ForgetPassActivity.this.finish();
								} else if ("1".equals(result)) {
									BaseApplication.toastMethod(
											ForgetPassActivity.this, "邮件发送失败",
											1);
								} else {
									BaseApplication.toastMethod(
											ForgetPassActivity.this,
											"您的邮箱未注册,请先注册", 1);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						BaseApplication.toastMethod(ForgetPassActivity.this,
								"", 0);
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
