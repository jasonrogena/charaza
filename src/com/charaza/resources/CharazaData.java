package com.charaza.resources;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import android.util.Log;

public class CharazaData implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6481133839431933140L;
	private Context context;
	private SQLiteDatabase readableDb;
	private SQLiteDatabase writableDb;
	private DatabaseHelper databaseHelper;
	private int httpPostTimout=20000;
	private int httpResponseTimout=20000;
	public static String baseURL="http://10.0.2.2/~jason/charaza";
	//public static String baseURL="http://charaza.zxq.net";

	public CharazaData(Context context) 
	{
		this.context=context;
		databaseHelper=new DatabaseHelper(this.context);
		readableDb=databaseHelper.getReadableDatabase();
		writableDb=databaseHelper.getWritableDatabase();
	}
	
	public void closeDatabase()
	{
		readableDb.close();
		writableDb.close();
	}
	
	public String[][] getProfiles()
	{
		updateProfiles();
		String[] columns={"_id","name","post","charazwad"};
		String[][] result=databaseHelper.runSelectQuery(readableDb, databaseHelper.PROFILE_TABLE, columns, null, null, null, null, null, null);
		return result;
	}
	
	public String[][] getAliasTypes()
	{
		updateAliasTypes();
		String[] columns={"_id","text"};
		String[][] results=databaseHelper.runSelectQuery(readableDb, databaseHelper.ALIAS_TYPE_TABLE, columns, null, null, null, null, null, null);
		return results;
	}
	
	public String[][] getPosts()
	{
		updatePosts();
		String[][] result=databaseHelper.runSelectQuery(readableDb, databaseHelper.POST_TABLE, new String[] {"_id","text"}, null, null, null, null, null, null);
		return result;
	}
	
	public String[] getLastUpdated()
	{
		//TODO: set the logic for fetching the dates with the biggest _id
		String[][] result=databaseHelper.runSelectQuery(readableDb, databaseHelper.PROPERTIES_TABLE, new String[] {"_id","last_profile_update","last_post_update","last_alias_type_update"},"_id="+String.valueOf(databaseHelper.VERSION) , null, null, null, null, null);
		return result[0];
	}
	
	public String getPost(int id)
	{
		
		Log.d("post id again", String.valueOf(id).trim());
		String selection="_id="+String.valueOf(id).trim();
		String[][] result=databaseHelper.runSelectQuery(readableDb, databaseHelper.POST_TABLE, new String[] {"text"}, selection, null, null, null, null, null);
		/*if(result[0][0]==null)
	    {
	    	updatePosts();
	    }*/
		
		return result[0][0];
	    
	}
	
	public String getAliasType(int id)
	{
		String selection="_id="+String.valueOf(id);
		String[][] result=databaseHelper.runSelectQuery(readableDb, databaseHelper.ALIAS_TYPE_TABLE, new String[] {"text"}, selection, null, null, null, null, null);
		return result[0][0];
	}
	
	public String[] getProfile(int id)
	{
		String selection="_id="+String.valueOf(id);
		String[][] results=databaseHelper.runSelectQuery(readableDb, databaseHelper.PROFILE_TABLE, new String[] {"name","post","charazwad"}, selection, null, null, null, null, null);
		return results[0];
	}
	
	public String[][] getIncidents(int profile)
	{
		if(checkNetworkConnection())
		{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, httpPostTimout);
			HttpConnectionParams.setSoTimeout(httpParameters, httpResponseTimout);
			HttpClient httpClient=new DefaultHttpClient(httpParameters);
			HttpPost httpPost=new HttpPost(CharazaData.baseURL+"/getIncidents.php");
			try
			{
				String[] dates=getLastUpdated();
				List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("_id", dates[0]));
				nameValuePairs.add(new BasicNameValuePair("profile", String.valueOf(profile)));
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse httpResponse=httpClient.execute(httpPost);
				if(httpResponse.getStatusLine().getStatusCode()==200)
				{
					HttpEntity httpEntity=httpResponse.getEntity();
					if(httpEntity!=null)
					{
						InputStream inputStream=httpEntity.getContent();
						String responseString=convertStreamToString(inputStream);
						if(!responseString.contains("upt0d@te"))
						{
							JSONArray jsonArray=new JSONArray(responseString);
							JSONObject jsonObject=new JSONObject();
							int count=0;
							String[][] results=new String[jsonArray.length()][3];
							while(count<jsonArray.length())
							{
								jsonObject=jsonArray.getJSONObject(count);
								results[count][0]=jsonObject.getString("_id");
								results[count][1]=jsonObject.getString("text");
								results[count][2]=jsonObject.getString("time");
								Log.d("incident", "new incident added");
								count++;
							}
							return results;
						}
					}
				}
				
			}
			catch (Exception e) 
			{
			}
		}
		return null;
	}
	
	public boolean checkNetworkConnection()
	{
		ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
		if(networkInfo==null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public boolean postIncidet(Profile profile, String details)
	{
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, httpPostTimout);
		HttpConnectionParams.setSoTimeout(httpParameters, httpResponseTimout);
		HttpClient httpClient=new DefaultHttpClient(httpParameters);
		HttpPost httpPost=new HttpPost(CharazaData.baseURL+"/submitIncident.php");
		try
		{
			/*JSONArray aliasTypeArray=new JSONArray(profile.getAliasTypes());
			JSONArray aliasArray=new JSONArray(profile.getAliases());
			JSONObject report=new JSONObject();
			report.put("id",1);
			report.put("profileName", profile.getName());
			report.put("proflePost",profile.getPost());
			report.put("details",details);
			report.put("aliasTypes", aliasTypeArray);
			report.put("aliases", aliasArray);
			
			StringEntity stringEntity=new StringEntity(report.toString());
			stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			stringEntity.setContentEncoding("UTF-8");
			//List<NameValuePair> list=new ArrayList<NameValuePair>(1);
			//list.add(new BasicNameValuePair("data", report.toString()));
			httpPost.setEntity(stringEntity);
			//httpPost.setEntity(new UrlEncodedFormEntity(list));
			httpClient.execute(httpPost);*/
			List<NameValuePair> data=new ArrayList<NameValuePair>();
			data.add(new BasicNameValuePair("id", "1"));
			data.add(new BasicNameValuePair("profileName", profile.getName()));
			data.add(new BasicNameValuePair("proflePost", profile.getPost()));
			data.add(new BasicNameValuePair("details", details));
			JSONArray aliasTypeArray=new JSONArray(profile.getAliasTypes());
			JSONArray aliasArray=new JSONArray(profile.getAliases());
			data.add(new BasicNameValuePair("aliasTypes", aliasTypeArray.toString()));
			data.add(new BasicNameValuePair("aliases", aliasArray.toString()));
			httpPost.setEntity(new UrlEncodedFormEntity(data));
			httpClient.execute(httpPost);
			
			return true;
		}
		catch(Exception e)
		{
			Log.w("Http connection", "unable to send incident");
		}
		return false;
	}
	
	public void updateProfiles()
	{
		if(checkNetworkConnection())
		{
			Thread thread=new Thread(new Runnable() 
			{
				
				@Override
				public void run() 
				{
					//Toast.makeText(context, "connecting to server(for profiles)", Toast.LENGTH_SHORT).show();
					HttpParams httpParameters = new BasicHttpParams();
					HttpConnectionParams.setConnectionTimeout(httpParameters, httpPostTimout);
					HttpConnectionParams.setSoTimeout(httpParameters, httpResponseTimout);
					HttpClient httpClient=new DefaultHttpClient(httpParameters);
					HttpPost httpPost=new HttpPost(CharazaData.baseURL+"/getProfiles.php");
					try
					{
						String[] dates=getLastUpdated();//last profile update should be the second item in the array
						List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(2);
						nameValuePairs.add(new BasicNameValuePair("_id", dates[0]));
						nameValuePairs.add(new BasicNameValuePair("last_profile_update", dates[1]));
						httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

						HttpResponse httpResponse=httpClient.execute(httpPost);
						if(httpResponse.getStatusLine().getStatusCode()==200)
						{
							HttpEntity httpEntity=httpResponse.getEntity();
							if(httpEntity!=null)
							{
								//Toast.makeText(context, "response gotten(for profiles)", Toast.LENGTH_SHORT).show();
								InputStream inputStream=httpEntity.getContent();
								String responseString=convertStreamToString(inputStream);
								if(!responseString.contains("upt0d@te"))
								{
									JSONArray jsonArray=new JSONArray(responseString);
									JSONObject jsonObject=new JSONObject();
									int count=0;
									while(count<jsonArray.length())//while((jsonObject=jsonArray.getJSONObject(count))!=null)
									{
										jsonObject=jsonArray.getJSONObject(count);
										int id=jsonObject.getInt("_id");
										String name=jsonObject.getString("name");
										//Toast.makeText(context, name, Toast.LENGTH_SHORT).show();
										int post=jsonObject.getInt("post");
										int charazwad=jsonObject.getInt("charazwad");
										Profile newProfile=new Profile(id, name, post, charazwad, context, writableDb);
										newProfile.addToDatabase();
										JSONArray aliasArray=jsonObject.getJSONArray("aliases");
										int count2=0;
										JSONObject aliasJsonObject=new JSONObject();
										while(count2<aliasArray.length())//while((aliasJsonObject=aliasArray.getJSONObject(count2))!=null)
										{
											aliasJsonObject=aliasArray.getJSONObject(count2);
											int aliasId=aliasJsonObject.getInt("_id");
											int aliasType=aliasJsonObject.getInt("alias_type");
											String aliasText=aliasJsonObject.getString("text");
											//Toast.makeText(context, aliasText, Toast.LENGTH_SHORT).show();
											Alias alias=new Alias(aliasId, id, aliasType, aliasText, context, writableDb);
											alias.addToDatabase();
											count2++;
										}
										count++;
									}
									//Toast.makeText(context, "updating time", Toast.LENGTH_SHORT).show();
									updateDatabaseTime("last_profile_update");
								}
								else
								{
									//Toast.makeText(context, "database up to date", Toast.LENGTH_SHORT).show();
								}
							}
						}
						else
						{
							Log.d("network connection error", "a code other than 200 has been parsed from the server");
						}
					}
					catch (Exception e) 
					{
						// TODO: handle exception
					}
				}
			});
			thread.run();
		}
		else
		{
			//networkError();
		}
	}
	
	public void networkError()
	{
		Builder alertDialog=new AlertDialog.Builder(context);
		alertDialog.setTitle("Connection failed");
		alertDialog.setMessage("This application requires network access. Enable mobile network or Wi-Fi to download data.");
		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Intent intent=new Intent(Settings.ACTION_WIRELESS_SETTINGS);//initially was 
				//ComponentName componentName=new ComponentName("com.android.phone","com.android.phone.Settings");
				//intent.setComponent(componentName);
				context.startActivity(intent);
			}
		});
		alertDialog.setNegativeButton("Cancel", null);
		alertDialog.show();
	}
	
	public void updatePosts()
	{
		if(checkNetworkConnection())
		{
			Thread thread=new Thread(new Runnable() 
			{
				@Override
				public void run() 
				{
					HttpParams httpParameters = new BasicHttpParams();
					HttpConnectionParams.setConnectionTimeout(httpParameters, httpPostTimout);
					HttpConnectionParams.setSoTimeout(httpParameters, httpResponseTimout);
					HttpClient httpClient=new DefaultHttpClient(httpParameters);
					HttpPost httpPost=new HttpPost(CharazaData.baseURL+"/getPosts.php");
					try
					{
						//Toast.makeText(context, "connecting to server(to fetch posts)", Toast.LENGTH_SHORT).show();
						String[] dates=getLastUpdated();//last post update should be the third item in the array
						List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(2);
						nameValuePairs.add(new BasicNameValuePair("_id", dates[0]));
						nameValuePairs.add(new BasicNameValuePair("last_post_update", dates[2]));
						httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

						HttpResponse httpResponse=httpClient.execute(httpPost);
						if(httpResponse.getStatusLine().getStatusCode()==200)
						{
							HttpEntity httpEntity=httpResponse.getEntity();
							if(httpEntity!=null)
							{
								InputStream inputStream=httpEntity.getContent();
								String responseString=convertStreamToString(inputStream);
								//Toast.makeText(context, "response gotten for posts", Toast.LENGTH_SHORT).show();
								if(!responseString.contains("upt0d@te"))
								{
									JSONArray jsonArray=new JSONArray(responseString);
									JSONObject jsonObject=new JSONObject();
									int count=0;
									while(count<jsonArray.length())
									{
										jsonObject=jsonArray.getJSONObject(count);
										int id=jsonObject.getInt("_id");
										String text=jsonObject.getString("text");
										//Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
										Post newPost=new Post(id, text, context, writableDb);
										newPost.addToDatabase();
										count++;
									}
									//Toast.makeText(context, "updating post time", Toast.LENGTH_SHORT).show();
									updateDatabaseTime("last_post_update");
								}
								else
								{
									//Toast.makeText(context, "database up to date", Toast.LENGTH_SHORT).show();
								}
							}
						}
						else
						{
							Log.d("network connection error", "a code other than 200 has been parsed from the server");
						}
					}
					catch (Exception e) 
					{
						// TODO: handle exception
					}
				}
			});
			thread.run();
		}
		else
		{
			//networkError();
		}
	}
	
	public void updateAliasTypes()
	{

		if(checkNetworkConnection())
		{
			Thread thread=new Thread(new Runnable() 
			{
				
				@Override
				public void run() 
				{
					HttpParams httpParameters = new BasicHttpParams();
					HttpConnectionParams.setConnectionTimeout(httpParameters, httpPostTimout);
					HttpConnectionParams.setSoTimeout(httpParameters, httpResponseTimout);
					HttpClient httpClient=new DefaultHttpClient(httpParameters);
					HttpPost httpPost=new HttpPost(CharazaData.baseURL+"/getAliasTypes.php");
					try
					{
						String[] dates=getLastUpdated();
						List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(2);
						nameValuePairs.add(new BasicNameValuePair("_id", dates[0]));
						nameValuePairs.add(new BasicNameValuePair("last_alias_type_update", dates[3]));
						httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

						HttpResponse httpResponse=httpClient.execute(httpPost);
						if(httpResponse.getStatusLine().getStatusCode()==200)
						{
							HttpEntity httpEntity=httpResponse.getEntity();
							if(httpEntity!=null)
							{
								InputStream inputStream=httpEntity.getContent();
								String responseString=convertStreamToString(inputStream);
								if(!responseString.contains("upt0d@te"))
								{
									JSONArray jsonArray=new JSONArray(responseString);
									JSONObject jsonObject=new JSONObject();
									int count=0;
									while(count<jsonArray.length())
									{
										jsonObject=jsonArray.getJSONObject(count);
										int id=jsonObject.getInt("_id");
										String text=jsonObject.getString("text");
										AliasType aliasType=new AliasType(id, text, context, writableDb);
										//Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
										aliasType.addToDatabase();
										count++;
									}
									updateDatabaseTime("last_alias_type_update");
								}
								else
								{
									//Toast.makeText(context, "database up to date", Toast.LENGTH_SHORT).show();
								}
							}
						}
						else
						{
							Log.d("network connection error", "a code other than 200 has been parsed from the server");
						}
					}
					catch (Exception e) 
					{
						// TODO: handle exception
					}
				}
			});
			thread.run();
		}
		else
		{
			//networkError();
		}
	}
	
	private static String convertStreamToString(InputStream inputStream)
	{
		BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder stringBuilder=new StringBuilder();
		String line=null;
		try
		{
			while((line=bufferedReader.readLine()) != null)
			{
				stringBuilder.append(line+"\n");
			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}
		finally
		{
			try 
			{
				inputStream.close();
				
			} catch (Exception e2) 
			{
				// TODO: handle exception
			}
		}
		return stringBuilder.toString();
	}
	
	private void updateDatabaseTime(String column)
	{
		//String time=String.valueOf(System.currentTimeMillis());//milliseconds since jan 1st 1970
		DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("gmt"));
		String gmtTime = df.format(new Date());
		databaseHelper.runQuery(writableDb, "UPDATE "+databaseHelper.PROPERTIES_TABLE+" SET "+column+"=\""+gmtTime+"\" WHERE _id=\""+String.valueOf(DatabaseHelper.VERSION)+"\"");
		String[][] times=databaseHelper.runSelectQuery(readableDb, databaseHelper.PROPERTIES_TABLE, new String[] {column}, null, null, null, null, null, null);
		//Toast.makeText(context, times[0][0], Toast.LENGTH_SHORT).show();
	}
	
}
