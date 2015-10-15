package com.meizhiyun.mayi.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyView extends TextView {

	public static final int MPAUSE = 1;
	public static final int MCONTIUNE = 2;
	public static final int MUPDATE = 3;
	public static final int DEFAULT = 0;
	public static final int MGONE = 4;
	private int mCurrent;

	public int getmCurrent() {
		return mCurrent;
	}

	public void setmCurrent(int mCurrent) {
		this.mCurrent = mCurrent;
	}

	public MyView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

}
