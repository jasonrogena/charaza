package com.charaza.resources;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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
	public static final String DATE_FORMAT="yyyy-MM-dd HH:mm:ss";
	private Context context;
	private SQLiteDatabase readableDb;
	private SQLiteDatabase writableDb;
	private DatabaseHelper databaseHelper;
	private static int httpPostTimout=20000;
	private static int httpResponseTimout=20000;
	//public static String baseURL="http://10.0.2.2/~jason/charaza";
	public static String baseURL="http://charaza.zxq.net";

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
	
	public boolean isFirstTimeMulika()
	{
		if(writableDb.isOpen())
		{
			Log.d("writable database", "writable db is open");
			String selection="_id=1";
			String[] columns={"mulika","ranks","latest","profile"};
			String[][] result=databaseHelper.runSelectQuery(writableDb, databaseHelper.INSTRUCTIONS_TABLE, columns, selection, null, null, null, null, null);
			if(result!=null && result.length>0 && result[0]!=null && result[0].length>3)
			{
				if(result[0][0].equals("0"))//this is the first time
				{
					Log.d("first time", "this is the first time"+result[0][0]);
					databaseHelper.runDeleteQuery(writableDb, databaseHelper.INSTRUCTIONS_TABLE, "1");
					databaseHelper.runInsertQuery(databaseHelper.INSTRUCTIONS_TABLE, new String[]{"_id","mulika","ranks","latest","profile"}, new String[]{"1","1",result[0][1],result[0][2],result[0][3]}, writableDb);
					return true;
				}
				{
					Log.d("first time", "this is not the first time");
					return false;
				}
			}
		}
		return true;
	}
	public boolean isFirstTimeRanks()
	{
		if(writableDb.isOpen())
		{
			Log.d("writable database", "writable db is open");
			String selection="_id=1";
			String[] columns={"mulika","ranks","latest","profile"};
			String[][] result=databaseHelper.runSelectQuery(writableDb, databaseHelper.INSTRUCTIONS_TABLE, columns, selection, null, null, null, null, null);
			if(result!=null && result.length>0 && result[0]!=null && result[0].length>3)
			{
				if(result[0][1].equals("0"))//this is the first time
				{
					Log.d("first time", "this is the first time"+result[0][1]);
					databaseHelper.runDeleteQuery(writableDb, databaseHelper.INSTRUCTIONS_TABLE, "1");
					databaseHelper.runInsertQuery(databaseHelper.INSTRUCTIONS_TABLE, new String[]{"_id","mulika","ranks","latest","profile"}, new String[]{"1",result[0][0],"1",result[0][2],result[0][3]}, writableDb);
					return true;
				}
				{
					Log.d("first time", "this is not the first time");
					return false;
				}
			}
		}
		return true;
	}
	public boolean isFirstTimeLatest()
	{
		if(writableDb.isOpen())
		{
			Log.d("writable database", "writable db is open");
			String selection="_id=1";
			String[] columns={"mulika","ranks","latest","profile"};
			String[][] result=databaseHelper.runSelectQuery(writableDb, databaseHelper.INSTRUCTIONS_TABLE, columns, selection, null, null, null, null, null);
			if(result!=null && result.length>0 && result[0]!=null && result[0].length>3)
			{
				if(result[0][2].equals("0"))//this is the first time
				{
					Log.d("first time", "this is the first time"+result[0][2]);
					databaseHelper.runDeleteQuery(writableDb, databaseHelper.INSTRUCTIONS_TABLE, "1");
					databaseHelper.runInsertQuery(databaseHelper.INSTRUCTIONS_TABLE, new String[]{"_id","mulika","ranks","latest","profile"}, new String[]{"1",result[0][0],result[0][1],"1",result[0][3]}, writableDb);
					return true;
				}
				{
					Log.d("first time", "this is not the first time");
					return false;
				}
			}
		}
		return true;
	}
	public boolean isFirstTimeProfile()
	{
		if(writableDb.isOpen())
		{
			Log.d("writable database", "writable db is open");
			String selection="_id=1";
			String[] columns={"mulika","ranks","latest","profile"};
			String[][] result=databaseHelper.runSelectQuery(writableDb, databaseHelper.INSTRUCTIONS_TABLE, columns, selection, null, null, null, null, null);
			if(result!=null && result.length>0 && result[0]!=null && result[0].length>3)
			{
				if(result[0][3].equals("0"))//this is the first time
				{
					Log.d("first time", "this is the first time"+result[0][3]);
					databaseHelper.runDeleteQuery(writableDb, databaseHelper.INSTRUCTIONS_TABLE, "1");
					databaseHelper.runInsertQuery(databaseHelper.INSTRUCTIONS_TABLE, new String[]{"_id","mulika","ranks","latest","profile"}, new String[]{"1",result[0][0],result[0][1],result[0][2],"1"}, writableDb);
					return true;
				}
				{
					Log.d("first time", "this is not the first time");
					return false;
				}
			}
		}
		return true;
	}
	
	public String[][] getProfiles()
	{
		if(readableDb.isOpen())
		{
			updateProfiles();
			String[] columns={"_id","name","post","charazwad"};
			if(readableDb.isOpen())
			{
				String[][] result=databaseHelper.runSelectQuery(readableDb, databaseHelper.PROFILE_TABLE, columns, null, null, null, null, null, null);
				return result;
			}
		}
		return null;
	}
	
	public String[][] getAliasTypes()
	{
		if(readableDb.isOpen())
		{
			updateAliasTypes();
			String[] columns={"_id","text"};
			if(readableDb.isOpen())
			{
				String[][] results=databaseHelper.runSelectQuery(readableDb, databaseHelper.ALIAS_TYPE_TABLE, columns, null, null, null, null, null, null);
				return results;
			}
		}
		return null;
	}
	
	public String[][] getPosts()
	{
		if(readableDb.isOpen())
		{
			updatePosts();
			if(readableDb.isOpen())
			{
				String[][] result=databaseHelper.runSelectQuery(readableDb, databaseHelper.POST_TABLE, new String[] {"_id","text"}, null, null, null, null, null, null);
				return result;
			}
		}
		return null;
	}
	
	private String[] getLastUpdated()
	{
		if(readableDb.isOpen())
		{
			//TODO: set the logic for fetching the dates with the biggest _id
			String[][] result=databaseHelper.runSelectQuery(readableDb, databaseHelper.PROPERTIES_TABLE, new String[] {"_id","last_profile_update","last_post_update","last_alias_type_update"},"_id="+String.valueOf(databaseHelper.VERSION) , null, null, null, null, null);
			return result[0];
		}
		return null;
	}
	
	public String getPost(int id)
	{
		if(readableDb.isOpen())
		{
			Log.d("post id again", String.valueOf(id));
			String selection="_id="+String.valueOf(id);
			String[][] result=databaseHelper.runSelectQuery(readableDb, databaseHelper.POST_TABLE, new String[] {"text"}, selection, null, null, null, null, null);
			if(result.length>0)
			{
				return result[0][0];
			}
			else 
			{
				return null;
			}
		}
		
		return null;
	}
	
	public String getAliasType(int id)
	{
		if(readableDb.isOpen())
		{
			String selection="_id="+String.valueOf(id);
			String[][] result=databaseHelper.runSelectQuery(readableDb, databaseHelper.ALIAS_TYPE_TABLE, new String[] {"text"}, selection, null, null, null, null, null);
			return result[0][0];
		}
		return null;
	}
	
	public String[] getProfile(int id)
	{
		if(readableDb.isOpen())
		{
			String selection="_id="+String.valueOf(id);
			String[][] results=databaseHelper.runSelectQuery(readableDb, databaseHelper.PROFILE_TABLE, new String[] {"name","post","charazwad"}, selection, null, null, null, null, null);
			Log.d("profile size", String.valueOf(results[0].length));
			Log.d("profile name", results[0][0]);
			Log.d("profile type", results[0][1]);
			return results[0];
		}
		return null;
	}
	
	public String[][] getLatestProfiles(final int number)
	{
		String[][] profiles=null;
		//Toast.makeText(context, "connecting to server(for profiles)", Toast.LENGTH_SHORT).show();
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, httpPostTimout);
		HttpConnectionParams.setSoTimeout(httpParameters, httpResponseTimout);
		HttpClient httpClient=new DefaultHttpClient(httpParameters);
		HttpPost httpPost=new HttpPost(CharazaData.baseURL+"/getLatestProfiles.php");
		try
		{
			String[] dates=getLastUpdated();//last profile update should be the second item in the array
			if(dates==null)
			{
				dates=new String[1];
				dates[0]="1";
				Log.e("getLastUpdated()", "getLastUpdated() returned null probably because the database is already closed");
			}
			List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("_id", dates[0]));
			nameValuePairs.add(new BasicNameValuePair("number", String.valueOf(number)));
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
						profiles=new String[jsonArray.length()][4];
						int count=0;
						while(count<jsonArray.length())//while((jsonObject=jsonArray.getJSONObject(count))!=null)
						{
							jsonObject=jsonArray.getJSONObject(count);
							profiles[count][0]=jsonObject.getString("_id");
							profiles[count][1]=jsonObject.getString("name");
							profiles[count][2]=jsonObject.getString("dateUpdated");
							profiles[count][3]=jsonObject.getString("now");
							count++;
						}
						//Toast.makeText(context, "updating time", Toast.LENGTH_SHORT).show();
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
		return profiles;
	}
	
	public String[][] getComments(int incident,String time)
	{
		//date should be 'all' if you want to get all comments
		if(time==null||time=="")
		{
			Log.w("getComments()", "time is empty thus method returned null");
			return null;
		}
		else
		{
			if(checkNetworkConnection())
			{
				Log.d("getComments()", "network is good");
				HttpParams httpParameters = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParameters, httpPostTimout);
				HttpConnectionParams.setSoTimeout(httpParameters, httpResponseTimout);
				HttpClient httpClient=new DefaultHttpClient(httpParameters);
				HttpPost httpPost=new HttpPost(CharazaData.baseURL+"/getComments.php");
				try
				{
					String[] dates=getLastUpdated();
					if(dates==null)
					{
						dates=new String[1];
						dates[0]="1";
						Log.e("getLastUpdated()", "getLastUpdated() returned null probably because the database is closed");
					}
					Log.d("time", time);
					List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(2);
					nameValuePairs.add(new BasicNameValuePair("_id", dates[0]));
					nameValuePairs.add(new BasicNameValuePair("incident", String.valueOf(incident)));
					nameValuePairs.add(new BasicNameValuePair("time", time));
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					
					HttpResponse httpResponse=httpClient.execute(httpPost);
					if(httpResponse.getStatusLine().getStatusCode()==200)
					{
						Log.d("getComments()", "response gotten from server");
						HttpEntity httpEntity=httpResponse.getEntity();
						if(httpEntity!=null)
						{
							Log.d("getComments()", "http entry is not null");
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
									Log.d("comments", "new comment added");
									count++;
								}
								return results;
							}
							else
							{
								Log.d("getComments()", "the server replied "+responseString);
							}
						}
					}
				}
				catch(Exception e)
				{
					
				}
			}
		}
		
		return null;
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
				if(dates==null)
				{
					dates=new String[1];
					dates[0]="1";
					Log.e("getLastUpdated()", "getLastUpdated() returned null probably because the database is closed");
				}
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
	
	public boolean postComment(int incident, String comment)
	{
		Log.d("postComment()", comment);
		if(comment==null || comment=="")
		{
			Log.w("postComment()", "the comment was either null or empty. method has been exited and comment not posted");
			return false;
		}
		else
		{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, httpPostTimout);
			HttpConnectionParams.setSoTimeout(httpParameters, httpResponseTimout);
			HttpClient httpClient=new DefaultHttpClient(httpParameters);
			HttpPost httpPost=new HttpPost(CharazaData.baseURL+"/submitComment.php");
			try
			{
				List<NameValuePair> data=new ArrayList<NameValuePair>();
				data.add(new BasicNameValuePair("_id", "1"));
				data.add(new BasicNameValuePair("incident", String.valueOf(incident)));
				data.add(new BasicNameValuePair("comment", comment));
				httpPost.setEntity(new UrlEncodedFormEntity(data));
				httpClient.execute(httpPost);
				
				return true;
			}
			catch(Exception e)
			{
				Log.w("Http connection", "unable to send comment");
			}
		}
		return false;
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
	
	private void updateProfiles()
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
	
	private void updatePosts()
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
	
	private void updateAliasTypes()
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
	
	public static boolean updateCharazwadValue(int profileId)
	{
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, httpPostTimout);
		HttpConnectionParams.setSoTimeout(httpParameters, httpResponseTimout);
		HttpClient httpClient=new DefaultHttpClient(httpParameters);
		HttpPost httpPost=new HttpPost(CharazaData.baseURL+"/updateCharazwadValue.php");
		try
		{
			List<NameValuePair> data=new ArrayList<NameValuePair>();
			data.add(new BasicNameValuePair("_id", "1"));
			data.add(new BasicNameValuePair("profileId", String.valueOf(profileId)));
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
		DateFormat df=new SimpleDateFormat(DATE_FORMAT);
		df.setTimeZone(TimeZone.getTimeZone("gmt"));
		String gmtTime = df.format(new Date());
		databaseHelper.runQuery(writableDb, "UPDATE "+databaseHelper.PROPERTIES_TABLE+" SET "+column+"=\""+gmtTime+"\" WHERE _id=\""+String.valueOf(DatabaseHelper.VERSION)+"\"");
		String[][] times=databaseHelper.runSelectQuery(readableDb, databaseHelper.PROPERTIES_TABLE, new String[] {column}, null, null, null, null, null, null);
		//Toast.makeText(context, times[0][0], Toast.LENGTH_SHORT).show();
	}
	
}
