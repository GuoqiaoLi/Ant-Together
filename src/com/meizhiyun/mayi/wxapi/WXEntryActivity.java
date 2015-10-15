package com.meizhiyun.mayi.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.meizhiyun.mayi.utils.Constant;
import com.meizhiyun.mayi.utils.PreferenceUtil;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	private IWXAPI api;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = WXAPIFactory.createWXAPI(this, Constant.WX_APP_ID, false);
		api.handleIntent(getIntent(), this);
	}

	// 微信发送请求到第三方应用时，会回调到该方法
	@Override
	public void onReq(BaseReq req) {
		finish();
	}

	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	@Override
	public void onResp(BaseResp resp) {
		String result = "";
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			result = "发送成功";
			Toast.makeText(this, result, Toast.LENGTH_LONG).show();
			if (TextUtils.isEmpty(PreferenceUtil.readString(WXEntryActivity.this, "wxlogin", "token"))) {
				String token = ((SendAuth.Resp) resp).token;
				PreferenceUtil.write(WXEntryActivity.this, "wxlogin", "token", token);
			}
			finish();
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = "发送取消";
			Toast.makeText(this, result, Toast.LENGTH_LONG).show();
			finish();
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = "发送被拒绝";
			Toast.makeText(this, result, Toast.LENGTH_LONG).show();
			finish();
			break;
		default:
			result = "发送返回";
			Toast.makeText(this, result, Toast.LENGTH_LONG).show();
			finish();
			break;
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
		finish();
	}
}
