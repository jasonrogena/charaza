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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends SherlockActivity implements View.OnClickListener, ISideNavigationCallback, View.OnTouchListener
{
	private SideNavigationView sideNavigationView;
	private RelativeLayout profileActivityRelativeLayout;
	private ScrollView profileActivityScrollView;
	private RelativeLayout profileActivityMainLayout;
	private ProgressBar profileActivityProgressBar;
	
	private CharazaData charazaData;
	private int profileId;
	private TextView profileActivityName;
	private TextView profileActivityCharazwad;
	private TextView profileActivityAliasPost;
	private Button charazaButton;
	private int incidentShowAnimationTime;
	private int networkCheckStatus=0;//flag showing all other activities that the user has already been notified that there is no connection to internet
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        
        //initialise views
        profileActivityScrollView=(ScrollView)this.findViewById(R.id.profileActivityScrollView);
        profileActivityRelativeLayout=(RelativeLayout)this.findViewById(R.id.profileActivityRelativeLayout);
        profileActivityMainLayout=(RelativeLayout)this.findViewById(R.id.profileActivityMainLayout);
        int minHeight=0;
        Display display=this.getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        if(metrics.densityDpi==DisplayMetrics.DENSITY_XHIGH)
        {
        	minHeight=display.getHeight()-150;
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
        	minHeight=display.getHeight()-24-34;
        }
        else
        {
        	minHeight=display.getHeight()-150;
        }
        profileActivityRelativeLayout.setMinimumHeight(minHeight);
        
        sideNavigationView=(SideNavigationView)this.findViewById(R.id.side_navigation_view_profile_activity);
		sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
		sideNavigationView.setMenuClickCallback(this);
		this.setTitle(R.string.profileActivitySubTitle);
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		sideNavigationView.setMinimumHeight(minHeight+5);
        
        profileActivityName=(TextView)this.findViewById(R.id.profileActivityName);
        profileActivityCharazwad=(TextView)this.findViewById(R.id.profileActivityCharazwad);
        profileActivityAliasPost=(TextView)this.findViewById(R.id.profileActivityAliasPost);
        charazaButton=(Button)this.findViewById(R.id.charazaButton);
        charazaButton.setOnClickListener(this);
        charazaButton.setOnTouchListener(this);
        profileActivityScrollView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,minHeight+5-charazaButton.getLayoutParams().height));
        profileActivityProgressBar=(ProgressBar)this.findViewById(R.id.profileActivityProgressBar);
        
        //initialise resources
        incidentShowAnimationTime=180;
        charazaData=new CharazaData(this);
        Bundle bundle=this.getIntent().getExtras();
        this.profileId=bundle.getInt("profileId");
        networkCheckStatus=bundle.getInt("networkCheckStatus");
        
        //check network connection
        if(!charazaData.checkNetworkConnection() && networkCheckStatus==0)
        {
        	charazaData.networkError();
        	networkCheckStatus=1;
        }
        
        //access server
        //Thread thread=new Thread(new Initialiser());
        //thread.run();
        //new GetProfileThread().execute(0);
        new GetIncidentsThread().execute(0);
        profileActivityProgressBar.setVisibility(ProgressBar.VISIBLE);
    }
    
    @Override
    protected void onResume()
    {
       super.onResume();
       new GetProfileThread().execute(0);
    }
    
    @Override
    protected void onDestroy ()
    {
    	Log.d("profileActivity database", "database about to be closed by onDestroy()");
    	charazaData.closeDatabase();
    	super.onDestroy();
    }
    
    /*@Override
	protected void onPause() 
    {
    	charazaData.closeDatabase();
		super.onPause();
	}*/

    @Override
	public boolean onOptionsItemSelected(MenuItem item) 
    {
    	/*profileActivityScrollView.post(new Runnable() 
		{

	        @Override
	        public void run() 
	        {
	        	profileActivityScrollView.fullScroll(ScrollView.FOCUS_UP);
	        }
	    });*/
    	InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE); 
    	if(this.getCurrentFocus()!=null)
    	{
    		inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    	}
    	switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			//sideNavigationView.toggleMenu();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
    
    private void setProfile(String name, String charazwad, String post)
    {
    	if(name.equals(null)||name.equals(""))
    	{
    		profileActivityName.setText("Unknown person");
    	}
    	else
    	{
    		profileActivityName.setText(name);
    	}
    	
    	profileActivityCharazwad.setText(charazwad);
    	//Log.d("profile post", post);
    	profileActivityAliasPost.setText(post);
    }
    
    public void addIncidents(String[][] incidents)
    {
    	int count=0;
    	Log.d("profile incidents", String.valueOf(incidents.length));
    	while(count<incidents.length)
    	{
    		TextView incidentTime=new TextView(this);
    		incidentTime.setId(23442+count);
    		incidentTime.setText(incidents[count][2]);
    		RelativeLayout.LayoutParams incidentTimeLayoutParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
    		
    		if(count==0)
    		{
    			incidentTimeLayoutParams.addRule(RelativeLayout.BELOW,R.id.profileActivityAliasPost);
    		}
    		
    		else
    		{
    			incidentTimeLayoutParams.addRule(RelativeLayout.BELOW,4562+count-1);
    		}
    		Display display=this.getWindowManager().getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int incidentTimeLayoutParamsTopMargin=0;
            int incidentTimeSideMargin=0;
            int incidentTimeTextSize=0;
            int incidentTextLayoutParamsTopMargin=0;
            int incidentTextSideMargin=0;
            int incidentTextSize=0;
            if(metrics.densityDpi==DisplayMetrics.DENSITY_XHIGH)
            {
            	if(count==0)
            	{
            		incidentTimeLayoutParamsTopMargin = 40; 
            	}
            	else
            	{
            		incidentTimeLayoutParamsTopMargin = 32; 
            	}
            	incidentTimeSideMargin=13;
            	incidentTimeTextSize=13;
            	incidentTextLayoutParamsTopMargin=7;
            	incidentTextSideMargin=14;
            	incidentTextSize=15;
            }
            else if(metrics.densityDpi==DisplayMetrics.DENSITY_HIGH)
            {
            	if(count==0)
            	{
            		incidentTimeLayoutParamsTopMargin = 35; 
            	}
            	else
            	{
            		incidentTimeLayoutParamsTopMargin = 27; 
            	}
            	incidentTimeSideMargin=11;
            	incidentTimeTextSize=13;
            	incidentTextLayoutParamsTopMargin=7;
            	incidentTextSideMargin=12;
            	incidentTextSize=15;
            }
            else if(metrics.densityDpi==DisplayMetrics.DENSITY_MEDIUM)
            {
            	if(count==0)
            	{
            		incidentTimeLayoutParamsTopMargin = 25; 
            	}
            	else
            	{
            		incidentTimeLayoutParamsTopMargin = 15; 
            	}
            	incidentTimeSideMargin=10;
            	incidentTimeTextSize=12;
            	incidentTextLayoutParamsTopMargin=5;
            	incidentTextSideMargin=10;
            	incidentTextSize=14;
            }
            else if(metrics.densityDpi==DisplayMetrics.DENSITY_LOW)
            {
            	if(count==0)
            	{
            		incidentTimeLayoutParamsTopMargin = 28; 
            	}
            	else
            	{
            		incidentTimeLayoutParamsTopMargin = 16; 
            	}
            	incidentTimeSideMargin=7;
            	incidentTimeTextSize=12;
            	incidentTextLayoutParamsTopMargin=6;
            	incidentTextSideMargin=7;
            	incidentTextSize=14;
            }
            else
            {
            	if(count==0)
            	{
            		incidentTimeLayoutParamsTopMargin = 40; 
            	}
            	else
            	{
            		incidentTimeLayoutParamsTopMargin = 32; 
            	}
            	incidentTimeSideMargin=13;
            	incidentTimeTextSize=13;
            	incidentTextLayoutParamsTopMargin=7;
            	incidentTextSideMargin=14;
            	incidentTextSize=15;
            }
    		incidentTimeLayoutParams.topMargin=incidentTimeLayoutParamsTopMargin;
    		incidentTimeLayoutParams.leftMargin=incidentTimeSideMargin;
    		incidentTimeLayoutParams.rightMargin=incidentTimeSideMargin;
    		incidentTime.setTextColor(getResources().getColor(R.color.incidentTimeTextColor));
    		incidentTime.setTextSize(incidentTimeTextSize);
    		incidentTime.setLayoutParams(incidentTimeLayoutParams);
    		profileActivityRelativeLayout.addView(incidentTime);
    		Animation showTimeAnimation=new ScaleAnimation((float)1, (float)1, (float)0, (float)1, Animation.RELATIVE_TO_SELF, (float)0, Animation.RELATIVE_TO_SELF, (float)0);
			showTimeAnimation.setDuration(incidentShowAnimationTime);
			showTimeAnimation.setStartOffset(count*incidentShowAnimationTime);
			incidentTime.clearAnimation();
			incidentTime.startAnimation(showTimeAnimation);
    		
    		TextView incidentText=new TextView(this);
    		incidentText.setId(4562+count);
    		incidentText.setText(incidents[count][1]);
    		RelativeLayout.LayoutParams incidentTextLayoutParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    		/*if(count==0)
    		{
    			incidentTextLayoutParams.addRule(RelativeLayout.BELOW, R.id.profileActivityAliasPost);
    			incidentTextLayoutParams.topMargin=12;
    		}
    		else
    		{
    			incidentTextLayoutParams.addRule(RelativeLayout.BELOW, 4562+count-1);//TODO: check if this works
    			incidentTextLayoutParams.topMargin=18;
    		}*/
    		incidentTextLayoutParams.addRule(RelativeLayout.BELOW,incidentTime.getId());
    		incidentTextLayoutParams.topMargin=incidentTextLayoutParamsTopMargin;
    		incidentTextLayoutParams.leftMargin=incidentTextSideMargin;
    		incidentTextLayoutParams.rightMargin=incidentTextSideMargin;
    		incidentText.setTextColor(getResources().getColor(R.color.normalTextColor));
    		incidentText.setTextSize(incidentTextSize);
    		incidentText.setLayoutParams(incidentTextLayoutParams);
    		final int incidentId=Integer.parseInt(incidents[count][0]);
    		final String iT=incidents[count][1];
    		incidentText.setOnTouchListener(new View.OnTouchListener()
    		{
				
				@Override
				public boolean onTouch(View v, MotionEvent event)
				{
					if(event.getAction()==MotionEvent.ACTION_UP)
					{
						Intent intent=new Intent(ProfileActivity.this, IncidentActivity.class);
						//charazaData.closeDatabase();
						intent.putExtra("networkCheckStatus", networkCheckStatus);
						intent.putExtra("profileText", profileActivityName.getText().toString());
						intent.putExtra("incidentId", incidentId);
						intent.putExtra("incidentText", iT);
						startActivity(intent);
					}
					return true;
				}
			});
    		profileActivityRelativeLayout.addView(incidentText);
    		Animation showIncidentAnimation=new ScaleAnimation((float)1, (float)1, (float)0, (float)1, Animation.RELATIVE_TO_SELF, (float)0, Animation.RELATIVE_TO_SELF, (float)0);
    		showIncidentAnimation.setDuration(incidentShowAnimationTime);
    		showIncidentAnimation.setStartOffset(count*incidentShowAnimationTime+incidentShowAnimationTime/2);
			incidentText.clearAnimation();
			incidentText.startAnimation(showIncidentAnimation);
    		
    		/*View incidentBackground=new View(this);
    		incidentBackground.setId(45532+count);
    		int incidentBackGroundLayoutTopMargin=27;
    		int incidentBackgrounHeight=(incidentTimeLayoutParamsTopMargin-incidentBackGroundLayoutTopMargin)+incidentTime.getLayoutParams().height+incidentTextLayoutParamsTopMargin+incidentText.getLayoutParams().height+(incidentTimeLayoutParamsTopMargin-incidentBackGroundLayoutTopMargin);
    		RelativeLayout.LayoutParams incidentBackgroundLayoutParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,incidentBackgrounHeight);
    		if(count==0)
    		{
    			incidentBackgroundLayoutParams.addRule(RelativeLayout.BELOW, R.id.profileActivityAliasPost);
    		}
    		else
    		{
    			incidentBackgroundLayoutParams.addRule(RelativeLayout.BELOW, 4562+count-1);
    		}
    		incidentBackgroundLayoutParams.topMargin=incidentBackGroundLayoutTopMargin;
    		incidentBackgroundLayoutParams.leftMargin=getResources().getDimensionPixelSize(R.dimen.sideMargin);
    		incidentBackgroundLayoutParams.rightMargin=getResources().getDimensionPixelSize(R.dimen.sideMargin);
    		incidentBackground.setBackgroundColor(getResources().getColor(R.color.profileIncidentBackgroundColor));
    		incidentBackground.setLayoutParams(incidentBackgroundLayoutParams);
    		profileActivityRelativeLayout.addView(incidentBackground);*/
    		
    		count++;
    	}
    }
    
    private class GetIncidentsThread extends AsyncTask<Integer, Integer, String[][]>
    {

		@Override
		protected String[][] doInBackground(Integer... params) 
		{
			String[][] incidents=charazaData.getIncidents(profileId);
			//Log.d("incidents", "incidents fetched "+String.valueOf(incidents.length));
			return incidents;
		}

		@Override
		protected void onPostExecute(String[][] result) 
		{
			if(result!=null)
			{
				profileActivityProgressBar.setVisibility(ProgressBar.GONE);
				addIncidents(result);
			}
			else
			{
				Log.e("getIncidents()", "getIncidents() returned null");
			}
			super.onPostExecute(result);
		}
    	
    }
    
    private class GetProfileThread extends AsyncTask<Integer, Integer, String[]>
    {

		@Override
		protected String[] doInBackground(Integer... params)
		{
			String[] profile=charazaData.getProfile(profileId);
			return profile;
		}

		@Override
		protected void onPostExecute(String[] result)
		{
			if(result!=null)
			{
				setProfile(result[0], result[2], charazaData.getPost(Integer.parseInt(result[1])));
			}
			else
			{
				Log.e("getProfile()", "getProfile() returned null");
			}
			super.onPostExecute(result);
		}
    	
    }
    
    /*private class Initialiser implements Runnable
    {
		@Override
		public void run() 
		{
			String[][] incidents=charazaData.getIncidents(profileId);
			if(incidents!=null)
			{
				addIncidents(incidents);
			}
			
			String[] profile=charazaData.getProfile(profileId);
			Log.d("profile post id", profile[1]);
			setProfile(profile[0], profile[2],"");// charazaData.getPost(Integer.parseInt(profile[1])));
		}
    	
    }*/

	@Override
	public void onClick(View v) 
	{
		if(v==charazaButton)
		{
			charazaButtonClicked();
		}
	}
	
	private void charazaButtonClicked()
	{
		charazaButton.setBackgroundColor(getResources().getColor(R.color.normalButtonBackgroundColor));
		charazaButton.setTextColor(getResources().getColor(R.color.normalButtonTextColor));
		Intent intent=new Intent(ProfileActivity.this, CharazaActivity.class);
		//charazaData.closeDatabase();
		intent.putExtra("profileId", profileId);
		startActivity(intent);
	}

	@Override
	public void onSideNavigationItemClick(int itemId)
	{
		if(itemId==R.id.mulikaSideNavigation)
		{
			Intent intent=new Intent(ProfileActivity.this, Mulika.class);
			intent.putExtra("networkCheckStatus", networkCheckStatus);
			//charazaData.closeDatabase();
			startActivity(intent);
		}
		else if(itemId==R.id.ranksSideNavigation)
		{
			Intent intent=new Intent(ProfileActivity.this, Ranks.class);
			intent.putExtra("networkCheckStatus", networkCheckStatus);
			//charazaData.closeDatabase();
			startActivity(intent);
		}
		else if(itemId==R.id.latestSideNavigation)
		{
			Intent intent=new Intent(ProfileActivity.this, Latest.class);
			intent.putExtra("networkCheckStatus", networkCheckStatus);
			startActivity(intent);
		}
		else if(itemId==R.id.aboutSideNavigation)
		{
			Intent intent=new Intent(ProfileActivity.this, About.class);
			intent.putExtra("networkCheckStatus", networkCheckStatus);
			startActivity(intent);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		if(v==charazaButton)
		{
			if(event.getAction()==MotionEvent.ACTION_DOWN)
			{
				charazaButton.setBackgroundColor(getResources().getColor(R.color.normalButtonFocusedBackgroundColor));
				charazaButton.setTextColor(getResources().getColor(R.color.normalButtonFocusedTextColor));
			}
			else if(event.getAction()==MotionEvent.ACTION_UP)
			{
				charazaButton.setBackgroundColor(getResources().getColor(R.color.normalButtonBackgroundColor));
				charazaButton.setTextColor(getResources().getColor(R.color.normalButtonTextColor));
				charazaButtonClicked();
			}
			/*else
			{
				charazaButton.setBackgroundColor(getResources().getColor(R.color.normalButtonBackgroundColor));
				charazaButton.setTextColor(getResources().getColor(R.color.normalButtonTextColor));
			}*/
		}
		return true;
	}
}
