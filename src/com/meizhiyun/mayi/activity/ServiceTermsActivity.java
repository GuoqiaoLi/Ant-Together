package com.meizhiyun.mayi.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.meizhiyun.mayi.R;
import com.meizhiyun.mayi.utils.PreferenceUtil;

/**
 * 
 * @类名称: ServiceTermsActivity
 * @类描述: 服务条款Activity
 * @创建人：LiXinYang
 * @备注：
 * @version V1.0
 */
@ContentView(R.layout.service_terms_activity)
public class ServiceTermsActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
	}

	@OnClick({ R.id.tv_unagree, R.id.tv_agree })
	public void click(View v) {
		switch (v.getId()) {
		case R.id.tv_agree:
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
			PreferenceUtil.write(this, "First_Login", "isFirstLogin", false);
			break;
		case R.id.tv_unagree:
			unagreeMethod();
			break;
		default:
			break;
		}
	}

	// 拒绝的方法
	private void unagreeMethod() {
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示");
		builder.setMessage("如果您拒绝本条款，您将无权使用蚂蚁聚聚。");
		builder.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}

				});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
}
