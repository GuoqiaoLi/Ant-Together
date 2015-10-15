package com.meizhiyun.mayi.view;

import com.meizhiyun.mayi.R;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 
 * @类名称: HistoryShareWindow
 * @类描述: 历史轨迹分享
 * @创建人：LiXinYang
 * @备注：
 * @version V1.0
 */

public class HistoryShareWindow extends PopupWindow {
	private View menuView;
	private ImageView ivWechatShare;
	private ImageView ivWechatMommentShare;
	private TextView tvCancel;

	public HistoryShareWindow(Activity context, OnClickListener onclick) {
		super(context);
		// 填充布局
		menuView = LayoutInflater.from(context).inflate(R.layout.share_window,
				null, false);
		ivWechatShare = (ImageView) menuView.findViewById(R.id.iv_wechat_share);
		ivWechatMommentShare = (ImageView) menuView.findViewById(R.id.iv_wechat_momment_share);
		tvCancel = (TextView) menuView.findViewById(R.id.tv_cancel);
		
		tvCancel.setOnClickListener(onclick);
		ivWechatShare.setOnClickListener(onclick);
		ivWechatMommentShare.setOnClickListener(onclick);
		
		this.setContentView(menuView);
		//设置宽和高
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		//设置可获得焦点
		this.setFocusable(true);
		//设置动画
		this.setAnimationStyle(R.style.ShareWindowAnim);
		//设置背景色
		ColorDrawable dw = new ColorDrawable(0x00000000);
		this.setBackgroundDrawable(dw);
		//设置触摸事件,即当点击PopupWindow的外面时,弹窗消失
		menuView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				int height = menuView.findViewById(R.id.pop_layout).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});

	}
}
