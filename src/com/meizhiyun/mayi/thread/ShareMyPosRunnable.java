package com.meizhiyun.mayi.thread;

import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;

import com.meizhiyun.mayi.listener.OnGetJsonDataListener;
import com.meizhiyun.mayi.utils.UrlConnGetMethod;
/**
 * 
 * @类名称: ShareMyPosRunnable
 * @类描述: 共享位置的线程的runnable
 * @创建人：LiXinYang
 * @备注：     
 * @version V1.0
 */
public class ShareMyPosRunnable implements Runnable {
	private Context context;
	private OnGetJsonDataListener mListener;
	private List<NameValuePair> list;
	public ShareMyPosRunnable(Context context, List<NameValuePair> list,OnGetJsonDataListener listener) {
		this.context = context;
		this.mListener = listener;
		this.list = list;
	}

	@Override
	public void run() {
		String json = UrlConnGetMethod.urlConnGetData(context, list);
		mListener.getJsonData(json);
	}

}
