package com.charaza.resources;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.util.Log;

public class Extras 
{
	public byte[] serializeObject(Object object)
	{
		byte[] buffer=null;
		ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream;
		try
		{
			objectOutputStream=new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(object);
			objectOutputStream.close();
			buffer=byteArrayOutputStream.toByteArray();
		}
		catch (Exception e)
		{
			Log.e("Serializing", "unable to serialize object");
		}
		return buffer;
	}
	
	public Object deSerializeObject(byte[] byteArray)
	{
		Object object=null;
		try
		{
			ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(byteArray);
			ObjectInputStream objectInputStream=new ObjectInputStream(byteArrayInputStream);
			object=objectInputStream.readObject();
		}
		catch (Exception e)
		{
			Log.e("Deserializing","unable to deserialize object");
		}
		return object;
	}

}
