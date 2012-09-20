package com.charaza;

import com.charaza.resources.CharazaCanvas;
import com.charaza.resources.CharazaData;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

public class CharazaActivity extends Activity
{
	private CharazaCanvas canvas;
	private int profileId;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		canvas=new CharazaCanvas(this);
		this.setContentView(canvas);
		
		//initialise resources
		Bundle bundle=this.getIntent().getExtras();
        this.profileId=bundle.getInt("profileId");
		
		//add +1 to charazwad value of profile
		new PostCharazwadThread().execute(0);
	}
	private class PostCharazwadThread extends AsyncTask<Integer, Integer, Boolean>
    {

		@Override
		protected Boolean doInBackground(Integer... params) 
		{
			boolean result = CharazaData.updateCharazwadValue(profileId);
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result)
		{
			//TODO: do something if charazwadValue is not updated
			super.onPostExecute(result);
		}
    }
}
