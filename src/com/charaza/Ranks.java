package com.charaza;

import com.charaza.resources.CharazaData;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.R;
import com.devspark.sidenavigation.SideNavigationView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Ranks extends SherlockActivity implements ISideNavigationCallback
{
	//private static final int SWIPE_MIN_DISTANCE=70;//initially 120
	//private static final int SWIPE_MAX_OFF_PATH=250;
	//private static final int SWIPE_THRESHOLD_VELOCITY=10;//initially 200
	
	private SideNavigationView sideNavigationView;
	//private GestureDetector gestureDetector;
	//protected View.OnTouchListener gestureListener;
	private ScrollView ranksScrollView;
	private RelativeLayout ranksRelativeLayout;
	private TableLayout ranksTableLayout;

	private DisplayMetrics metrics;
	private CharazaData charazaData;
	private int networkCheckStatus=0;//flag showing all other activities that the user has already been notified that there is no connection to internet
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranks);
        
        //gesture detection
        /*gestureDetector=new GestureDetector(new MyGestureDetector());
        gestureListener=new View.OnTouchListener() 
        {
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				Log.d("Gestures","touch detected");
				return gestureDetector.onTouchEvent(event);
			}
		};*/
        
        //initialise of views
		ranksScrollView=(ScrollView)this.findViewById(R.id.ranksScrollView);
		//ranksScrollView.setOnTouchListener(gestureListener);
        ranksRelativeLayout=(RelativeLayout)this.findViewById(R.id.ranksRelativeLayout);
        //ranksRelativeLayout.setOnTouchListener(gestureListener);
        int minHeight=0;
        Display display=this.getWindowManager().getDefaultDisplay();
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        if(metrics.densityDpi==DisplayMetrics.DENSITY_HIGH)
        {
        	minHeight=display.getHeight()-48-67;
        }
        else if(metrics.densityDpi==DisplayMetrics.DENSITY_MEDIUM)
        {
        	minHeight=display.getHeight()-32-45;
        }
        else if(metrics.densityDpi==DisplayMetrics.DENSITY_LOW)
        {
        	minHeight=display.getHeight()-24-33;
        }
		ranksRelativeLayout.setMinimumHeight(minHeight);//API level 8-12 require getHeight()
		
		sideNavigationView=(SideNavigationView)this.findViewById(R.id.side_navigation_view_ranks);
		sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
		sideNavigationView.setMenuClickCallback(this);
		this.setTitle(R.string.ranksSideNavigationTitle);
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		sideNavigationView.setMinimumHeight(minHeight);
		
		ranksTableLayout=(TableLayout)this.findViewById(R.id.ranksTableLayout);
		
		//initialise utils
		charazaData=new CharazaData(this);
		Bundle bundle=this.getIntent().getExtras();
		if(bundle!=null)
		{
			networkCheckStatus=bundle.getInt("networkCheckStatus");
		}
		
		//check network connection
        if(!charazaData.checkNetworkConnection() && networkCheckStatus==0)
        {
        	charazaData.networkError();
        	networkCheckStatus=1;
        }
		
		//fetch network data
		new GetProfilesThread().execute(0);
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) 
    {
    	ranksScrollView.fullScroll(0);
    	InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
    	if(this.getCurrentFocus()!=null)
    	{
    		inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    	}
    	switch (item.getItemId()) {
		case android.R.id.home:
			sideNavigationView.toggleMenu();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
    
    @Override
    protected void onDestroy ()
    {
    	charazaData.closeDatabase();
    	super.onDestroy();
    }
    
    public void addProfiles(String[][] profiles)
    {
    	int count=0;
    	Log.d("rank table", "addprofiles called");
    	//TODO: replace 20 with the number of rows that will fill the display
    	while(count<20 || count<profiles.length)
    	{
    		int next=0;
    		int i=0;
    		while(i<profiles.length&&next<profiles.length)//get the largest charazwad value
    		{
    			if(profiles[next][0]!=null)//checks to see if the profile being pointed by next is already a row
    			{
    				if(count==profiles.length-1)//next is pointing at the last profile in the array
    				{
    					break;
    				}
    				else
    				{
    					if(profiles[i][0]!=null && Integer.parseInt(profiles[i][3])>=Integer.parseInt(profiles[next][3]))
            			{
            				next=i;
            			}
    					i++;
    				}
    				
    			}
    			else
    			{
    				next++;
    			}
    			
    		}
    		if(next==profiles.length)
    		{
    			break;
    		}
    		final TableRow tableRow=new TableRow(this);
    		tableRow.setId(1222+Integer.parseInt(profiles[next][0]));
    		//DisplayMetrics metrics = this.getResources().getDisplayMetrics();
    		//float dp = this.getResources().getDimension(R.dimen.tableRowHeight);
    		//int pixels = (int) (metrics.density * dp + 0.5f);
    		int tableRowHeight=0;//20dp
    		int tableTextSideMargin=0;//4dp
    		int tableTextSize=0;//14dp
    		if(metrics.densityDpi==DisplayMetrics.DENSITY_HIGH)
    		{
    			tableRowHeight=44;//initially 30
    			tableTextSideMargin=14;//initially 6
    			tableTextSize=16;//initially 21
    		}
    		else if(metrics.densityDpi==DisplayMetrics.DENSITY_MEDIUM)
    		{
    			tableRowHeight=27;//initially 20
    			tableTextSideMargin=10;//initially 4
    			tableTextSize=15;//initially 14
    		}
    		else if(metrics.densityDpi==DisplayMetrics.DENSITY_LOW)
    		{
    			tableRowHeight=20;//initially 15
    			tableTextSideMargin=6;//initially 3
    			tableTextSize=15;//initially 11
    		}
    		tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,tableRowHeight));
    		
    		final TextView profileName=new TextView(this);
    		TableRow.LayoutParams profileNameLayoutParams=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,tableRowHeight);
    		//dp=this.getResources().getDimension(R.dimen.tableTextSideMargin);
    		//pixels=(int)(metrics.density*dp+0.5f);
    		profileNameLayoutParams.setMargins(tableTextSideMargin, 0, 0, 0);//TODO: set left margin to 4dp
    		profileName.setId(1222+Integer.parseInt(profiles[next][0])+222);
    		final int profileId=Integer.parseInt(profiles[next][0]);
    		Log.d("rank table id", String.valueOf(profileId));
    		profileName.setText(profiles[next][1]);
    		Log.d("rank table", profiles[next][1]+profiles[next][3]);
    		profileName.setTextSize(tableTextSize);
    		profileName.setTextColor(this.getResources().getColor(R.color.normalTextColor));
    		profileName.setGravity(Gravity.CENTER_VERTICAL);
    		profileName.setLayoutParams(profileNameLayoutParams);
    		tableRow.addView(profileName);
    		
    		final View rowSeparator=new View(this);
    		rowSeparator.setId(1222+Integer.parseInt(profiles[next][0])+2222);
    		//dp=this.getResources().getDimension(R.dimen.tableRowHeight);
    		//pixels = (int) (metrics.density * dp + 0.5f);
    		//dp=this.getResources().getDimension(R.dimen.tableCellSeparatorWidth);
    		//int pixels2=(int)(metrics.density*dp+0.5f);
    		rowSeparator.setLayoutParams(new TableRow.LayoutParams(1,tableRowHeight));
    		rowSeparator.setBackgroundColor(this.getResources().getColor(R.color.tableSeparatorColor));
    		tableRow.addView(rowSeparator);
    		
    		final TextView profileCharazwad=new TextView(this);
    		//dp = this.getResources().getDimension(R.dimen.tableRowHeight);
    		//pixels = (int) (metrics.density * dp + 0.5f);
    		TableRow.LayoutParams profileCharazwadLayoutParams=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,tableRowHeight);
    		//dp=25f;
    		//pixels=(int)(metrics.density*dp+0.5f);
    		profileCharazwadLayoutParams.setMargins(tableTextSideMargin, 0, 0, 0);//TODO: set left margin to 4dp
    		profileCharazwad.setId(1222+Integer.parseInt(profiles[next][0])+22222);
    		profileCharazwad.setText(profiles[next][3]);
    		profileCharazwad.setTextSize(tableTextSize);
    		profileCharazwad.setTextColor(this.getResources().getColor(R.color.normalTextColor));
    		profileCharazwad.setGravity(Gravity.CENTER_VERTICAL);
    		profileCharazwad.setLayoutParams(profileCharazwadLayoutParams);
    		tableRow.addView(profileCharazwad);
    		
    		tableRow.setOnTouchListener(new View.OnTouchListener() 
    		{
				
				@Override
				public boolean onTouch(View v, MotionEvent event) 
				{
					if(event.getAction()==MotionEvent.ACTION_DOWN)
					{
						tableRow.setBackgroundColor(getResources().getColor(R.color.tableRowSelectedColor));
						//profileName.setTextColor(getResources().getColor(R.color.normalTextSelectedColor));
						//profileCharazwad.setTextColor(getResources().getColor(R.color.normalTextSelectedColor));
						//rowSeparator.setBackgroundColor(getResources().getColor(R.color.tableSeparatorSelectedColor));
					}
					else if(event.getAction()==MotionEvent.ACTION_UP)
					{
						tableRow.setBackgroundColor(getResources().getColor(R.color.tableRowColor));
						//profileName.setTextColor(getResources().getColor(R.color.normalTextColor));
						//profileCharazwad.setTextColor(getResources().getColor(R.color.normalTextColor));
						//rowSeparator.setBackgroundColor(getResources().getColor(R.color.tableSeparatorColor));

						Intent intent=new Intent(Ranks.this, ProfileActivity.class);
						intent.putExtra("profileId", profileId);
						intent.putExtra("networkCheckStatus", networkCheckStatus);
						startActivity(intent);
					}
					else
					{
						tableRow.setBackgroundColor(getResources().getColor(R.color.tableRowColor));
					}
					return true;
				}
			});
    		
    		//dp = this.getResources().getDimension(R.dimen.tableRowHeight);
    		//pixels = (int) (metrics.density * dp + 0.5f);
    		ranksTableLayout.addView(tableRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,tableRowHeight));
    		profiles[next][0]=null;
    		
    		count++;
    	}
    }
    
    /*private class MyGestureDetector extends SimpleOnGestureListener
	{
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
		{
			Log.d("Gestures", "MyGestureDetector called");
			try
			{
				Log.d("Gestures","calculating gesture");
				if(Math.abs(e1.getY() - e2.getY())>SWIPE_MAX_OFF_PATH)
				{
					return false;
				}
				if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
				{
					Log.d("Gestures", "left swipe detected");
				}
				else if(e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
				{
					Log.d("Gestures", "right swipe detected");
					Intent intent=new Intent(Ranks.this, Mulika.class);
					intent.putExtra("networkCheckStatus", networkCheckStatus);
					startActivity(intent);
				}
			}
			catch (Exception e)
			{
				Log.d("Gestures", "somethine went wrong with the gestures//////////////\n"+e.getMessage());
			}
			return false;
		}
	}*/
    
    private class GetProfilesThread extends AsyncTask<Integer, Integer, String[][]>
    {

		@Override
		protected String[][] doInBackground(Integer... params)
		{
			String[][] profiles=charazaData.getProfiles();
			return profiles;
		}

		@Override
		protected void onPostExecute(String[][] result)
		{
			addProfiles(result);
			super.onPostExecute(result);
		}
		
    	
    }

	@Override
	public void onSideNavigationItemClick(int itemId) 
	{
		if(itemId==R.id.mulikaSideNavigation)
		{
			Intent intent=new Intent(Ranks.this, Mulika.class);
			intent.putExtra("networkCheckStatus", networkCheckStatus);
			startActivity(intent);
		}
		else if(itemId==R.id.ranksSideNavigation)
		{
			//TODO: do something
		}
	}
    
    /*private class Initializer implements Runnable
    {

		@Override
		public void run() 
		{
			String[][] profiles=charazaData.getProfiles();
			addProfiles(profiles);
		}
    	
    }*/
}
