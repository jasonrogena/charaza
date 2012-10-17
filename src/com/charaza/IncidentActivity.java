package com.charaza;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.R;
import com.devspark.sidenavigation.SideNavigationView;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class IncidentActivity extends SherlockActivity implements View.OnClickListener, ISideNavigationCallback, View.OnTouchListener
{
	private SideNavigationView sideNavigationView;
	private ScrollView incidentActivityScrollView;
	private RelativeLayout incidentActivityRelativeLayout;
	private RelativeLayout incidentActivityMainLayout;
	private Button incidentActivityCommentButton;
	private Button incidentActivityCharazaButton;
	private TextView incidentActivityName;
	
	private float previousY;
	private int networkCheckStatus=0;
	private int incidentId;
	private String incidentText;
	private int commentButtonStatus=1;//0 is for hidden
	private int buttonAnimationTime;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incident_activity);
        
        incidentActivityMainLayout=(RelativeLayout)this.findViewById(R.id.incidentActivityMainLayout);
        incidentActivityRelativeLayout=(RelativeLayout)this.findViewById(R.id.incidentActivityRelativeLayout);
        incidentActivityScrollView=(ScrollView)this.findViewById(R.id.incidentActivityScrollView);
        incidentActivityScrollView.setOnTouchListener(this);
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
        incidentActivityRelativeLayout.setMinimumHeight(minHeight);
        
        sideNavigationView=(SideNavigationView)this.findViewById(R.id.side_navigation_view_incident_activity);
		sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
		sideNavigationView.setMenuClickCallback(this);
		this.setTitle(R.string.profileActivitySubTitle);//TODO: change this to something else if it doesnt work
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		sideNavigationView.setMinimumHeight(minHeight+5);
		
		incidentActivityCommentButton=(Button)this.findViewById(R.id.incidentActivityCommentButton);
		//incidentActivityCommentButton.setOnTouchListener(this);
		incidentActivityCommentButton.setOnClickListener(this);
		incidentActivityCommentButton.setOnTouchListener(this);
		incidentActivityCharazaButton=(Button)this.findViewById(R.id.incidentActivityCharazaButton);
		incidentActivityCharazaButton.setOnTouchListener(this);
		incidentActivityCharazaButton.setOnClickListener(this);
		incidentActivityScrollView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,minHeight+5-incidentActivityCharazaButton.getLayoutParams().height-incidentActivityCommentButton.getLayoutParams().height));
		incidentActivityName=(TextView)this.findViewById(R.id.incidentActivityName);
		
		previousY=0;
		buttonAnimationTime=200;
		Bundle bundle=this.getIntent().getExtras();
		networkCheckStatus=bundle.getInt("networkCheckStatus");
		incidentActivityName.setText(bundle.getString("profileText"));
		incidentId=bundle.getInt("incidentId");
		incidentText=bundle.getString("incidentText");
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) 
    {
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

	@Override
	public boolean onTouch(View view, MotionEvent event)
	{
		if(view==incidentActivityCommentButton)
		{
			if(event.getAction()==MotionEvent.ACTION_DOWN)
			{
				incidentActivityCommentButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonFocusedColor));
			}
			else if(event.getAction()==MotionEvent.ACTION_UP)
			{
				incidentActivityCommentButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonBackground));
				incidentActivityCommentButtonClicked();
			}
			/*else
			{
				incidentActivityCommentButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonBackground));
			}*/
		}
		else if(view==incidentActivityCharazaButton)
		{
			if(event.getAction()==MotionEvent.ACTION_DOWN)
			{
				hideCommentButton();
				incidentActivityCharazaButton.setBackgroundColor(getResources().getColor(R.color.normalButtonFocusedBackgroundColor));
				incidentActivityCharazaButton.setTextColor(getResources().getColor(R.color.normalButtonFocusedTextColor));
			}
			else if(event.getAction()==MotionEvent.ACTION_UP)
			{
				showCommentButton();
				incidentActivityCharazaButton.setBackgroundColor(getResources().getColor(R.color.normalButtonBackgroundColor));
				incidentActivityCharazaButton.setTextColor(getResources().getColor(R.color.normalButtonTextColor));
				incidentActivityCharazaButtonClicked();
			}
			/*else
			{
				showCommentButton();
				incidentActivityCharazaButton.setBackgroundColor(getResources().getColor(R.color.normalButtonBackgroundColor));
				incidentActivityCharazaButton.setTextColor(getResources().getColor(R.color.normalButtonTextColor));
			}*/
		}
		return true;
	}
	
	private void incidentActivityCommentButtonClicked()
	{
		incidentActivityCommentButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonBackground));
	}
	
	private void incidentActivityCharazaButtonClicked()
	{
		incidentActivityCharazaButton.setBackgroundColor(getResources().getColor(R.color.normalButtonBackgroundColor));
		incidentActivityCharazaButton.setTextColor(getResources().getColor(R.color.normalButtonTextColor));
	}
	
	private void showCommentButton()
	{
		if(commentButtonStatus==0)
		{
			Animation showButtonAnimation=new ScaleAnimation((float)1, (float)1, (float)0, (float)1, Animation.RELATIVE_TO_SELF, (float)1, Animation.RELATIVE_TO_SELF, (float)1);
			showButtonAnimation.setDuration(buttonAnimationTime);
			showButtonAnimation.setAnimationListener(new Animation.AnimationListener() 
			{
				
				@Override
				public void onAnimationStart(Animation animation) 
				{
					incidentActivityCommentButton.setVisibility(Button.VISIBLE);
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) 
				{
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationEnd(Animation animation) 
				{
					
				}
			});
			incidentActivityCommentButton.clearAnimation();
			incidentActivityCommentButton.startAnimation(showButtonAnimation);
			commentButtonStatus=1;
		}
	}
	
	private void hideCommentButton()
	{
		if(commentButtonStatus==1)
		{
			Animation hideButtonAnimation=new ScaleAnimation((float)1, (float)1, (float)1, (float)0, Animation.RELATIVE_TO_SELF, (float)1, Animation.RELATIVE_TO_SELF, (float)1);
			hideButtonAnimation.setDuration(buttonAnimationTime);
			hideButtonAnimation.setAnimationListener(new Animation.AnimationListener() 
			{
				
				@Override
				public void onAnimationStart(Animation animation) 
				{
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) 
				{
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationEnd(Animation animation) 
				{
					incidentActivityCommentButton.setVisibility(Button.GONE);
				}
			});
			incidentActivityCommentButton.clearAnimation();
			incidentActivityCommentButton.startAnimation(hideButtonAnimation);
			commentButtonStatus=0;
		}
	}

	@Override
	public void onSideNavigationItemClick(int itemId)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View view) 
	{
		if(view==incidentActivityCommentButton)
		{
			
		}
	}
}
