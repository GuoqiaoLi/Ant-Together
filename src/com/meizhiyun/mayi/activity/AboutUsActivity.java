package com.meizhiyun.mayi.activity;

import android.content.Intent;
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
 * @类名称: AboutUsActivity
 * @类描述: 关于我们界面
 * @创建人：Guoqiao Li
 * @备注：
 * @version V1.0
 */

@ContentView(R.layout.about_us)
public class AboutUsActivity extends BaseActivity {

	@ViewInject(R.id.iv_back)
	private ImageView ivBack;
	@ViewInject(R.id.tv_title)
	private TextView tvTitle;
	@ViewInject(R.id.tv_user_agreement)
	private TextView tvUserAgreement;
	@ViewInject(R.id.rl_title_bar)
	private RelativeLayout rlTitleBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		init();
	}

	// 初始化信息
	private void init() {
		ivBack.setBackgroundResource(R.drawable.back_selector);
		tvTitle.setText("关于我们");
		// 设置标题栏的高度
		LayoutParams params = rlTitleBar.getLayoutParams();
		int[] titleSizes = SetScreenSizeUtils.getTitleSize(this, 88);
		params.width = titleSizes[0];
		params.height = titleSizes[1];
		rlTitleBar.setLayoutParams(params);

		SetScreenSizeUtils.setViewSizeFromWidth(this, ivBack, 41, 66);
	}

	// 点击事件
	@OnClick({ R.id.tv_user_agreement, R.id.iv_back })
	public void click(View v) {
		switch (v.getId()) {
		case R.id.tv_user_agreement:
			Intent intent = new Intent(this, UserAgreementActivity.class);
			startActivity(intent);
			break;
		case R.id.iv_back:
			finish();
			break;

		default:
			break;
		}
	}

}
