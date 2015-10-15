package com.meizhiyun.mayi.view;

import android.content.Context;
import android.content.Intent;
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

import com.meizhiyun.mayi.R;
import com.meizhiyun.mayi.activity.OfflineCityListActivity;
import com.meizhiyun.mayi.utils.PreferenceUtil;

/**
 * 
 * @类名称: DownloadOfflineMapWindow
 * @类描述: 下载离线地图的窗口
 * @创建人：LiXinYang
 * @备注：
 * @version V1.0
 */
public class DownloadOfflineMapWindow extends PopupWindow implements
		OnClickListener {
	private View view;
	private Context context;
	private boolean dontNotice;
	private TextView tvCancel;
	private TextView tvConfirm;
	private ImageView ivNotice;

	public DownloadOfflineMapWindow(Context context) {
		super(context);
		this.context = context;
		dontNotice = PreferenceUtil.readBoolean(context, "window", "dontNotice");
		view = LayoutInflater.from(context).inflate(
				R.layout.download_offlinemap_window, null);
		tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
		tvConfirm = (TextView) view.findViewById(R.id.tv_confirm);
		ivNotice = (ImageView) view.findViewById(R.id.iv_notice);
		tvCancel.setOnClickListener(this);
		tvConfirm.setOnClickListener(this);
		ivNotice.setOnClickListener(this);
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

				int height = view.findViewById(R.id.ll_download_offline_map)
						.getTop();
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_cancel:
			dismiss();
			PreferenceUtil.write(context, "window", "dontNotice", dontNotice);
			break;
		case R.id.tv_confirm:
			dismiss();
			Intent intent = new Intent(context,
					OfflineCityListActivity.class);
			context.startActivity(intent);
			PreferenceUtil.write(context, "window", "dontNotice", dontNotice);
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

		default:
			break;
		}
	}
}
