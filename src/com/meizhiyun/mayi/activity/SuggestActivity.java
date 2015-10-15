package com.meizhiyun.mayi.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.meizhiyun.mayi.bean.UrlConfig;
import com.meizhiyun.mayi.utils.PreferenceUtil;
import com.meizhiyun.mayi.utils.SetScreenSizeUtils;

/**
 * 
 * @类名称: SuggestActivity
 * @类描述: 给我们建议的界面
 * @创建人：KevinLee
 * @备注：
 * @version V1.0
 */

@ContentView(R.layout.suggest_activity_layout)
public class SuggestActivity extends BaseActivity {
	@ViewInject(R.id.iv_back)
	private ImageView ivBack;
	@ViewInject(R.id.tv_title)
	private TextView tvTitle;
	@ViewInject(R.id.tv_text_count)
	private TextView tvTextCount;
	@ViewInject(R.id.et_text)
	private EditText etText;
	@ViewInject(R.id.et_contact_info)
	private EditText etContactInfo;
	@ViewInject(R.id.btn_submit)
	private Button btnSubmit;
	@ViewInject(R.id.ll_edit)
	private LinearLayout llEdit;
	@ViewInject(R.id.rl_title_bar)
	private RelativeLayout rlTitleBar;

	private int num = 200;
	private String userid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		// 初始化布局
		initLayout();
		// 初始化信息
		init();
	}

	// 初始化信息
	private void init() {
		userid = PreferenceUtil.readString(this, "login", "userid");
		etText.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				temp = s;
			}

			public void afterTextChanged(Editable s) {
				int number = num - s.length();
				tvTextCount.setText("可输入" + number + "字");
				selectionStart = etText.getSelectionStart();
				selectionEnd = etText.getSelectionEnd();
				if (temp.length() > num) {
					s.delete(selectionStart - 1, selectionEnd);
					int tempSelection = selectionEnd;
					etText.setText(s);
					etText.setSelection(tempSelection);// 设置光标在最后
				}
			}
		});
	}

	// 初始化布局
	private void initLayout() {
		ivBack.setBackgroundResource(R.drawable.back_selector);
		tvTitle.setText("意见反馈");
		// 设置标题栏的高度
		LayoutParams params = rlTitleBar.getLayoutParams();
		int[] titleSizes = SetScreenSizeUtils.getTitleSize(this, 88);
		params.width = titleSizes[0];
		params.height = titleSizes[1];
		rlTitleBar.setLayoutParams(params);
		SetScreenSizeUtils.setViewSizeFromWidth(this, ivBack, 41, 66);
		SetScreenSizeUtils.setViewSizeFromWidth(this, btnSubmit, 590, 81);
		SetScreenSizeUtils.setViewSizeFromWidth(this, llEdit, 590, 365);
	}
	
	//点击事件
	@OnClick({R.id.iv_back,R.id.btn_submit})
	public void click(View v){
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.btn_submit:
			submitMethod();
			break;

		default:
			break;
		}
	}

	//提交方法
	private void submitMethod() {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);   
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); 
		if (TextUtils.isEmpty(etText.getText().toString().trim())) {
			BaseApplication.toastMethod(this, "建议不可为空哦!", 1);
		}else if (TextUtils.isEmpty(etContactInfo.getText().toString().trim())) {
			BaseApplication.toastMethod(this, "联系方式不可为空哦!", 1);
		}
		HttpUtils httpUtils = new HttpUtils(20000).configCurrentHttpCacheExpiry(10000);
		RequestParams params = new RequestParams();
		params.addBodyParameter("action", "FeedBack");
		params.addBodyParameter("userid", userid);
		params.addBodyParameter("advice", etText.getText().toString().trim());
		params.addBodyParameter("link", etContactInfo.getText().toString().trim());
		httpUtils.send(HttpMethod.POST, UrlConfig.BASICURL, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				BaseApplication.toastMethod(SuggestActivity.this, "我们已经收到您的建议咯!", 1);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				
			}
		});
	}
	
}
