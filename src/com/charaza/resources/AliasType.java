package com.charaza.resources;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class AliasType
{
	private int id;
	private String text;
	private DatabaseHelper databaseHelper;
	private Context context;
	private SQLiteDatabase writableDb;
	public AliasType(int id, String text,Context context,SQLiteDatabase writableDb) 
	{
		this.id=id;
		this.text=text;
		this.context=context;
		this.writableDb=writableDb;
		databaseHelper=new DatabaseHelper(context);
	}
	
	public void addToDatabase()
	{
		databaseHelper.runDeleteQuery(writableDb, databaseHelper.ALIAS_TYPE_TABLE, String.valueOf(id));
		databaseHelper.runInsertQuery(databaseHelper.ALIAS_TYPE_TABLE, new String[] {"_id","text"}, new String[] {String.valueOf(id),text}, writableDb);
	}

}
