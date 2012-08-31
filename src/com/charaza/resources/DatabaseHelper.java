package com.charaza.resources;

import java.io.Serializable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -943331717690736953L;
	public static final String DB_NAME="charaza_db";
	public static final String PROFILE_TABLE="profile";
	public static final String INCIDENT_TABLE="incident";
	public static final String ALIAS_TABLE="alias";
	public static final String COMMENT_TABLE="comment";
	public static final String POST_TABLE="post";
	public static final String ALIAS_TYPE_TABLE="alias_type";
	public static final String PROPERTIES_TABLE="properties";
	public static final int VERSION=1;
	
	public DatabaseHelper(Context context)
	{
		super(context, DB_NAME, null, 1);//version is one
		//TODO: Check if database alread exists
		//TODO: all dates should be in GMT
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL("CREATE TABLE "+PROPERTIES_TABLE+" (_id INTEGER PRIMARY KEY, last_profile_update TEXT, last_post_update TEXT, last_alias_type_update TEXT);");
		db.execSQL("CREATE TABLE "+PROFILE_TABLE+" (_id INTEGER PRIMARY KEY, name TEXT, post INTEGER, charazwad INTEGER);");//will store up to 1000 profiles
		db.execSQL("CREATE TABLE "+INCIDENT_TABLE+" (_id INTEGER PRIMARY KEY, profile INTEGER, text TEXT, time TEXT);");
		db.execSQL("CREATE TABLE "+ALIAS_TABLE+" (_id INTEGER PRIMARY KEY, profile INTEGER, alias_type INTEGER, text TEXT);");//will store all aliases for app profiles
		db.execSQL("CREATE TABLE "+COMMENT_TABLE+" (_id INTEGER PRIMARY KEY, incident INTEGER, text TEXT, time TEXT);");
		db.execSQL("CREATE TABLE "+POST_TABLE+" (_id INTEGER PRIMARY KEY, text TEXT);");//will store all posts
		db.execSQL("CREATE TABLE "+ALIAS_TYPE_TABLE+" (_id INTEGER, text TEXT);");//will store all alias types
		ContentValues cv=new ContentValues();
		cv.put("_id", 1);
		cv.put("last_profile_update","2000-1-1 01:00:00");//date should be in GMT
		cv.put("last_post_update","2000-1-1 01:00:00");
		cv.put("last_alias_type_update","2000-1-1 01:00:00");
		db.insert(PROPERTIES_TABLE, null,cv);
		cv.clear();
		
		/*//TEST DATA
		cv.put("_id", 0);
		cv.put("text", "I don't know");
		db.insert(POST_TABLE, null, cv);
		cv.clear();
		cv.put("_id", 500);//TODO: remove this row from the table and integrate it into the drop down
		cv.put("text", "Something else");
		db.insert(POST_TABLE, null, cv);*/
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS "+ALIAS_TYPE_TABLE);
		db.execSQL("DROP TABLE IF EXISTS "+POST_TABLE);
		db.execSQL("DROP TABLE IF EXISTS "+COMMENT_TABLE);
		db.execSQL("DROP TABLE IF EXISTS "+ALIAS_TABLE);
		db.execSQL("DROP TABLE IF EXISTS "+INCIDENT_TABLE);
		db.execSQL("DROP TABLE IF EXISTS "+PROFILE_TABLE);
		db.execSQL("DROP TABLE IF EXISTS "+PROPERTIES_TABLE);
		onCreate(db);
	}
	
	public String[][] runSelectQuery(SQLiteDatabase db, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
	{
		Cursor cursor=db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
		if(cursor.getCount()!=-1)
		{
			String[][] result=new String[cursor.getCount()][columns.length];
			int c1=0;
			cursor.moveToFirst();
			while(c1<cursor.getCount())
			{
				int c2=0;
				while(c2<columns.length)
				{
					result[c1][c2]=cursor.getString(c2);
					c2++;
				}
				if(c1!=cursor.getCount()-1)//is not the last row
				{
					cursor.moveToNext();
				}
				c1++;
			}
			cursor.close();
			return result;
		}
		else
		{
			return null;
		}
	}
	
	public void runDeleteQuery(SQLiteDatabase db, String table, String _id)
	{
		db.delete(table, "_id=?", new String[]{_id});
	}
	
	public void runInsertQuery(String table,String[] columns,String[] values,SQLiteDatabase db)
	{
		if(columns.length==values.length)
		{
			ContentValues cv=new ContentValues();
			int count=0;
			while(count<columns.length)
			{
				cv.put(columns[count], values[count]);
				count++;
			}
			db.insert(table, null, cv);
			cv.clear();
		}
	}
	
	public void runQuery(SQLiteDatabase db, String query)//non return queries
	{
		db.execSQL(query);
	}

}
