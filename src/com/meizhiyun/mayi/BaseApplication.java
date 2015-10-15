package com.meizhiyun.mayi;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.meizhiyun.mayi.netstate.NetWorkUtil;

public class BaseApplication extends Application {

	private static BaseApplication application;
	private ArrayList<Activity> activities = new ArrayList<Activity>();

	public static BaseApplication getInstance() {
		if (application == null) {
			application = new BaseApplication();
		}
		return application;
	}

	@Override
	public void onCreate() {
		// 应用程序入口处调用,避免手机内存过小，杀死后台进程,造成SpeechUtility对象为null
		// 注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
		// 参数间使用“,”分隔。
		// 设置你申请的应用appid
		// SpeechUtility.createUtility(BaseApplication.this, "appid=55040f80");
		super.onCreate();
	}

	// 添加Activity到容器中
	public void addActivity(Activity activity) {
		activities.add(activity);
	}

	public void deleteActivity(Activity activity) {
		activities.remove(activity);
	}

	// finish
	public void exit() {
		for (Activity activity : activities) {
			activity.finish();
		}
		activities.clear();

	}

	// 弹出土司的方法
	public static void toastMethod(Context context, String text, int type) {
		if (type == 0) {
			Toast.makeText(context, "网络请求失败,请检查网络", Toast.LENGTH_SHORT).show();
		} else if (type == 1) {
			Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
		}
	}

	// 判断网络状态的方法
	public static boolean judgeNetwork(Context context) {
		boolean isNetwork = true;
		if (!NetWorkUtil.isNetworkAvailable(context)
				|| !NetWorkUtil.isNetworkConnected(context)) {
			isNetwork = false;
		}
		return isNetwork;
	}
}
