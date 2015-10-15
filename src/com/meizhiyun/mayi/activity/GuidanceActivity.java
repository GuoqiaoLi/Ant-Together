package com.meizhiyun.mayi.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.meizhiyun.mayi.R;
import com.meizhiyun.mayi.adapter.GuidanceAdapter;

/**
 * 
 * @类名称: GuidanceActivity
 * @类描述: 引导界面
 * @创建人：LiXinYang
 * @备注：
 * @version V1.0
 */
@ContentView(R.layout.guidance_activity_layout)
public class GuidanceActivity extends BaseActivity implements OnClickListener{
	@ViewInject(R.id.vp)
	private ViewPager vp;
	private int[] imgs;// 图片资源id
	private GuidanceAdapter adapter;
	private List<ImageView> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		// 初始化信息
		init();
	}

	// 初始化信息
	private void init() {
		// 图片资源id
		imgs = getImgs();
		list = new ArrayList<ImageView>(); // 定义一个布局并设置参数
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		// 设置数据源
		for (int i = 0; i < imgs.length; i++) {
			ImageView iv = new ImageView(this);
			iv.setLayoutParams(mParams);
			iv.setBackgroundResource(imgs[i]);
			list.add(iv);
		}
		adapter = new GuidanceAdapter();
		adapter.setData(list);
		vp.setAdapter(adapter);
		list.get(list.size()-1).setOnClickListener(this);
	}

	// 获取图片资源
	private int[] getImgs() {
		return new int[] { R.drawable.guidance1, R.drawable.guidance2,
				R.drawable.guidance3 };
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this, ServiceTermsActivity.class);
		startActivity(intent);
		finish();
	}
}
