package com.meizhiyun.mayi.view;

import com.meizhiyun.mayi.R;
import com.meizhiyun.mayi.utils.PreferenceUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
 * @类名称: UpdateVersionWindow
 * @类描述: 更新版本的PopupWindow
 * @创建人：LiXinYang
 * @备注：
 * @version V1.0
 */
public class UpdateVersionWindow extends PopupWindow implements OnClickListener {

	private View view;
	private Context context;
	private boolean dontNotice;
	private TextView tvCancel;
	private TextView tvConfirm;
	private ImageView ivNotice;
	private TextView tvVersionInfo;
	private String downloadUrl;

	public UpdateVersionWindow(Context context, String info,String downloadUrl) {
		super(context);
		this.context = context;
		this.downloadUrl = downloadUrl;
		dontNotice = PreferenceUtil.readBoolean(context, "window",
				"update_dontNotice");
		view = LayoutInflater.from(context).inflate(
				R.layout.update_version_window, null);
		tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
		tvConfirm = (TextView) view.findViewById(R.id.tv_confirm);
		ivNotice = (ImageView) view.findViewById(R.id.iv_notice);
		tvVersionInfo = (TextView) view.findViewById(R.id.tv_version_info);
		tvCancel.setOnClickListener(this);
		tvConfirm.setOnClickListener(this);
		ivNotice.setOnClickListener(this);
		tvVersionInfo.setText(info);
		this.setContentView(view);
		// 设置宽和高
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.MATCH_PARENT);
		// 设置可获得焦点
		this.setFocusable(true);
		// 设置动画
		this.setAnimationStyle(R.style.CenterWindowAnim);
		// 设置背景色
		ColorDrawable dw = new ColorDrawable(0xbb000000);
		this.setBackgroundDrawable(dw);
		// 设置触摸事件,即当点击PopupWindow的外面时,弹窗消失
		view.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				int height = view.findViewById(R.id.ll_update_version).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height || y > height) {
						dismiss();
					}
				}
				return true;
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_cancel:
			dismiss();
			PreferenceUtil.write(context, "window", "update_dontNotice", dontNotice);
			break;
		case R.id.iv_notice:
			if (!dontNotice) {
				ivNotice.setBackgroundResource(R.drawable.right);
				dontNotice = true;
			}else {
				ivNotice.setBackgroundResource(R.drawable.right_hui);
				dontNotice = false;
			}
			break;
		case R.id.tv_confirm:
			dismiss();
			Intent it = new Intent(Intent.ACTION_VIEW, Uri
					.parse(downloadUrl));
			it.setClassName("com.android.browser",
					"com.android.browser.BrowserActivity");
			context.startActivity(it);
			PreferenceUtil.write(context, "window", "update_dontNotice", dontNotice);
			break;

		default:
			break;
		}
	}
}
