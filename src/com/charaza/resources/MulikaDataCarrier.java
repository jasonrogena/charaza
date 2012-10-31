package com.charaza.resources;

import java.util.ArrayList;
import java.util.List;

import com.charaza.ExtraInfo;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class MulikaDataCarrier implements Parcelable
{
	public final String PARCELABLE_KEY="mulikaDataCarrier";
	private String post;
	private String somethingElse;
	private String incident;
	private int profileStatus;
	private boolean data;
	
	public MulikaDataCarrier()
	{
		data=false;
	}
	
	public MulikaDataCarrier(String post, String somethingElse, String incident,boolean status) 
	{
		this.post=post;
		this.somethingElse=somethingElse;
		this.incident=incident;
		if(status==true)
		{
			profileStatus=1;
		}
		else
		{
			profileStatus=0;
		}
		data=true;
	}
	
	public boolean hasData()
	{
		return data;
	}
	
	public MulikaDataCarrier(Parcel parcel)
	{
		readFromParcel(parcel);
	}
	
	public int getPostPostion(String[] allPosts)
	{
		if(allPosts==null || allPosts.length==0)
		{
			return -1;
		}
		else
		{
			for (int i = 0; i < allPosts.length; i++)
			{
				if(allPosts[i].equals(post))
				{
					return i;
				}
			}
			return 0;
		}
	}
	
	public boolean getProfileStatus()
	{
		if(profileStatus==1)
		{
			return true;
		}
		else 
		{
			return false;
		}
	}
	
	public String getSomethingElse()
	{
		return somethingElse;
	}
	
	public String getIncidentDetails()
	{
		return incident;
	}
	
	@Override
	public int describeContents() 
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) 
	{
		dest.writeString(post);
		dest.writeString(somethingElse);
		dest.writeString(incident);
		dest.writeInt(profileStatus);
	}
	
	public void readFromParcel(Parcel parcel)
	{
		post=parcel.readString();
		somethingElse=parcel.readString();
		incident=parcel.readString();
		profileStatus=parcel.readInt();
	}
	
	public static final Parcelable.Creator<MulikaDataCarrier> CREATOR=new Parcelable.Creator<MulikaDataCarrier>()
			{

				@Override
				public MulikaDataCarrier createFromParcel(Parcel source)
				{
					return new MulikaDataCarrier(source);
				}

				@Override
				public MulikaDataCarrier[] newArray(int size)
				{
					// TODO Auto-generated method stub
					return new MulikaDataCarrier[size];
				}
	};
}
