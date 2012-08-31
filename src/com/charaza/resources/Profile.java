package com.charaza.resources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Button;

public class Profile implements Parcelable
{
	/**
	 * 
	 */
	public final String PARCELABLE_KEY="Profile";
	private int id;
	private String name;
	private int postId;
	private String post;
	private int charazwad;
	private Context context;
	private DatabaseHelper databaseHelper;
	private SQLiteDatabase writableDb;
	private List<String> aliasTypes;
	private List<String> aliases;
	//private List<Button> addedAliases;
	
	public Profile()
	{
	}
	public Profile(int id,String name,int postId,int charazwad,Context context,SQLiteDatabase writableDb) 
	{
		this.context=context;
		this.id=id;
		this.name=name;
		this.databaseHelper=new DatabaseHelper(context);
		this.context=context;
		this.postId=postId;
		this.charazwad=charazwad;
		this.writableDb=writableDb;
		this.aliases=new ArrayList<String>();
		this.aliasTypes=new ArrayList<String>();
	}
	public Profile(Context context)
	{
		this.context=context;
		this.databaseHelper=new DatabaseHelper(context);
		this.writableDb=databaseHelper.getWritableDatabase();
		this.aliases=new ArrayList<String>();
		this.aliasTypes=new ArrayList<String>();
	}
	public Profile(Parcel in)
	{
		readFromParcel(in);
	}
	public void addToDatabase()
	{
		databaseHelper.runDeleteQuery(writableDb, databaseHelper.PROFILE_TABLE, String.valueOf(id));
		databaseHelper.runInsertQuery(databaseHelper.PROFILE_TABLE, new String[] {"_id","name","post","charazwad"}, new String[] {String.valueOf(id),name,String.valueOf(postId),String.valueOf(charazwad)}, writableDb);
	}
	public void setContext(Context context)
	{
		this.context=context;
		this.databaseHelper=new DatabaseHelper(context);
		this.writableDb=databaseHelper.getWritableDatabase();
	}
	public void addAlias(String aliasType, String alias)
	{
		aliasTypes.add(aliasType);
		aliases.add(alias);
	}
	public String getAliasAt(int index)
	{
		return aliases.get(index);
	}
	public String getAliasTypeAt(int index)
	{
		return aliasTypes.get(index);
	}
	
	public void setName(String name)
	{
		this.name=name;
	}
	
	public void setPost(String post)
	{
		this.post=post;
	}
	
	public String getPost()
	{
		return post;
	}
	
	public String getName()
	{
		return name;
	}
	
	public List<String> getAliasTypes()
	{
		return aliasTypes;
	}
	
	public List<String> getAliases()
	{
		return aliases;
	}
	
	public void closeDatabase()
	{
		writableDb.close();
	}
	public int getNumberOfAliases()
	{
		return aliases.size();
	}
	@Override
	public int describeContents() 
	{
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) 
	{
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeInt(postId);
		dest.writeString(post);
		dest.writeInt(charazwad);
		dest.writeList(aliasTypes);
		dest.writeList(aliases);
		//dest.writeList(addedAliases);
	}
	
	public void readFromParcel(Parcel in)
	{
		this.id=in.readInt();
		this.name=in.readString();
		this.postId=in.readInt();
		this.post=in.readString();
		this.charazwad=in.readInt();
		this.aliasTypes=new ArrayList<String>();
		in.readList(aliasTypes, null);
		this.aliases=new ArrayList<String>();
		in.readList(aliases, null);
		//this.addedAliases=new ArrayList<Button>();
		//in.readList(addedAliases, null);
	}
	
	public static final Parcelable.Creator<Profile> CREATOR=new Parcelable.Creator<Profile>()
	{
		@Override
		public Profile createFromParcel(Parcel source)
		{
			return new Profile(source);
		}

		@Override
		public Profile[] newArray(int size) 
		{
			return new Profile[size];
		}
		
	};
}
