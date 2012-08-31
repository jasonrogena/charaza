package com.charaza.resources;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class Alias 
{
	private int id;
	private int aliasType;
	private String text;
	private int profileId;
	private Context context;
	private SQLiteDatabase writableDb;
	private DatabaseHelper databaseHelper;
	public Alias(int id,int profileId,int aliasType,String text,Context context,SQLiteDatabase writableDb)
	{
		this.id=id;
		this.aliasType=aliasType;
		this.text=text;
		this.context=context;
		this.profileId=profileId;
		this.writableDb=writableDb;
		databaseHelper=new DatabaseHelper(context);
	}
	
	public void addToDatabase()
	{
		databaseHelper.runDeleteQuery(writableDb, databaseHelper.ALIAS_TABLE, String.valueOf(id));
		databaseHelper.runInsertQuery(databaseHelper.ALIAS_TABLE, new String[] {"_id","profile","alias_type","text"}, new String[] {String.valueOf(id),String.valueOf(profileId),String.valueOf(aliasType),text}, writableDb);
	}

}
