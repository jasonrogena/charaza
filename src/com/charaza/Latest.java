package com.charaza;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.charaza.resources.CharazaData;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.R;
import com.devspark.sidenavigation.SideNavigationView;

import java.text.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class Latest extends SherlockActivity implements ISideNavigationCallback, View.OnTouchListener
{
	//private static final int SWIPE_MIN_DISTANCE=70;//initially 120
	//private static final int SWIPE_MAX_OFF_PATH=250;
	//private static final int SWIPE_THRESHOLD_VELOCITY=10;//initially 200
	
	private SideNavigationView sideNavigationView;
	//private GestureDetector gestureDetector;
	//protected View.OnTouchListener gestureListener;
	private ScrollView latestScrollView;
	private RelativeLayout latestRelativeLayout;
	private TableLayout latestTableLayout;
	private ProgressBar latestProgressBar;

	private DisplayMetrics metrics;
	private CharazaData charazaData;
	private int numberOfProfiles;
	private int nameShowAnimationTime;
	private int networkCheckStatus=0;//flag showing all other activities that the user has already been notified that there is no connection to internet
    private Dialog latestInstructionsDialog;
    private Button latestInstructionButton;
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.latest);
        
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
		latestScrollView=(ScrollView)this.findViewById(R.id.latestScrollView);
		//ranksScrollView.setOnTouchListener(gestureListener);
        latestRelativeLayout=(RelativeLayout)this.findViewById(R.id.latestRelativeLayout);
        //ranksRelativeLayout.setOnTouchListener(gestureListener);
        int minHeight=0;
        Display display=this.getWindowManager().getDefaultDisplay();
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        if(metrics.densityDpi==DisplayMetrics.DENSITY_XHIGH)
        {
        	minHeight=display.getHeight()-146;
        }
        else if(metrics.densityDpi==DisplayMetrics.DENSITY_HIGH)
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
        else
        {
        	minHeight=display.getHeight()-146;
        }
		latestRelativeLayout.setMinimumHeight(minHeight);//API level 8-12 require getHeight()
		
		sideNavigationView=(SideNavigationView)this.findViewById(R.id.side_navigation_view_latest);
		sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
		sideNavigationView.setMenuClickCallback(this);
		this.setTitle(R.string.latestSideNavigationTitle);
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//sideNavigationView.setMinimumHeight(minHeight);
		latestProgressBar=(ProgressBar)this.findViewById(R.id.latestProgressBar);
		latestTableLayout=(TableLayout)this.findViewById(R.id.latestTableLayout);
		
		latestInstructionsDialog=new Dialog(this);
		latestInstructionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		latestInstructionsDialog.setContentView(R.layout.latest_instructions);
		WindowManager.LayoutParams latestInstructionsLayoutParams=latestInstructionsDialog.getWindow().getAttributes();
		latestInstructionsLayoutParams.width=WindowManager.LayoutParams.MATCH_PARENT;
		latestInstructionButton=(Button)latestInstructionsDialog.findViewById(R.id.latestInstructionsButton);
		latestInstructionButton.setOnTouchListener(this);
		//latestInstructionsDialog.show();
		
		//initialise utils
		numberOfProfiles=20;
		nameShowAnimationTime=220;
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
        new GetIsFirstTimeLatestTread().execute(0);
		new GetProfilesThread().execute(0);
		latestProgressBar.setVisibility(ProgressBar.VISIBLE);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
        	this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) 
    {
    	/*ranksScrollView.post(new Runnable() 
		{

	        @Override
	        public void run() 
	        {
	        	ranksScrollView.fullScroll(ScrollView.FOCUS_UP);
	        }
	    });*/
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
    	Log.d("latest database","database about to be closed by onDestroy()");
    	charazaData.closeDatabase();
    	super.onDestroy();
    }
    
    public void addProfiles(String[][] profiles) throws ParseException  
    {
    	int count=0;
    	while(count<numberOfProfiles && count<profiles.length)
    	{
    		//Log.d("addprofiles count", String.valueOf(count));
    		final TableRow tableRow=new TableRow(this);
    		tableRow.setId(5534+Integer.parseInt(profiles[count][0]));
    		//DisplayMetrics metrics = this.getResources().getDisplayMetrics();
    		//float dp = this.getResources().getDimension(R.dimen.tableRowHeight);
    		//int pixels = (int) (metrics.density * dp + 0.5f);
    		int tableRowHeight=0;//20dp
    		int tableTextSideMargin=0;//4dp
    		int tableTextSize=0;//14dp
    		if(metrics.densityDpi==DisplayMetrics.DENSITY_XHIGH)
    		{
    			tableRowHeight=58;//initially 30
    			tableTextSideMargin=14;//initially 6
    			tableTextSize=16;//initially 21
    		}
    		else if(metrics.densityDpi==DisplayMetrics.DENSITY_HIGH)
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
    		else
    		{
    			tableRowHeight=58;//initially 30
    			tableTextSideMargin=14;//initially 6
    			tableTextSize=16;//initially 21
    		}
    		tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,tableRowHeight));
    		
    		final TextView profileName=new TextView(this);
    		TableRow.LayoutParams profileNameLayoutParams=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,tableRowHeight);
    		//dp=this.getResources().getDimension(R.dimen.tableTextSideMargin);
    		//pixels=(int)(metrics.density*dp+0.5f);
    		profileNameLayoutParams.setMargins(tableTextSideMargin, 0, 0, 0);//TODO: set left margin to 4dp
    		profileName.setId(5534+Integer.parseInt(profiles[count][0])+222);
    		final int profileId=Integer.parseInt(profiles[count][0]);
    		Log.d("rank table id", String.valueOf(profileId));
    		if(profiles[count][1].equals("")||profiles[count][1].equals(null))
    		{
    			profileName.setText("Unknown person");
    		}
    		else
    		{
    			profileName.setText(profiles[count][1]);
    		}
    		//profileName.setText(profiles[count][1]);
    		profileName.setTextSize(tableTextSize);
    		profileName.setTextColor(this.getResources().getColor(R.color.normalTextColor));
    		profileName.setGravity(Gravity.CENTER_VERTICAL);
    		profileName.setLayoutParams(profileNameLayoutParams);
    		tableRow.addView(profileName);
    		
    		final View rowSeparator=new View(this);
    		rowSeparator.setId(5534+Integer.parseInt(profiles[count][0])+2222);
    		//dp=this.getResources().getDimension(R.dimen.tableRowHeight);
    		//pixels = (int) (metrics.density * dp + 0.5f);
    		//dp=this.getResources().getDimension(R.dimen.tableCellSeparatorWidth);
    		//int pixels2=(int)(metrics.density*dp+0.5f);
    		rowSeparator.setLayoutParams(new TableRow.LayoutParams(1,tableRowHeight));
    		rowSeparator.setBackgroundColor(this.getResources().getColor(R.color.tableSeparatorColor));
    		tableRow.addView(rowSeparator);
    		
    		final TextView profileDate=new TextView(this);
    		//dp = this.getResources().getDimension(R.dimen.tableRowHeight);
    		//pixels = (int) (metrics.density * dp + 0.5f);
    		TableRow.LayoutParams profileDateLayoutParams=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,tableRowHeight);
    		//dp=25f;
    		//pixels=(int)(metrics.density*dp+0.5f);
    		profileDateLayoutParams.setMargins(tableTextSideMargin, 0,tableTextSideMargin,0);
    		profileDate.setId(5534+Integer.parseInt(profiles[count][0])+22222);
    		
    		SimpleDateFormat dateFormat=new SimpleDateFormat(charazaData.DATE_FORMAT);
    		Date date=dateFormat.parse(profiles[count][2]);
    		Date today=dateFormat.parse(profiles[count][3]);
    		long dateDifference=(today.getTime()-date.getTime())/86400000;
    		
    		if(dateDifference>=0&&dateDifference<1)
    		{
    			profileDate.setText("Today");
    		}
    		else if(dateDifference>=1&&dateDifference<2)
    		{
    			profileDate.setText("Yesterday");
    		}
    		else
    		{
    			profileDate.setText(String.valueOf(dateDifference)+" days ago");
    		}
    		profileDate.setTextSize(tableTextSize);
    		profileDate.setTextColor(this.getResources().getColor(R.color.normalTextColor));
    		profileDate.setGravity(Gravity.CENTER_VERTICAL);
    		profileDate.setLayoutParams(profileDateLayoutParams);
    		tableRow.addView(profileDate);
    		
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

						Intent intent=new Intent(Latest.this, ProfileActivity.class);
						intent.putExtra("profileId", profileId);
						intent.putExtra("networkCheckStatus", networkCheckStatus);
						startActivity(intent);
					}
					else if(event.getAction()==MotionEvent.ACTION_CANCEL)
					{
						tableRow.setBackgroundColor(getResources().getColor(R.color.tableRowColor));
					}
					return true;
				}
			});
    		
    		//dp = this.getResources().getDimension(R.dimen.tableRowHeight);
    		//pixels = (int) (metrics.density * dp + 0.5f);
    		Animation showRowAnimation=new ScaleAnimation((float)1, (float)1, (float)0, (float)1, Animation.RELATIVE_TO_SELF, (float)0, Animation.RELATIVE_TO_SELF, (float)0);
			showRowAnimation.setDuration(nameShowAnimationTime);
			showRowAnimation.setStartOffset(count*nameShowAnimationTime);
    		latestTableLayout.addView(tableRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,tableRowHeight));
    		tableRow.clearAnimation();
    		tableRow.startAnimation(showRowAnimation);
    		//profiles[next][0]=null;
    		
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
    
    private class GetIsFirstTimeLatestTread extends AsyncTask<Integer, Integer, Integer>
    {

		@Override
		protected Integer doInBackground(Integer... params)
		{
			if(charazaData.isFirstTimeLatest())
			{
				return 1;//true
			}
			else
			{
				return 0;//false
			}
		}

		@Override
		protected void onPostExecute(Integer result)
		{
			if(result==1)
			{
				latestInstructionsDialog.show();
			}
			super.onPostExecute(result);
		}
    	
    }
    
    private class GetProfilesThread extends AsyncTask<Integer, Integer, String[][]>
    {

		@Override
		protected String[][] doInBackground(Integer... params)
		{
			String[][] profiles=charazaData.getLatestProfiles(numberOfProfiles);
			return profiles;
		}

		@Override
		protected void onPostExecute(String[][] result)
		{
			try 
			{
				if(result!=null)
				{
					addProfiles(result);
					latestProgressBar.setVisibility(ProgressBar.GONE);
				}
				else
				{
					Log.e("getLatestProfile()", "getLatestProfile() returned null");
				}
			} 
			catch (ParseException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			super.onPostExecute(result);
		}
		
    	
    }

	@Override
	public void onSideNavigationItemClick(int itemId) 
	{
		if(itemId==R.id.mulikaSideNavigation)
		{
			Intent intent=new Intent(Latest.this, Mulika.class);
			intent.putExtra("networkCheckStatus", networkCheckStatus);
			startActivity(intent);
		}
		else if(itemId==R.id.ranksSideNavigation)
		{
			Intent intent=new Intent(Latest.this, Ranks.class);
			intent.putExtra("networkCheckStatus", networkCheckStatus);
			startActivity(intent);
		}
		else if(itemId==R.id.latestSideNavigation)
		{
			
		}
		else if(itemId==R.id.aboutSideNavigation)
		{
			Intent intent=new Intent(Latest.this, About.class);
			intent.putExtra("networkCheckStatus", networkCheckStatus);
			startActivity(intent);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		if(v==latestInstructionButton)
		{
			if(event.getAction()==MotionEvent.ACTION_DOWN)
			{
				latestInstructionButton.setBackgroundColor(getResources().getColor(R.color.normalButtonFocusedBackgroundColor));
				latestInstructionButton.setTextColor(getResources().getColor(R.color.normalButtonFocusedTextColor));
			}
			else if(event.getAction()==MotionEvent.ACTION_UP)
			{
				latestInstructionButton.setBackgroundColor(getResources().getColor(R.color.normalButtonBackgroundColor));
				latestInstructionButton.setTextColor(getResources().getColor(R.color.normalButtonTextColor));
				latestInstructionsDialog.dismiss();
			}
			else if(event.getAction()==MotionEvent.ACTION_CANCEL)
			{
				latestInstructionButton.setBackgroundColor(getResources().getColor(R.color.normalButtonBackgroundColor));
				latestInstructionButton.setTextColor(getResources().getColor(R.color.normalButtonTextColor));
			}
		}
		return true;
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