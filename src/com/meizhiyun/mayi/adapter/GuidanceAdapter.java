package com.meizhiyun.mayi.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 
 * @类名称: GuidanceAdapter
 * @类描述: 引导页的适配器
 * @创建人：LiXinYang
 * @备注：
 * @version V1.0
 */
public class GuidanceAdapter extends PagerAdapter {
	private List<ImageView> mList;

	public void setData(List<ImageView> list) {
		this.mList = list;
		// 数据源发生改变,发出通知
		notifyDataSetChanged();
	}

	// 类似于BaseAdapter中的getCount()方法:
	// 用来决定ViewPager中能够显示多少个子view.
	@Override
	public int getCount() {
		if (mList != null) {
			return mList.size();
		}
		return 0;
	}

	// 判断容器中的view是否和obj是同一个对象.
	// "篮子":ViewPager,container;"鸡蛋":ImageView,view;"鸡":集合
	// 判断"篮子里已有的鸡蛋和从鸡中新得到的鸡蛋是否是同一个鸡蛋"

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return view == obj;
	}

	// 为每个position创建对应的pager.
	// 2个参数:①.container:ViewPager;②.position:每个view对应的位置;
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// 将集合中position位置上的图片添加到容器(ViewGroup)中
		container.addView(mList.get(position));
		// 返回集合中position的图片
		return mList.get(position);
	}

	// 移出一个指定位置上的pager,将其移出到内存中."把鸡蛋从篮子拿出来,拿出来放到了地上"
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// 从容器中移除集合中position位置处的对象
		container.removeView(mList.get(position));
	}
}
