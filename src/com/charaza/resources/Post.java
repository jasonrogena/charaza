package com.charaza.resources;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class Post 
{
	private int id;
	private String text;
	private Context context;
	private SQLiteDatabase writableDb;
	private DatabaseHelper databaseHelper;
	public Post(int id,String text,Context context,SQLiteDatabase writableDb) 
	{
		this.id=id;
		this.text=text;
		this.context=context;
		this.writableDb=writableDb;
		this.databaseHelper=new DatabaseHelper(context);
	}

	public void addToDatabase()
	{
		//Toast.makeText(context, "adding post"+String.valueOf(id)+" to database", Toast.LENGTH_SHORT).show();
		databaseHelper.runDeleteQuery(writableDb, databaseHelper.POST_TABLE, String.valueOf(id));
		databaseHelper.runInsertQuery(databaseHelper.POST_TABLE, new String[] {"_id","text"}, new String[] {String.valueOf(id),text}, writableDb);
	}
}
