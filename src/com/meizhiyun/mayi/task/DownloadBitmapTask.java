package com.meizhiyun.mayi.task;

import com.meizhiyun.mayi.listener.OnGetImgDataListener;
import com.meizhiyun.mayi.utils.HttpUtil;

import android.os.AsyncTask;
/**
 * 
 * @类名称: DownloadBitmapTask
 * @类描述: 下载图片的异步任务
 * @创建人：LiXinYang
 * @备注：     
 * @version V1.0
 */
public class DownloadBitmapTask extends AsyncTask<String, Void, byte[]> {

	private OnGetImgDataListener mListener;

	public DownloadBitmapTask(OnGetImgDataListener mListener) {
		this.mListener = mListener;
	}

	@Override
	protected byte[] doInBackground(String... params) {

		byte[] data = HttpUtil.getImageResult(params[0], "get");

		return data;
	}

	@Override
	protected void onPostExecute(byte[] result) {
		super.onPostExecute(result);
		mListener.getImgData(result);
	}

}
