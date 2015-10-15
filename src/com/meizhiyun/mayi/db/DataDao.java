package com.meizhiyun.mayi.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataDao {

	private SQLiteDatabase db;

	public DataDao(Context context) {
		DBHelper helper = new DBHelper(context);
		db = helper.getWritableDatabase();
	}


	// 向数据库中写入轨迹的信息
	public void insertRouteInfo(String userid, String latitude, String longitude) {
		Cursor cursor = db.query("route", null,
				"userid = ? and latitude = ? and longitude = ?", new String[] {
						userid, latitude, longitude }, null, null, null);
		int columnCount = 0;
		while (cursor.moveToNext()) {
			columnCount = cursor.getColumnCount();
		}
		if (columnCount == 0) {
			ContentValues values = new ContentValues();
			values.put("grpid", userid);
			values.put("latitude", latitude);
			values.put("longitude", longitude);
			db.insert("route", null, values);
		}
	}

	// 从数据库中查询轨迹信息
	public Cursor queryRouteInfo(String userid) {
		Cursor cursor = db.query("route", null, "userid = ?",
				new String[] { userid }, null, null, null);
		return cursor;
	}
	
	// 将数据库中相对应的grpid的数据删除
	public void deleteRouteInfo(String userid){
		db.delete("route", "userid = ?", new String[]{userid});
	}
	
	//删除表中所有的数据
	public void deleteAllInfo(String table){
		db.delete(table, null, null);
	}

}
