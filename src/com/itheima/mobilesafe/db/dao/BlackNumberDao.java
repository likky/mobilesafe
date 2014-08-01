package com.itheima.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.itheima.mobilesafe.db.BlackNumberDBOpenHelper;
import com.itheima.mobilesafe.db.domain.BlackNumberInfo;

/**
 * 黑名单的数据库dao
 * 提供增删改查的方法
 *
 */
public class BlackNumberDao {

	private BlackNumberDBOpenHelper helper;
	public BlackNumberDao(Context context){
		helper = new BlackNumberDBOpenHelper(context);
	}
	/**
	 * 添加一条黑名单号码
	 * @param phone 黑名单号码
	 * @param mode 拦截方式 1 电话拦截 2 短信拦截 3 全部拦截
	 */
	public boolean add(String phone,String mode){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("phone", phone);
		values.put("mode", mode);
		long rowid = db.insert("blacknumber",null, values );
		db.close();
		return rowid!=-1;
	}
	/**
	 * 删除一条黑名单号码
	 * @param phone 黑名单号码
	 */
	public boolean delete(String phone){
		SQLiteDatabase db = helper.getWritableDatabase();
		int rawCount = db.delete("blacknumber", "phone=?", new String[]{phone});
		db.close();
		return rawCount>0;
	}
	/**
	 * 修改一条黑名单号码的拦截模式
	 * @param phone 黑名单号码
	 */
	public boolean update(String phone,String newMode){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", newMode);
		int rowCount = db.update("blacknumber", values , "phone=?", new String[]{phone});
		db.close();
		return rowCount>0;
	}
	/**
	 * 查询一条黑名单号码是否存在
	 * @param phone 黑名单号码
	 */
	public boolean find(String phone){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("blacknumber", null,"phone=?",new String[]{phone},null, null, null);
		boolean result = cursor.moveToNext();
		cursor.close();
		db.close();
		return result;
	}
	public List<BlackNumberInfo> findAll(){
		List<BlackNumberInfo> infos = new ArrayList<BlackNumberInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("blacknumber", new String[]{"phone","mode"},null, null,null,null,null);
		while(cursor.moveToNext()){
			infos.add(new BlackNumberInfo(cursor.getString(0),cursor.getString(1)));
		}
		cursor.close();db.close();
		return infos;
	}
	
}