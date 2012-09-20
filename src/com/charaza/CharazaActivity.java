package com.charaza;

import com.charaza.resources.CharazaCanvas;
import com.charaza.resources.CharazaData;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;

public class CharazaActivity extends Activity
{
	private CharazaCanvas canvas;
	private int profileId;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Display display=this.getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
		canvas=new CharazaCanvas(this,metrics);
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
