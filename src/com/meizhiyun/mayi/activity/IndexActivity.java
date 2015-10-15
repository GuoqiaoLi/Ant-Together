package com.meizhiyun.mayi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.meizhiyun.mayi.R;
import com.meizhiyun.mayi.utils.PreferenceUtil;

/**
 * 
 * @类名称: IndexActivity
 * @类描述: 启动页界面
 * @创建人：Guoqiao Li
 * @备注：
 * @version V1.0
 */

@ContentView(R.layout.index_activity_layout)
public class IndexActivity extends BaseActivity {
	private boolean isFirstLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
	}
	
	//初始化信息
	public void init(){
		isFirstLogin = PreferenceUtil.readBooleanTrue(this, "First_Login", "isFirstLogin");
		Intent intent = null;
		if (isFirstLogin) {
			intent = new Intent(this, GuidanceActivity.class);
		}else {
			intent = new Intent(this, MainActivity.class);
		}
		startActivity(intent);
		finish();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		new Thread(){
			public void run(){
				SystemClock.sleep(3000);
				init();
			}
		}.start();
	}
}
