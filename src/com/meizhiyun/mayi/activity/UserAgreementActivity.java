package com.meizhiyun.mayi.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.meizhiyun.mayi.R;
import com.meizhiyun.mayi.utils.SetScreenSizeUtils;

/**
 * 
 * @类名称: UserAgreementActivity
 * @类描述: 用户协议界面
 * @创建人：LiXinYang
 * @备注：
 * @version V1.0
 */
@ContentView(R.layout.user_agreement_activity)
public class UserAgreementActivity extends BaseActivity {
	@ViewInject(R.id.iv_back)
	private ImageView ivBack;
	@ViewInject(R.id.tv_title)
	private TextView tvTitle;
	@ViewInject(R.id.rl_title_bar)
	private RelativeLayout rlTitleBar;

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
		tvTitle.setText("用户协议");
		// 设置标题栏的高度
		LayoutParams params = rlTitleBar.getLayoutParams();
		int[] titleSizes = SetScreenSizeUtils.getTitleSize(this, 88);
		params.width = titleSizes[0];
		params.height = titleSizes[1];
		rlTitleBar.setLayoutParams(params);

		SetScreenSizeUtils.setViewSizeFromWidth(this, ivBack, 41, 66);
	}
	
	//点击事件
	@OnClick(R.id.iv_back)
	public void click(View v){
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;

		default:
			break;
		}
	}
}
