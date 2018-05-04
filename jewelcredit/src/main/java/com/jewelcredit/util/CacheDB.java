package com.jewelcredit.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class CacheDB {

	private static final String mFilename = "zbd.cache";
	private SQLiteDatabase mdb;
	private static CacheDB mInstance;	
	
	
	public boolean initialize(Context context){
		return open(context);
	}

	
	public synchronized  boolean open(Context context){
		
		if(mdb != null)
		{
			return true;
		}
		
		
		String sql = "SELECT COUNT(*) FROM sqlite_master WHERE type='table' and name='cache'";
		Cursor cursor = null;
		boolean retval = false;
		
		try{
			mdb  = context.openOrCreateDatabase(mFilename, Context.MODE_PRIVATE, null);
			cursor = mdb.rawQuery(sql, null);
			
			cursor.moveToNext();
			long count = cursor.getLong(0);
			if(count > 0){
				retval = true;
			}
			else
			{
				mdb.execSQL("DROP TABLE IF EXISTS cache; ");
				mdb.execSQL("CREATE TABLE cache(key VARCHAR(200) NOT NULL, value BLOB);");
				mdb.execSQL("DROP INDEX IF EXISTS key_index; ");
				mdb.execSQL("CREATE UNIQUE INDEX key_index ON cache(key);");
			}
		}
		catch(SQLiteException e)
		{
		}
		finally
		{
			cursor.close();
		}
		
		return retval;
	}
	
	
	public static CacheDB getInstance(){
		if(mInstance != null){
			return mInstance;
		}
		
		mInstance = new CacheDB();
		return mInstance;
	}
	
	
	public synchronized  void setValue(String key, String value)
	{
		String sql = " REPLACE INTO cache(key, value) VALUES (?, ?); ";
		
		try{
			mdb.execSQL(sql, new Object[]{key, value});
		}
		catch(SQLiteException e)
		{
		}
		finally
		{
		}
	}
	
	
	public synchronized String getValue(String key)
	{
		String sql = " SELECT value FROM cache WHERE key=? ";
		String value = "";
		Cursor cursor = null;
		
		try{
			cursor = mdb.rawQuery(sql, new String[]{key});

			if(cursor.moveToNext()){
				value = cursor.getString(0);
			}
		}
		catch(SQLiteException e)
		{
			Utils.log(e.getMessage());
		}
		finally
		{
			cursor.close();
		}
		
		return value;
	}
}
